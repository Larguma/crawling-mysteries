package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;

import dev.larguma.crawlingmysteries.Config;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import dev.larguma.crawlingmysteries.particle.ModParticles;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EternalGuardiansBandItem extends Item implements ICurioItem {

  // Kill x hostiles to attune
  private static final int ATTUNEMENT_SOULS = 1000;

  public EternalGuardiansBandItem() {
    super(new Item.Properties().rarity(Rarity.RARE).stacksTo(1));
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardians_band.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardians_band.tooltip.line2"));

    tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
    if (Screen.hasShiftDown()) {
      if (ItemDataHelper.getAttunement(stack) >= 1.0f) {
        tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.attuned"));
      } else {
        tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.attunement"));
        tooltipComponents.add(ItemHelper.buildTooltipBar(ItemDataHelper.getAttunement(stack)));

        tooltipComponents
            .add(Component.translatable("item.crawlingmysteries.eternal_guardians_band.tooltip.attunement_line"));
      }

    } else {
      tooltipComponents.add(Component.translatable("tolltip.crawlingmysteries.press_shift"));
    }

    if (!Config.SERVER.enableTombstone.get()) {
      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.config_disabled"));
    }
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  @Override
  public void curioTick(SlotContext slotContext, ItemStack stack) {
    float currentAttunement = ItemDataHelper.getAttunement(stack);
    if (currentAttunement < 1.0f && slotContext.entity().level().getGameTime() % 20 == 0) {
      Player player = (Player) slotContext.entity();
      int soulsSucked = suckSoulsNearby(player, stack);
      if (soulsSucked > 0) {
        float attunementPerSoul = 1f / ATTUNEMENT_SOULS;
        float totalAttunement = currentAttunement + attunementPerSoul * soulsSucked;
        if (totalAttunement >= 1.0f) {
          ItemDataHelper.setEnabled(stack, true);
        }
        ItemDataHelper.setAttunement(stack, totalAttunement);

      }
    }

    ICurioItem.super.curioTick(slotContext, stack);
  }

  private int suckSoulsNearby(Player player, ItemStack stack) {
    List<Monster> hostiles = player.level().getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(5),
        e -> !player.isAlliedTo(e) && e.getHealth() > 0);

    if (hostiles.isEmpty()) {
      return 0;
    }

    for (Monster hostile : hostiles) {
      hostile.hurt(player.damageSources().magic(), 0.1f);

      if (!player.level().isClientSide()
          && player.level() instanceof ServerLevel serverLevel) {
        for (int i = 0; i < 4; i++) {
          double offsetX = (player.level().random.nextDouble() - 0.5) * 0.8;
          double offsetY = (player.level().random.nextDouble() - 0.3) * 0.6;
          double offsetZ = (player.level().random.nextDouble() - 0.5) * 0.8;

          serverLevel.sendParticles(
              ModParticles.SOUL_SUCKLE.get(),
              hostile.getX() + offsetX,
              hostile.getY(0.5) + offsetY,
              hostile.getZ() + offsetZ,
              1,
              0, 0, 0, 0);
        }
      }
    }

    if (!player.level().isClientSide()) {
      float pitch = 0.8f + player.level().random.nextFloat() * 0.4f;
      float volume = Math.min(0.5f + hostiles.size() * 0.1f, 1f);
      player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
          SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, volume, pitch);
    }

    return hostiles.size();
  }

}

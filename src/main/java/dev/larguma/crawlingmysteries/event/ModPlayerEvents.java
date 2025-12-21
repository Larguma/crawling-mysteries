package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModPlayerEvents {

  @SubscribeEvent
  public static void playerLogin(PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!player.getData(ModDataAttachments.STARTER_RECEIVED)) {
      player.getInventory().add(new ItemStack(ModItems.CRYPTIC_EYE.get()));
      player.setData(ModDataAttachments.STARTER_RECEIVED, true);
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerRespawn(PlayerRespawnEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!event.isEndConquered()) {
      SpellCooldownManager.clearAllCooldowns((ServerPlayer) player);
    }
    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerChangedDimension(PlayerChangedDimensionEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void onLivingDamage(LivingDamageEvent.Pre event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    if (!event.getSource().is(DamageTypeTags.IS_FALL)) {
      return;
    }

    ItemStack horseshoe = ItemHelper.findEquippedTrinket(player, ModItems.LUCKY_HORSESHOE.get());
    if (horseshoe.isEmpty()) {
      return;
    }

    HorseshoeDataComponent component = horseshoe.get(ModDataComponents.HORSESHOE_TIER.get());

    float reduction = component.getFallReduction();

    if (reduction < 1.0f) {
      if (player.getRandom().nextFloat() < reduction) {
        event.setNewDamage(0);
      }
    } else {
      event.setNewDamage(0);
    }
  }
}

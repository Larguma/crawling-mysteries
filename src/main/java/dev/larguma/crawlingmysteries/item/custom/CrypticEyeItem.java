package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import dev.larguma.crawlingmysteries.client.item.CrypticEyeItemRenderer;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.EffectCures;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CrypticEyeItem extends Item implements GeoItem, ICurioItem {

  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  private static final int TOTEMS_FOR_BONUS = 5;
  private static final int MAX_BONUS_TOTEMS = 50;
  private static final int MAX_HEALTH_BONUS = MAX_BONUS_TOTEMS / TOTEMS_FOR_BONUS;

  public CrypticEyeItem() {
    super(new Item.Properties().stacksTo(1).component(ModDataComponents.SPELL_STAGE.get(), 1));
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    int consumed = getTotemsConsumed(stack);
    int spellStage = ItemDataHelper.getSpellStage(stack);
    Spell spell = ModSpells.getSpellFromStage(ModItems.CRYPTIC_EYE.getId().getPath(), spellStage);

    tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.line1"));

    if (consumed < MAX_BONUS_TOTEMS)
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.hungry"));
    else
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.full"));

    tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
    if (Screen.hasShiftDown()) {
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.consumed", consumed)
          .withStyle(ChatFormatting.GOLD));
      if (consumed > 0) {
        tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.bonus",
            getBonus(stack)).withStyle(ChatFormatting.GOLD));
      }

      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
      MutableComponent spellTooltip = Component.translatable("tooltip.crawlingmysteries.spell_stage", spellStage)
          .withStyle(ChatFormatting.GOLD)
          .append(Component.literal(" "))
          .append(Component.translatable("spell.crawlingmysteries." + spell.id()));
      if (ClientSpellCooldownManager.isOnCooldown(spell)) {
        String formattedCooldown = ClientSpellCooldownManager.getRemainingCooldownFormatted(spell);
        spellTooltip.append(Component.literal(" "))
            .append(Component.translatable("tooltip.crawlingmysteries.spell_cooldown", formattedCooldown)
                .withStyle(ChatFormatting.DARK_GRAY));
      }
      tooltipComponents.add(spellTooltip);

    } else {
      tooltipComponents.add(Component.translatable("tolltip.crawlingmysteries.press_shift"));
    }

    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  public boolean consumeTotem(ServerPlayer player, ItemStack stack) {
    Level level = player.level();

    if (!level.isClientSide) {
      ItemStack totemStack = ItemStack.EMPTY;
      if (player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
        totemStack = player.getOffhandItem();
      } else {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
          ItemStack s = player.getInventory().getItem(i);
          if (s.is(Items.TOTEM_OF_UNDYING)) {
            totemStack = s;
            break;
          }
        }
      }

      if (!totemStack.isEmpty()) {
        totemStack.shrink(1);

        if (player instanceof ServerPlayer serverplayer) {
          serverplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
          CriteriaTriggers.USED_TOTEM.trigger(serverplayer, totemStack);
          player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }

        totemEffects(player);
        // TODO: add custom animation
        player.level().broadcastEntityEvent(player, (byte) 35);

        if (!stack.has(ModDataComponents.TOTEMS_CONSUMED))
          stack.set(ModDataComponents.TOTEMS_CONSUMED, 0);
        int consumed = stack.get(ModDataComponents.TOTEMS_CONSUMED) + 1;
        stack.set(ModDataComponents.TOTEMS_CONSUMED, consumed);

        if (consumed == MAX_BONUS_TOTEMS)
          ItemDataHelper.nextSpellStage(stack);

        return true;
      } else {
        player.displayClientMessage(Component.translatable(
            "message.crawlingmysteries.cryptic_eye.no_totem_found_" + level.getRandom().nextInt(5)), true);
        return false;
      }

    }
    return false;
  }

  private void totemEffects(ServerPlayer player) {
    player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
  }

  public void beTotem(ServerPlayer player) {
    player.setHealth(1.0F);
    totemEffects(player);
    // TODO: own animation
    player.displayClientMessage(
        Component.translatable("message.crawlingmysteries.cryptic_eye.be_totem_effect"), true);
    // player.level().broadcastEntityEvent(player, (byte) 35);
  }

  private int getTotemsConsumed(ItemStack stack) {
    if (!stack.has(ModDataComponents.TOTEMS_CONSUMED))
      stack.set(ModDataComponents.TOTEMS_CONSUMED, 0);
    return stack.get(ModDataComponents.TOTEMS_CONSUMED);
  }

  private int getBonus(ItemStack stack) {
    if (!stack.has(ModDataComponents.TOTEMS_CONSUMED))
      stack.set(ModDataComponents.TOTEMS_CONSUMED, 0);
    int consumed = getTotemsConsumed(stack);
    return Math.min((consumed / TOTEMS_FOR_BONUS), MAX_HEALTH_BONUS);
  }

  // #region Curio
  @Override
  public void curioTick(SlotContext slotContext, ItemStack stack) {
    ICurioItem.super.curioTick(slotContext, stack);
  }

  @Override
  public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
      ResourceLocation id, ItemStack stack) {
    Multimap<Holder<Attribute>, AttributeModifier> map = LinkedHashMultimap.create();
    CuriosApi.addSlotModifier(map, "ring", id, 1, AttributeModifier.Operation.ADD_VALUE);

    double bonus = getBonus(stack);
    if (bonus > 0) {
      map.put(Attributes.MAX_HEALTH, new AttributeModifier(
          ResourceLocation.fromNamespaceAndPath("crawlingmysteries",
              "cryptic_eye_health_" + slotContext.identifier() + "_" + slotContext.index()),
          bonus,
          AttributeModifier.Operation.ADD_VALUE));
    }

    return map;
  }
  // #endregion Curio

  // #region Gecko
  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private CrypticEyeItemRenderer renderer;

      @Override
      public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (this.renderer == null)
          this.renderer = new CrypticEyeItemRenderer();
        return this.renderer;
      }
    });
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "Idle", 0, state -> {
      return state.setAndContinue(IDLE_ANIM);
    }));
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  // #endregion Gecko
}

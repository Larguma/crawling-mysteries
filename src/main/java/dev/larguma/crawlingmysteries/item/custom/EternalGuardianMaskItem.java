package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;

import dev.larguma.crawlingmysteries.client.item.EternalGuardianMaskItemRenderer;
import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class EternalGuardianMaskItem extends Item implements GeoItem, ICurioItem {

  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  // Wait x minutes to attune
  private static final int ATTUNEMENT_MINUTES = 360;

  public EternalGuardianMaskItem() {
    super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardian_mask.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardian_mask.tooltip.line2"));

    tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
    if (Screen.hasShiftDown()) {
      if (ItemDataHelper.getAttunement(stack) >= 1.0f) {
        tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.attuned"));
      } else {
        tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.attunement"));
        tooltipComponents.add(ItemHelper.buildTooltipBar(ItemDataHelper.getAttunement(stack)));
      }

      tooltipComponents.add(Component.translatable("tooltip.crawlingmysteries.blank"));
      tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardian_mask.tooltip.spell")
          .append(Component.translatable(
              "tooltip.crawlingmysteries" + (ItemDataHelper.isEnabled(stack) ? ".active" : ".inactive"))));
    } else {
      tooltipComponents.add(Component.translatable("tolltip.crawlingmysteries.press_shift"));
    }

    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  // #region Curio
  @Override
  public void curioTick(SlotContext slotContext, ItemStack stack) {
    float attunementPerTick = 1.0f / (ATTUNEMENT_MINUTES * 60 * 20);
    float attunement = ItemDataHelper.setAttunement(stack, ItemDataHelper.getAttunement(stack) + attunementPerTick);
    int amplifier = 0;

    if (attunement >= 1.0f) {
      amplifier = 3;
    } else if (attunement >= 0.75f) {
      amplifier = 2;
    } else if (attunement >= 0.5f) {
      amplifier = 1;
    }

    if (!slotContext.entity().level().isClientSide && ItemDataHelper.isEnabled(stack)) {
      slotContext.entity()
          .addEffect(new MobEffectInstance(ModMobEffects.SPECTRAL_GAZE, 60, amplifier, false, false, true));
    }

    ICurioItem.super.curioTick(slotContext, stack);
  }

  @Override
  public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
      ResourceLocation id, ItemStack stack) {
    CuriosApi.addModifier(stack, Attributes.ARMOR, id, 4, AttributeModifier.Operation.ADD_VALUE,
        slotContext.identifier());
    return ICurioItem.super.getAttributeModifiers(slotContext, id, stack);
  }

  @Override
  public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
    return true;
  }
  // #endregion Curio

  // #region Gecko
  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private EternalGuardianMaskItemRenderer renderer;

      @Override
      public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (this.renderer == null)
          this.renderer = new EternalGuardianMaskItemRenderer();
        return this.renderer;
      }
    });
  }

  @Override
  public void registerControllers(ControllerRegistrar controllers) {

  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }
  // #endregion Gecko

}

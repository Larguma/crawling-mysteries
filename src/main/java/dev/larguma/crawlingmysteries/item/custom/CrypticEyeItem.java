package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import dev.larguma.crawlingmysteries.client.item.CrypticEyeItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

  public CrypticEyeItem() {
    super(new Item.Properties().stacksTo(1));
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.cryptic_eye.tooltip.line2"));
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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

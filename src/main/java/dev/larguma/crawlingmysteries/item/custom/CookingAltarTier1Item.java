package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import dev.larguma.crawlingmysteries.client.item.CookingAltarTier1ItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CookingAltarTier1Item extends BlockItem implements GeoItem {
  private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

  public CookingAltarTier1Item(Block block, Properties properties) {
    super(block, properties);
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private CookingAltarTier1ItemRenderer renderer;

      @Override
      public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (this.renderer == null)
          this.renderer = new CookingAltarTier1ItemRenderer();
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

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("block.crawlingmysteries.cooking_altar_tier_1.tooltip"));
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }
}

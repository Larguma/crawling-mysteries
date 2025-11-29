package dev.larguma.crawlingmysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;

import dev.larguma.crawlingmysteries.client.item.EternalGuardianHeadItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EternalGuardianHeadItem extends Item implements GeoItem {
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

  public EternalGuardianHeadItem() {
    super(new Item.Properties());
    SingletonGeoAnimatable.registerSyncedAnimatable(this);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
      TooltipFlag tooltipFlag) {
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardian_head.tooltip.line1"));
    tooltipComponents.add(Component.translatable("item.crawlingmysteries.eternal_guardian_head.tooltip.line2"));
    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
  }

  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private EternalGuardianHeadItemRenderer renderer;

      @Override
      public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (this.renderer == null)
          this.renderer = new EternalGuardianHeadItemRenderer();
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

}

package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.client.EternalGuardianHeadItemRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class EternalGuardianHeadItem extends Item implements GeoItem {

  private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
  private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

  public EternalGuardianHeadItem() {
    super(new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(1));
  }

  // #region Base
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardian_head.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardian_head.tooltip.line2"));
    super.appendTooltip(stack, world, tooltip, context);
  }
  // #endregion

  // #region 3d animated
  @Override
  public void createRenderer(Consumer<Object> consumer) {
    consumer.accept(new RenderProvider() {
      private final EternalGuardianHeadItemRenderer renderer = new EternalGuardianHeadItemRenderer();

      @Override
      public BuiltinModelItemRenderer getCustomRenderer() {
        return this.renderer;
      }
    });
  }

  @Override
  public Supplier<Object> getRenderProvider() {
    return renderProvider;
  }

  @Override
  public double getTick(Object itemStack) {
    return RenderUtils.getCurrentTick();
  }

  @Override
  public void registerControllers(ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
  }

  private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {

    return PlayState.CONTINUE;
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return cache;
  }
  // #endregion
}

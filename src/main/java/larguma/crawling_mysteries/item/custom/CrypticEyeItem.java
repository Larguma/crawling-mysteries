package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.client.CrypticEyeItemRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class CrypticEyeItem extends TrinketItem implements GeoItem {

  private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
  private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

  public CrypticEyeItem(Settings settings) {
    super(settings);
  }
 
  @SuppressWarnings("null")
  public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
      LivingEntity entity, UUID uuid) {
    var modifiers = super.getModifiers(stack, slot, entity, uuid);
    // +10% movement speed
    modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        new EntityAttributeModifier(uuid, CrawlingMysteries.MOD_ID + ":movement_speed", 0.1,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    // If the player has access to ring slots, this will give them an extra one
    SlotAttributes.addSlotModifier(modifiers, "hand/ring", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
    return modifiers;
  }

  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling_mysteries.cryptic_eye.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling_mysteries.cryptic_eye.tooltip.line2")
        .formatted(Formatting.ITALIC, Formatting.LIGHT_PURPLE));
    super.appendTooltip(stack, world, tooltip, context);
  }

  @Override
  public void createRenderer(Consumer<Object> consumer) {
    consumer.accept(new RenderProvider() {
      private final CrypticEyeItemRenderer renderer = new CrypticEyeItemRenderer();

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
    tAnimationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
    return PlayState.CONTINUE;
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
   return cache;
  }
}

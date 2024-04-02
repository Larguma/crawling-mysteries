package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.effect.ModEffect;
import larguma.crawling_mysteries.event.KeyInputHandler;
import larguma.crawling_mysteries.item.client.EternalGuardianMaskItemRenderer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
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

public class EternalGuardianMaskItem extends TrinketItem implements GeoItem {

  private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
  private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

  public EternalGuardianMaskItem() {
    super(new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(0).rarity(Rarity.UNCOMMON)
        .maxCount(1));
  }

  // #region Trinkets
  @SuppressWarnings("null")
  public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot,
      LivingEntity entity, UUID uuid) {
    var modifiers = super.getModifiers(stack, slot, entity, uuid);
    modifiers.put(EntityAttributes.GENERIC_ARMOR,
        new EntityAttributeModifier(uuid, CrawlingMysteries.MOD_ID + ":armor", 4,
            EntityAttributeModifier.Operation.ADDITION));
    return modifiers;
  }

  @Override
  public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
    if (!entity.getWorld().isClient() && this.isEnabled(stack)) {
      entity.addStatusEffect(new StatusEffectInstance(ModEffect.SPECTRAL_GAZE, 60, 0, false, false, true));
    }
    super.tick(stack, slot, entity);
  }

  public boolean isEnabled(ItemStack stack) {
    if (!stack.hasNbt()) {
      stack.getOrCreateNbt().putBoolean("Enabled", false);
    }
    return stack.getNbt().getBoolean("Enabled");
  }

  public void toggle(ItemStack stack) {
    stack.getOrCreateNbt().putBoolean("Enabled", !this.isEnabled(stack));
  }
  // #endregion

  // #region Base
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    boolean isEnabled = this.isEnabled(stack);
    boolean isFavorite = false;
    Key key = KeyBindingHelper.getBoundKeyOf(KeyInputHandler.spellKeySlotBotton); // TODO: can be a favorite

    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardian_mask.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling-mysteries.eternal_guardian_mask.tooltip.line2"));
    tooltip.add(Text.translatable("general.crawling-mysteries.tooltip.blank"));
    tooltip.add(Text.translatable("general.crawling-mysteries.tooltip" + (isEnabled ? ".enabled" : ".disabled")));
    tooltip.add(Text.translatable("general.crawling-mysteries.tooltip" + (isFavorite ? ".favorite" : ".not_favorite"),
        key.getLocalizedText()));
    super.appendTooltip(stack, world, tooltip, context);
  }
  // #endregion

  // #region 3d animated
  @Override
  public void createRenderer(Consumer<Object> consumer) {
    consumer.accept(new RenderProvider() {
      private final EternalGuardianMaskItemRenderer renderer = new EternalGuardianMaskItemRenderer();

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

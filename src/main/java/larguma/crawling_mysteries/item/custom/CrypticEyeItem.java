package larguma.crawling_mysteries.item.custom;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketEnums.DropRule;
import dev.emi.trinkets.api.TrinketItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.util.ImplementedInventory;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.client.CrypticEyeItemRenderer;
import larguma.crawling_mysteries.screen.client.SpellSelectMenuScreen;
import larguma.crawling_mysteries.screen.custom.SpellSelectMenuScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
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

public class CrypticEyeItem extends TrinketItem implements GeoItem, ImplementedInventory, NamedScreenHandlerFactory {

  private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
  private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
  public static final int INVENTORY_SIZE = 4;
  private final DefaultedList<ItemStack> items = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

  public CrypticEyeItem() {
    super(new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(0).maxCount(1));
  }

  // #region Trinkets
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

  public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
    return DropRule.KEEP;
  }
  // #endregion

  // #region Base
  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    player.openHandledScreen(this);
    return TypedActionResult.pass(player.getStackInHand(hand));
  }

  @Override
  public Text getDisplayName() {
    return Text.of(this.getName());
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
    return new SpellSelectMenuScreenHandler(syncId, inv, this);
  }

  @Environment(EnvType.CLIENT)
  private static void openSpellSelectMenuScreenScreen(SpellSelectMenuScreenHandler handler, PlayerInventory inventory,
      Text title) {
    MinecraftClient.getInstance().setScreen(new SpellSelectMenuScreen(handler, inventory, title));
  }

  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable("item.crawling-mysteries.cryptic_eye.tooltip.line1"));
    tooltip.add(Text.translatable("item.crawling-mysteries.cryptic_eye.tooltip.line2"));
    tooltip.add(Text.translatable("general.crawling-mysteries.tooltip.blank"));
    super.appendTooltip(stack, world, tooltip, context);
  }
  // #endregion

  // #region Inventory
  @Override
  public DefaultedList<ItemStack> getItems() {
    return items;
  }

  public void readNbt(NbtCompound nbt) {
    Inventories.readNbt(nbt, items);
  }

  public void writeNbt(NbtCompound nbt) {
    Inventories.writeNbt(nbt, items);
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    ImplementedInventory.super.setStack(slot, stack);

    markDirty();
  }
  // #endregion

  // #region 3d animated
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
  // #endregion
}

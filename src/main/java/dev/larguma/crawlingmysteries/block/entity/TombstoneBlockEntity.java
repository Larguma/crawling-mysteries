package dev.larguma.crawlingmysteries.block.entity;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class TombstoneBlockEntity extends BlockEntity implements GeoBlockEntity, Container {

  protected static final RawAnimation DEPLOY_ANIM = RawAnimation.begin();
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  private NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
  private GameProfile tombstoneOwner;
  private int xp;
  private UUID guardianUUID;

  public TombstoneBlockEntity(BlockPos pos, BlockState blockState) {
    super(ModBlockEntities.TOMBSTONE_BE.get(), pos, blockState);
  }

  @Override
  public void registerControllers(ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, this::deployAnimController));

  }

  protected <E extends TombstoneBlockEntity> PlayState deployAnimController(final AnimationState<E> state) {
    return state.setAndContinue(DEPLOY_ANIM);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  @Override
  public double getTick(Object blockEntity) {
    return RenderUtil.getCurrentTick();
  }

  @Override
  public void clearContent() {
    items.clear();
    this.setChanged();
  }

  @Override
  public int getContainerSize() {
    return 150;
  }

  @Override
  public boolean isEmpty() {
    return this.items.stream().allMatch(ItemStack::isEmpty);
  }

  @Override
  public ItemStack getItem(int slot) {
    return this.items.get(slot);
  }

  public NonNullList<ItemStack> getItems() {
    return this.items;
  } 

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
    this.setChanged();
    return stack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    ItemStack stack = ContainerHelper.takeItem(this.items, slot);
    this.setChanged();
    return stack;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    stack.limitSize(this.getMaxStackSize(stack));
    this.items.set(slot, stack);
    this.setChanged();
  }

  public void setItems(NonNullList<ItemStack> items) {
    this.items = items;
    this.setChanged();
  }

  public void setTombstoneOwner(GameProfile gameProfile) {
    this.tombstoneOwner = gameProfile;
    this.setChanged();
  }

  public GameProfile getTombstoneOwner() {
    return this.tombstoneOwner != null ? this.tombstoneOwner
        : new GameProfile(CrawlingMysteries.ELDRICTH_WEAVER_UUID, CrawlingMysteries.ELDRICTH_WEAVER_NAME);
  }

  public boolean hasTombstoneOwner() {
    return this.tombstoneOwner != null;
  }

  public int getXp() {
    return this.xp;
  }

  public void setXp(int xp) {
    this.xp = xp;
    this.setChanged();
  }

  public void setGuardianUUID(UUID guardianUUID) {
    this.guardianUUID = guardianUUID;
    this.setChanged();
  }

  public UUID getGuardianUUID() {
    return this.guardianUUID;
  }

  public boolean hasGuardianUUID() {
    return this.guardianUUID != null;
  }

  @Override
  public void setChanged() {
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

}

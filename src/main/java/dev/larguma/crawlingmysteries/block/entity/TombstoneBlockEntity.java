package dev.larguma.crawlingmysteries.block.entity;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import dev.larguma.crawlingmysteries.util.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
  private UUID guardianUUID;
  private int xp;
  public static final String TOMBSTONE_OWNER_KEY = "tombstone_owner";
  public static final String TOMBSTONE_GUARDIAN_UUID_KEY = "guardian_uuid";
  public static final String TOMBSTONE_XP_KEY = "xp";

  public TombstoneBlockEntity(BlockPos pos, BlockState blockState) {
    super(ModBlockEntities.TOMBSTONE_BE.get(), pos, blockState);
  }

  // #region NBT
  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    super.loadAdditional(tag, registries);
    clearContent();
    ContainerHelper.loadAllItems(tag, this.items, registries);

    this.xp = tag.getInt(TOMBSTONE_XP_KEY);

    if (tag.contains(TOMBSTONE_OWNER_KEY))
      this.tombstoneOwner = NbtHelper.toGameProfile(tag.getCompound(TOMBSTONE_OWNER_KEY));

    if (tag.contains(TOMBSTONE_GUARDIAN_UUID_KEY))
      this.guardianUUID = tag.getUUID(TOMBSTONE_GUARDIAN_UUID_KEY);

  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
    super.saveAdditional(tag, registries);

    ContainerHelper.saveAllItems(tag, this.items, registries);

    tag.putInt(TOMBSTONE_XP_KEY, this.xp);

    if (this.hasTombstoneOwner())
      tag.put(TOMBSTONE_OWNER_KEY, NbtHelper.writeGameProfile(new CompoundTag(), this.tombstoneOwner));

    if (this.hasGuardianUUID())
      tag.putUUID(TOMBSTONE_GUARDIAN_UUID_KEY, this.guardianUUID);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public CompoundTag getUpdateTag(Provider registries) {
    return saveWithoutMetadata(registries);
  }
  // #endregion NBT

  //#region Gecko
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
  //#endregion Gecko

  @Override
  public void clearContent() {
    this.items.clear();
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
    return this.tombstoneOwner;
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
    ;
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
    return Container.stillValidBlockEntity(this, player);
  }

}

package dev.larguma.crawlingmysteries.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import dev.larguma.crawlingmysteries.util.NbtHelper;
import dev.larguma.crawlingmysteries.util.SlottedItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

public class TombstoneBlockEntity extends BlockEntity implements GeoBlockEntity {

  protected static final RawAnimation DEPLOY_ANIM = RawAnimation.begin();
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  private List<SlottedItemStack> slottedItems = new ArrayList<>();
  private GameProfile tombstoneOwner;
  private UUID guardianUUID;
  private int xp;
  public static final String TOMBSTONE_OWNER_KEY = "tombstone_owner";
  public static final String TOMBSTONE_GUARDIAN_UUID_KEY = "guardian_uuid";
  public static final String TOMBSTONE_XP_KEY = "xp";
  public static final String SLOTTED_ITEMS_KEY = "slotted_items";

  public TombstoneBlockEntity(BlockPos pos, BlockState blockState) {
    super(ModBlockEntities.TOMBSTONE_BE.get(), pos, blockState);
  }

  // #region NBT
  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {

    this.slottedItems.clear();
    if (tag.contains(SLOTTED_ITEMS_KEY)) {
      ListTag slottedItemsTag = tag.getList(SLOTTED_ITEMS_KEY, Tag.TAG_COMPOUND);
      for (int i = 0; i < slottedItemsTag.size(); i++) {
        CompoundTag itemTag = slottedItemsTag.getCompound(i);
        SlottedItemStack slottedItem = SlottedItemStack.load(itemTag, registries);
        if (!slottedItem.stack().isEmpty()) {
          this.slottedItems.add(slottedItem);
        }
      }
    }

    super.loadAdditional(tag, registries);
    this.xp = tag.getInt(TOMBSTONE_XP_KEY);

    if (tag.contains(TOMBSTONE_OWNER_KEY))
      this.tombstoneOwner = NbtHelper.toGameProfile(tag.getCompound(TOMBSTONE_OWNER_KEY));

    if (tag.contains(TOMBSTONE_GUARDIAN_UUID_KEY))
      this.guardianUUID = tag.getUUID(TOMBSTONE_GUARDIAN_UUID_KEY);

  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {

    ListTag slottedItemsTag = new ListTag();
    for (SlottedItemStack slottedItem : this.slottedItems) {
      if (!slottedItem.stack().isEmpty()) {
        slottedItemsTag.add(slottedItem.save(registries));
      }
    }
    tag.put(SLOTTED_ITEMS_KEY, slottedItemsTag);

    super.saveAdditional(tag, registries);
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

  // #region Gecko
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
  // #endregion Gecko

  public boolean isEmpty() {
    return this.slottedItems.isEmpty();
  }

  public List<SlottedItemStack> getSlottedItems() {
    return this.slottedItems;
  }

  public void setSlottedItems(List<SlottedItemStack> slottedItems) {
    this.slottedItems = new ArrayList<>(slottedItems);
    this.setChanged();
  }

  public void setTombstoneOwner(GameProfile gameProfile) {
    this.tombstoneOwner = gameProfile;
    this.setChanged();
  }

  public GameProfile getTombstoneOwner() {
    if (this.tombstoneOwner == null) {
      return new GameProfile(UUID.randomUUID(), "Unknown");
    }
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
}

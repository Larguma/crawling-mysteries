package larguma.crawling_mysteries.block.entity;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class TombstoneBlockEntity extends BlockEntity implements Nameable {

  private DefaultedList<ItemStack> items;
  private int xp;
  private GameProfile tombstoneOwner;
  private String customName;
  private UUID guardianUUID;
  public static final String TOMBSTONE_OWNER_KEY = "TombstoneOwner";
  public static final String TOMBSTONE_CUSTOM_NAME_KEY = "CustomName";
  public static final String TOMBSTONE_GUARDIAN_UUID_KEY = "GuardianUUID";
  public static final String TOMBSTONE_XP_KEY = "XP";
  public static final String TOMBSTONE_ITEMS_KEY = "Items";
  public static final String TOMBSTONE_ITEMCOUNT_KEY = "ItemCount";

  public TombstoneBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, pos, state);

    this.tombstoneOwner = null;
    this.xp = 0;
    this.items = DefaultedList.of();
    this.customName = null;
    this.guardianUUID = null;
  }

  public void setItems(DefaultedList<ItemStack> items) {
    this.items = items;
    this.markDirty();
  }

  public DefaultedList<ItemStack> getItems() {
    return items;
  }

  public void setTombstoneOwner(GameProfile gameProfile) {
    this.tombstoneOwner = gameProfile;
    this.markDirty();
  }

  public GameProfile getTombstoneOwner() {
    return tombstoneOwner != null ? tombstoneOwner
        : new GameProfile(CrawlingMysteries.ELDRICTH_WEAVER_UUID, CrawlingMysteries.ELDRICTH_WEAVER_NAME);
  }

  public int getXp() {
    return xp;
  }

  public void setXp(int xp) {
    this.xp = xp;
    this.markDirty();
  }

  public void setCustomName(String customName) {
    this.customName = customName;
    this.markDirty();
  }

  public Text getCustomName() {
    return Text.of(customName);
  }

  public void setGuardianUUID(UUID guardianUUID) {
    this.guardianUUID = guardianUUID;
    this.markDirty();
  }

  public UUID getGuardianUUID() {
    return guardianUUID;
  }

  @Override
  public void readNbt(NbtCompound nbt) {

    this.items = DefaultedList.ofSize(nbt.getInt(TOMBSTONE_ITEMCOUNT_KEY), ItemStack.EMPTY);

    Inventories.readNbt(nbt.getCompound(TOMBSTONE_ITEMS_KEY), this.items);

    this.xp = nbt.getInt(TOMBSTONE_XP_KEY);

    if (nbt.contains(TOMBSTONE_OWNER_KEY))
      this.tombstoneOwner = NbtHelper.toGameProfile(nbt.getCompound(TOMBSTONE_OWNER_KEY));

    if (nbt.contains(TOMBSTONE_CUSTOM_NAME_KEY))
      this.customName = nbt.getString(TOMBSTONE_CUSTOM_NAME_KEY);

    if (nbt.contains(TOMBSTONE_GUARDIAN_UUID_KEY))
      this.guardianUUID = NbtHelper.toUuid(nbt.get(TOMBSTONE_GUARDIAN_UUID_KEY));

    super.readNbt(nbt);
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);

    nbt.putInt(TOMBSTONE_ITEMCOUNT_KEY, this.items.size());

    nbt.put(TOMBSTONE_ITEMS_KEY, Inventories.writeNbt(new NbtCompound(), this.items, true));

    nbt.putInt(TOMBSTONE_XP_KEY, xp);

    if (tombstoneOwner != null)
      nbt.put(TOMBSTONE_OWNER_KEY, NbtHelper.writeGameProfile(new NbtCompound(), tombstoneOwner));

    if (customName != null && !customName.isEmpty())
      nbt.putString(TOMBSTONE_CUSTOM_NAME_KEY, customName);

    if (guardianUUID != null)
      nbt.put(TOMBSTONE_GUARDIAN_UUID_KEY, NbtHelper.fromUuid(guardianUUID));
  }

  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

  @Override
  public Text getName() {
    return this.hasCustomName() ? Text.of(this.customName) : Text.translatable("item.crawling-mysteries.tombstone");
  }

  @Override
  public Text getDisplayName() {
    return this.getName();
  }

  @Override
  public boolean hasCustomName() {
    return this.customName != null;
  }
}

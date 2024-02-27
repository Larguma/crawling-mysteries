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
  private GameProfile tombOwner;
  private String customName;
  private UUID guardianUUID;

  public TombstoneBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, pos, state);

    this.tombOwner = null;
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

  public void setTombOwner(GameProfile gameProfile) {
    this.tombOwner = gameProfile;
    this.markDirty();
  }

  public GameProfile getTombOwner() {
    return tombOwner != null ? tombOwner : new GameProfile(CrawlingMysteries.ELDRICTH_WEAVER_UUID, CrawlingMysteries.ELDRICTH_WEAVER_NAME);
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
  public void readNbt(NbtCompound tag) {
    super.readNbt(tag);

    this.items = DefaultedList.ofSize(tag.getInt("ItemCount"), ItemStack.EMPTY);

    Inventories.readNbt(tag.getCompound("Items"), this.items);

    this.xp = tag.getInt("XP");

    if (tag.contains("TombOwner"))
      this.tombOwner = NbtHelper.toGameProfile(tag.getCompound("TombOwner"));

    if (tag.contains("CustomName"))
      this.customName = tag.getString("CustomName");

    if (tag.contains("GuardianUUID"))
      this.guardianUUID = NbtHelper.toUuid(tag.getCompound("GuardianUUID"));
  }

  @Override
  public void writeNbt(NbtCompound tag) {
    super.writeNbt(tag);

    tag.putInt("ItemCount", this.items.size());

    tag.put("Items", Inventories.writeNbt(new NbtCompound(), this.items, true));

    tag.putInt("XP", xp);

    if (tombOwner != null)
      tag.put("TombOwner", NbtHelper.writeGameProfile(new NbtCompound(), tombOwner));

    if (customName != null && !customName.isEmpty())
      tag.putString("CustomName", customName);

    if (guardianUUID != null)
      tag.put("GuardianUUID", NbtHelper.fromUuid(guardianUUID));
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

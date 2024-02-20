package larguma.crawling_mysteries.block.entity;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class TombstoneBlockEntity extends BlockEntity {

  private DefaultedList<ItemStack> items;
  private int xp;
  private GameProfile tombOwner;
  private String customName;

  public TombstoneBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.TOMBSTONE_BLOCK_ENTITY, pos, state);

    this.customName = "";
    this.tombOwner = null;
    this.xp = 0;
    this.items = DefaultedList.ofSize(41, ItemStack.EMPTY);
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
    return tombOwner;
  }

  public void setCustomName(String text) {
    this.customName = text;
    this.markDirty();
  }

  public String getCustomName() {
    return customName;
  }

  public int getXp() {
    return xp;
  }

  public void setXp(int xp) {
    this.xp = xp;
    this.markDirty();
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
  }

  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

}

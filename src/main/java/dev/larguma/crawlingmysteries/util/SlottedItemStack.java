package dev.larguma.crawlingmysteries.util;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Represents an ItemStack with its original slot information for restoration.
 * Slot type values:
 * - 0-35: Main inventory (items)
 * - 100-103: Armor slots (boots, leggings, chestplate, helmet)
 * - 150: Offhand slot
 * - 200+: Curios slots (uses slotId string for identification)
 */
public record SlottedItemStack(ItemStack stack, int slotType, int slotIndex, String curiosSlotId, boolean isCosmetic) {

    public static final int MAIN_INVENTORY_START = 0;
    public static final int ARMOR_START = 100;
    public static final int OFFHAND_START = 150;
    public static final int CURIOS_START = 200;

    private static final String TAG_SLOT_TYPE = "SlotType";
    private static final String TAG_SLOT_INDEX = "SlotIndex";
    private static final String TAG_CURIOS_SLOT_ID = "CuriosSlotId";
    private static final String TAG_IS_COSMETIC = "IsCosmetic";

    /**
     * Creates a SlottedItemStack for main inventory items (slots 0-35)
     */
    public static SlottedItemStack forMainInventory(ItemStack stack, int slot) {
        return new SlottedItemStack(stack, MAIN_INVENTORY_START + slot, slot, "", false);
    }

    /**
     * Creates a SlottedItemStack for armor items (slots 100-103)
     */
    public static SlottedItemStack forArmor(ItemStack stack, int slot) {
        return new SlottedItemStack(stack, ARMOR_START + slot, slot, "", false);
    }

    /**
     * Creates a SlottedItemStack for offhand items (slot 150)
     */
    public static SlottedItemStack forOffhand(ItemStack stack, int slot) {
        return new SlottedItemStack(stack, OFFHAND_START + slot, slot, "", false);
    }

    /**
     * Creates a SlottedItemStack for curios items
     */
    public static SlottedItemStack forCurios(ItemStack stack, String slotId, int slotIndex, boolean isCosmetic) {
        return new SlottedItemStack(stack, CURIOS_START, slotIndex, slotId, isCosmetic);
    }

    /**
     * Checks if this is a main inventory slot
     */
    public boolean isMainInventory() {
        return slotType >= MAIN_INVENTORY_START && slotType < ARMOR_START;
    }

    /**
     * Checks if this is an armor slot
     */
    public boolean isArmor() {
        return slotType >= ARMOR_START && slotType < OFFHAND_START;
    }

    /**
     * Checks if this is an offhand slot
     */
    public boolean isOffhand() {
        return slotType >= OFFHAND_START && slotType < CURIOS_START;
    }

    /**
     * Checks if this is a curios slot
     */
    public boolean isCurios() {
        return slotType >= CURIOS_START;
    }

    /**
     * Saves this SlottedItemStack to NBT
     */
    public CompoundTag save(Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_SLOT_TYPE, slotType);
        tag.putInt(TAG_SLOT_INDEX, slotIndex);
        if (!curiosSlotId.isEmpty()) {
            tag.putString(TAG_CURIOS_SLOT_ID, curiosSlotId);
        }
        tag.putBoolean(TAG_IS_COSMETIC, isCosmetic);
        return (CompoundTag) stack.save(registries, tag);
    }

    /**
     * Loads a SlottedItemStack from NBT
     */
    public static SlottedItemStack load(CompoundTag tag, Provider registries) {
        int slotType = tag.getInt(TAG_SLOT_TYPE);
        int slotIndex = tag.getInt(TAG_SLOT_INDEX);
        String curiosSlotId = tag.contains(TAG_CURIOS_SLOT_ID) ? tag.getString(TAG_CURIOS_SLOT_ID) : "";
        boolean isCosmetic = tag.getBoolean(TAG_IS_COSMETIC);
        ItemStack stack = ItemStack.parse(registries, tag).orElse(ItemStack.EMPTY);
        return new SlottedItemStack(stack, slotType, slotIndex, curiosSlotId, isCosmetic);
    }
}

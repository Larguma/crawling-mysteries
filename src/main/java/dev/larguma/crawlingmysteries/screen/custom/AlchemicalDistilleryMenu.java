package dev.larguma.crawlingmysteries.screen.custom;

import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import dev.larguma.crawlingmysteries.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AlchemicalDistilleryMenu extends AbstractContainerMenu {
  public static final int GUI_WIDTH = 181;
  public static final int GUI_HEIGHT = 187;

  private static final int SLOT_0_X = 30;
  private static final int SLOT_0_Y = 41 - 16;
  private static final int SLOT_1_X = 83;
  private static final int SLOT_1_Y = 41 - 16;
  private static final int SLOT_2_X = 30;
  private static final int SLOT_2_Y = 76 - 16;
  private static final int SLOT_3_X = 83;
  private static final int SLOT_3_Y = 76 - 16;

  private static final int PLAYER_INV_X = 11;
  private static final int PLAYER_INV_Y = 103;

  private final CookingAltarTier2BlockEntity blockEntity;
  private final Level level;
  private final ContainerData data;

  public static final int DATA_NEEDLE_POSITION = 0;
  public static final int DATA_COOKING_PROGRESS = 1;
  public static final int DATA_COOKING_TIME = 2;
  public static final int DATA_STABILITY_SCORE = 3;
  public static final int DATA_IS_COOKING = 4;
  public static final int DATA_COUNT = 5;

  public AlchemicalDistilleryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
    this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()),
        new SimpleContainerData(DATA_COUNT));
  }

  public AlchemicalDistilleryMenu(int containerId, Inventory playerInventory,
      BlockEntity blockEntity, ContainerData data) {
    super(ModMenuTypes.ALCHEMICAL_DISTILLERY_MENU.get(), containerId);
    this.blockEntity = (CookingAltarTier2BlockEntity) blockEntity;
    this.level = playerInventory.player.level();
    this.data = data;

    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);

    IItemHandler itemHandler = this.blockEntity.getItemHandler();
    this.addSlot(new SlotItemHandler(itemHandler, 0, SLOT_0_X, SLOT_0_Y));
    this.addSlot(new SlotItemHandler(itemHandler, 1, SLOT_1_X, SLOT_1_Y));
    this.addSlot(new SlotItemHandler(itemHandler, 2, SLOT_2_X, SLOT_2_Y));
    this.addSlot(new SlotItemHandler(itemHandler, 3, SLOT_3_X, SLOT_3_Y));

    addDataSlots(data);
  }

  public CookingAltarTier2BlockEntity getBlockEntity() {
    return this.blockEntity;
  }

  public int getNeedlePosition() {
    return this.data.get(DATA_NEEDLE_POSITION);
  }

  public int getCookingProgress() {
    return this.data.get(DATA_COOKING_PROGRESS);
  }

  public int getCookingTime() {
    return this.data.get(DATA_COOKING_TIME);
  }

  public int getStabilityScore() {
    return this.data.get(DATA_STABILITY_SCORE);
  }

  public boolean isCooking() {
    return this.data.get(DATA_IS_COOKING) == 1;
  }

  public boolean hasValidIngredients() {
    IItemHandler handler = this.blockEntity.getItemHandler();
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      if (!isRawMeat(stack)) {
        return false;
      }
    }
    return true;
  }

  private boolean isRawMeat(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    return stack.is(Items.BEEF)
        || stack.is(Items.PORKCHOP)
        || stack.is(Items.CHICKEN)
        || stack.is(Items.MUTTON)
        || stack.is(Items.RABBIT)
        || stack.is(Items.COD)
        || stack.is(Items.SALMON);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    ItemStack quickMovedStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);

    if (slot.hasItem()) {
      ItemStack rawStack = slot.getItem();
      quickMovedStack = rawStack.copy();

      // 0-35 = player, 36-39 = distillery input slots
      if (slotIndex < 36) {
        if (!this.moveItemStackTo(rawStack, 36, 40, false)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (!this.moveItemStackTo(rawStack, 0, 36, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (rawStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return quickMovedStack;
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
        player, blockEntity.getBlockState().getBlock());
  }

  private void addPlayerInventory(Inventory playerInventory) {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
            PLAYER_INV_X + col * 18,
            PLAYER_INV_Y + row * 18));
      }
    }
  }

  private void addPlayerHotbar(Inventory playerInventory) {
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col,
          PLAYER_INV_X + col * 18,
          PLAYER_INV_Y + 58));
    }
  }
}

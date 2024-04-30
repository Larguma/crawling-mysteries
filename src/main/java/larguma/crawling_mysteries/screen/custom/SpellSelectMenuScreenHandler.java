package larguma.crawling_mysteries.screen.custom;

import io.wispforest.owo.client.screens.SlotGenerator;
import larguma.crawling_mysteries.datagen.ModItemTagProvider;
import larguma.crawling_mysteries.screen.ModScreenHandler;
import larguma.crawling_mysteries.util.EquipedSpellData;
import larguma.crawling_mysteries.util.IEntityDataSaver;
import larguma.crawling_mysteries.util.SpellUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class SpellSelectMenuScreenHandler extends ScreenHandler {

  private final static int FAVOURITE_SLOTS = 4;
  private final Inventory inventory;

  public SpellSelectMenuScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(FAVOURITE_SLOTS));
  }

  public SpellSelectMenuScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(ModScreenHandler.SPELL_SELECT_MENU_HANDLER_TYPE, syncId);

    this.inventory = inventory;

    // Favorites
    this.addSlot(new Slot(this.inventory, 0, 82, 12) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModItemTagProvider.SPELL_ITEMS);
      }
    });
    this.addSlot(new Slot(this.inventory, 1, 66, 25) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModItemTagProvider.SPELL_ITEMS);
      }
    });
    this.addSlot(new Slot(this.inventory, 2, 98, 25) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModItemTagProvider.SPELL_ITEMS);
      }
    });
    this.addSlot(new Slot(this.inventory, 3, 82, 38) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModItemTagProvider.SPELL_ITEMS);
      }
    });

    SlotGenerator.begin(this::addSlot, 9, 78)
        .playerInventory(playerInventory);
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int invSlot) {
    return ItemStack.EMPTY;
  }

  @Override
  public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
    boolean isHandEmpty = this.getCursorStack().isEmpty();

    // Handle favourite slots
    if (slotIndex < FAVOURITE_SLOTS && slotIndex >= 0) {
      boolean isFavouriteSlotEmpty = this.inventory.getStack(slotIndex).isEmpty();
      if (isHandEmpty && !isFavouriteSlotEmpty) {
        this.inventory.removeStack(slotIndex);
      } else if (!isHandEmpty && isFavouriteSlotEmpty && this.getCursorStack().isIn(ModItemTagProvider.SPELL_ITEMS)) {
        this.inventory.setStack(slotIndex, this.getCursorStack());
        EquipedSpellData.addSpell((IEntityDataSaver) this.player(), slotIndex, SpellUtils.getSpellId(this.getCursorStack()));
        this.setCursorStack(ItemStack.EMPTY);
      }
      this.inventory.markDirty();
      return;
    }

    // If hand is a spell item, clear the cursor on click
    if (!isHandEmpty && this.getCursorStack().isIn(ModItemTagProvider.SPELL_ITEMS)) {
      this.setCursorStack(ItemStack.EMPTY);
    }

    // If slot is a spell item, allow cloning
    if (slotIndex >= 0 && getSlot(slotIndex).getStack().isIn(ModItemTagProvider.SPELL_ITEMS)) {
      this.setCursorStack(getSlot(slotIndex).getStack().copy());
    } else {
      super.onSlotClick(slotIndex, button, actionType, player);
    }
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }
}

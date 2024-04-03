package larguma.crawling_mysteries.screen.custom;

import io.wispforest.owo.client.screens.SlotGenerator;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.datagen.ModItemTagProvider;
import larguma.crawling_mysteries.screen.ModScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

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

    // TODO: copy (not move) item to fav

    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(invSlot);
    if (slot != null && slot.hasStack()) {
      ItemStack originalStack = slot.getStack();
      newStack = originalStack.copy();
      if (invSlot < this.inventory.size()) {
        if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
        return ItemStack.EMPTY;
      }

      if (originalStack.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }
    }

    return newStack;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }
}

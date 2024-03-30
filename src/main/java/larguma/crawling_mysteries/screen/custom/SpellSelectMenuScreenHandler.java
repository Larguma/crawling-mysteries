package larguma.crawling_mysteries.screen.custom;

import io.wispforest.owo.client.screens.ScreenUtils;
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
  private final Inventory favouriteInventory;

  public SpellSelectMenuScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(FAVOURITE_SLOTS));
  }

  public SpellSelectMenuScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory favouriteInventory) {
    super(ModScreenHandler.SPELL_SELECT_MENU, syncId);

    this.favouriteInventory = favouriteInventory;

    // Favorites
    this.addSlot(new Slot(favouriteInventory, 0, 80, 60));
    this.addSlot(new Slot(favouriteInventory, 1, 65, 75));
    this.addSlot(new Slot(favouriteInventory, 2, 95, 75));
    this.addSlot(new Slot(favouriteInventory, 3, 80, 90));

    for (int i = 0; i < 3; ++i) {
      for (int l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 124 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 182));
    }

  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int invSlot) {
    return ScreenUtils.handleSlotTransfer(this, invSlot, this.favouriteInventory.size());
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.favouriteInventory.canPlayerUse(player);
  }

  public Inventory getFavoriteInventory() {
    return this.favouriteInventory;
  }

}

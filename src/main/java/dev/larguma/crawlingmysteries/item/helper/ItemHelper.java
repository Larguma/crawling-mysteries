package dev.larguma.crawlingmysteries.item.helper;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

/**
 * Helper methods for item-related functionalities.
 */
public class ItemHelper {

  /**
   * Builds a tooltip bar component representing the given fraction.
   * 
   * @param fraction A value between 0 and 1.
   * @return A component representing the fraction as a bar.
   */
  public static Component buildTooltipBar(float fraction) {
    int barLength = 25;
    int filled = Math.round(fraction * barLength);
    int empty = barLength - filled;

    int percentage = Math.round(fraction * 100);
    return Component.literal("")
        .append(Component.literal("￭".repeat(filled)).withStyle(ChatFormatting.DARK_PURPLE))
        .append(Component.literal("･".repeat(empty)).withStyle(ChatFormatting.DARK_GRAY))
        .append(Component.literal(" " + percentage + "%")).withStyle(ChatFormatting.GRAY);
  }

  /**
   * Finds the first equipped trinket item matching the target item.
   * Can be in curios slots or held in main/off hand.
   * 
   * @param player     The player to search trinkets on.
   * @param targetItem The target trinket item to find.
   * @return The first matching equipped trinket item stack, or ItemStack.EMPTY.
   */
  public static ItemStack findEquippedTrinket(Player player, Item targetItem) {
    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isEmpty()) {
      return ItemStack.EMPTY;
    }

    ICuriosItemHandler inventory = curiosHandler.get();
    for (SlotResult slotResult : inventory.findCurios(stack -> stack.is(targetItem))) {
      return slotResult.stack();
    }

    if (player.getMainHandItem().is(targetItem)) {
      return player.getMainHandItem();
    }
    if (player.getOffhandItem().is(targetItem)) {
      return player.getOffhandItem();
    }

    return ItemStack.EMPTY;
  }

  /**
   * Checks if the player has the specified item, either in their main inventory
   * or equipped as a trinket.
   * 
   * @param player The player to check.
   * @param item   The item to look for.
   * @return True if the player has the item, false otherwise.
   */
  public static boolean hasItem(Player player, Item item) {
    // Check curios slots
    Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
    if (curiosInventory.isPresent()) {
      for (SlotResult slot : curiosInventory.get().findCurios(item)) {
        if (!slot.stack().isEmpty()) {
          return true;
        }
      }
    }

    // Check main inventory
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack stack = player.getInventory().getItem(i);
      if (stack.is(item)) {
        return true;
      }
    }
    return false;
  }
}
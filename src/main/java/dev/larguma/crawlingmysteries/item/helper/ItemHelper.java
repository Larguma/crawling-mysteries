package dev.larguma.crawlingmysteries.item.helper;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
   * 
   * @param player     The player to search trinkets on.
   * @param targetItem The target trinket item to find.
   * @return The first matching equipped trinket item stack, or ItemStack.EMPTY.
   */
  public static ItemStack findEquippedTrinket(ServerPlayer player, Item targetItem) {
    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isEmpty()) {
      return ItemStack.EMPTY;
    }

    ICuriosItemHandler inventory = curiosHandler.get();
    for (SlotResult slotResult : inventory.findCurios(stack -> stack.is(targetItem))) {
      return slotResult.stack();
    }

    return ItemStack.EMPTY;
  }
}
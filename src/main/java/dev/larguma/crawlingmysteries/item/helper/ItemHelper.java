package dev.larguma.crawlingmysteries.item.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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
}
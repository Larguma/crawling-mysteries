package dev.larguma.crawlingmysteries.item.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHelper {

  /**
   * Scatters the given item stacks at the given position in the given level.
   */
  public static void scatterItems(Level level, BlockPos pos, NonNullList<ItemStack> stacks) {
    stacks.forEach((stack) -> {
      spawn(level, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), stack);
    });
  }

  /**
   * Spawns the given item stack at the given position in the given level.
   */
  private static void spawn(Level level, double x, double y, double z, ItemStack stack) {
    double d = (double) EntityType.ITEM.getWidth();
    double e = 1.0 - d;
    double f = d / 2.0;
    double g = Math.floor(x) + level.random.nextDouble() * e + f;
    double h = Math.floor(y) + level.random.nextDouble() * e;
    double i = Math.floor(z) + level.random.nextDouble() * e + f;

    while (!stack.isEmpty()) {
      ItemEntity itemEntity = new ItemEntity(level, g, h, i, stack.split(level.random.nextInt(21) + 10));
      itemEntity.setDeltaMovement(level.random.triangle(0.0, 0.11485000171139836),
          level.random.triangle(0.2, 0.11485000171139836), level.random.triangle(0.0, 0.11485000171139836));
      level.addFreshEntity(itemEntity);
    }
  }

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
package dev.larguma.crawlingmysteries.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHelper {

  public static void scatterItems(Level level, BlockPos pos, NonNullList<ItemStack> stacks) {
    stacks.forEach((stack) -> {
      spawn(level, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), stack);
    });
  }

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

}

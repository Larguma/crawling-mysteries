package dev.larguma.crawlingmysteries.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CookingAltarTier1RecipeInput(ItemStack item) implements RecipeInput {

  @Override
  public ItemStack getItem(int index) {
    if (index != 0) {
      throw new IllegalArgumentException("CookingAltarTier1RecipeInput only has one slot");
    }
    return item;
  }

  @Override
  public int size() {
    return 1;
  }
}

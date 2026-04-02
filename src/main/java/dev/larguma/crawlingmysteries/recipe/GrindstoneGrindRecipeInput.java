package dev.larguma.crawlingmysteries.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Input for grindstone grind recipes.
 */
public record GrindstoneGrindRecipeInput(ItemStack item) implements RecipeInput {

  @Override
  public ItemStack getItem(int index) {
    if (index != 0) {
      throw new IllegalArgumentException("GrindstoneGrindRecipeInput only has one slot");
    }
    return item;
  }

  @Override
  public int size() {
    return 1;
  }
}

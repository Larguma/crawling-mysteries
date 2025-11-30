package dev.larguma.crawlingmysteries.datagen;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class ModRecipeProvider extends RecipeProvider {

  public ModRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput recipeOutput) {
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ETERNAL_GUARDIAN_MASK.get())
        .define('l', Items.LEATHER)
        .define('h', ModItems.ETERNAL_GUARDIAN_HEAD)
        .pattern("lhl")
        .unlockedBy("has_leather", has(Items.LEATHER))
        .unlockedBy("has_eternal_guardina_head", has(ModItems.ETERNAL_GUARDIAN_HEAD))
        .save(recipeOutput);
  }

}

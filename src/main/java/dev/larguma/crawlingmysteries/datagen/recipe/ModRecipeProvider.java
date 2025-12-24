package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

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

    buildHorseshoeUpgradeRecipes(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModBlocks.BEER_KEG.get())
        .define('B', ModItems.BEER_BARREL.get())
        .define('I', Items.IRON_INGOT)
        .pattern(" I ")
        .pattern("IBI")
        .pattern(" I ")
        .unlockedBy("has_beer_barrel", has(ModItems.BEER_BARREL.get()))
        .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.BEER_BARREL.get())
        .define('O', Items.OAK_PLANKS)
        .define('H', Items.IRON_BARS)
        .pattern("OOO")
        .pattern("OHO")
        .pattern("OOO")
        .unlockedBy("has_oak_planks", has(Items.OAK_PLANKS))
        .unlockedBy("has_iron_bars", has(Items.IRON_BARS))
        .save(recipeOutput);

    ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModBlocks.BEER_MUG.get())
        .define('B', Tags.Items.BARRELS)
        .define('S', Items.STICK)
        .pattern("BS ")
        .unlockedBy("has_barrel", has(Tags.Items.BARRELS))
        .unlockedBy("has_stick", has(Items.STICK))
        .save(recipeOutput);
  }

  private void buildHorseshoeUpgradeRecipes(RecipeOutput recipeOutput) {
    // Tier X -> Tier 2 (Emeralds)
    DataComponentPatch tier2 = DataComponentPatch.builder()
        .set(ModDataComponents.HORSESHOE_TIER.get(), new HorseshoeDataComponent(2))
        .build();

    ComponentShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LUCKY_HORSESHOE.get(), tier2)
        .define('E', Items.EMERALD)
        .define('H', ModItems.LUCKY_HORSESHOE.get())
        .pattern("EEE")
        .pattern("EHE")
        .pattern("EEE")
        .unlockedBy("has_lucky_horseshoe", has(ModItems.LUCKY_HORSESHOE.get()))
        .unlockedBy("has_emerald", has(Items.EMERALD))
        .save(recipeOutput, CrawlingMysteries.MOD_ID + ":lucky_horseshoe_upgrade_tier_2");

    // Tier X -> Tier 3 (Diamonds)
    DataComponentPatch tier3 = DataComponentPatch.builder()
        .set(ModDataComponents.HORSESHOE_TIER.get(), new HorseshoeDataComponent(3))
        .build();

    ComponentShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LUCKY_HORSESHOE.get(), tier3)
        .define('D', Items.DIAMOND)
        .define('H', ModItems.LUCKY_HORSESHOE.get())
        .pattern("DDD")
        .pattern("DHD")
        .pattern("DDD")
        .unlockedBy("has_lucky_horseshoe", has(ModItems.LUCKY_HORSESHOE.get()))
        .unlockedBy("has_diamond", has(Items.DIAMOND))
        .save(recipeOutput, CrawlingMysteries.MOD_ID + ":lucky_horseshoe_upgrade_tier_3");

    // Tier X -> Tier 4 (Netherite Ingots)
    DataComponentPatch tier4 = DataComponentPatch.builder()
        .set(ModDataComponents.HORSESHOE_TIER.get(), new HorseshoeDataComponent(4))
        .build();

    ComponentShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LUCKY_HORSESHOE.get(), tier4)
        .define('N', Items.NETHERITE_INGOT)
        .define('H', ModItems.LUCKY_HORSESHOE.get())
        .pattern("NNN")
        .pattern("NHN")
        .pattern("NNN")
        .unlockedBy("has_lucky_horseshoe", has(ModItems.LUCKY_HORSESHOE.get()))
        .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
        .save(recipeOutput, CrawlingMysteries.MOD_ID + ":lucky_horseshoe_upgrade_tier_4");
  }

}
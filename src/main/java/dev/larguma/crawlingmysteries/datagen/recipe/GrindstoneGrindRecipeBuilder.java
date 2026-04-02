package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.recipe.GrindstoneGrindRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class GrindstoneGrindRecipeBuilder {
  private final Ingredient input;
  private final ItemStack successOutput;
  private final ItemStack failureOutput;
  private final float successChance;
  private final RecipeCategory category;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  public GrindstoneGrindRecipeBuilder(Ingredient input, ItemStack successOutput, ItemStack failureOutput,
      float successChance, RecipeCategory category) {
    this.input = input;
    this.successOutput = successOutput;
    this.failureOutput = failureOutput;
    this.successChance = successChance;
    this.category = category;
  }

  public static GrindstoneGrindRecipeBuilder grindstoneGrind(Ingredient input, ItemLike successOutput,
      ItemLike failureOutput, float successChance, RecipeCategory category) {
    return new GrindstoneGrindRecipeBuilder(input, new ItemStack(successOutput), new ItemStack(failureOutput),
        successChance, category);
  }

  public static GrindstoneGrindRecipeBuilder grindstoneGrind(Ingredient input, ItemStack successOutput,
      ItemStack failureOutput, float successChance, RecipeCategory category) {
    return new GrindstoneGrindRecipeBuilder(input, successOutput, failureOutput, successChance, category);
  }

  public GrindstoneGrindRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  public void save(RecipeOutput recipeOutput, String id) {
    this.save(recipeOutput, ResourceLocation.parse(id));
  }

  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    GrindstoneGrindRecipe recipe = new GrindstoneGrindRecipe(this.input, this.successOutput, this.failureOutput,
        this.successChance);

    Advancement.Builder advancement = recipeOutput.advancement()
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    this.criteria.forEach(advancement::addCriterion);

    recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }

}

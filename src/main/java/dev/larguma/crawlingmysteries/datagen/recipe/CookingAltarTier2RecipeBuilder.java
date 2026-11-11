package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.larguma.crawlingmysteries.recipe.CookingAltarTier2Recipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CookingAltarTier2RecipeBuilder {
  private final List<Ingredient> inputs;
  private final ItemStack successOutput;
  private final ItemStack failureOutput;
  private final int cookingTime;
  private final RecipeCategory category;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  public CookingAltarTier2RecipeBuilder(List<Ingredient> inputs, ItemStack successOutput, ItemStack failureOutput,
      int cookingTime, RecipeCategory category) {
    this.inputs = inputs;
    this.successOutput = successOutput;
    this.failureOutput = failureOutput;
    this.cookingTime = cookingTime;
    this.category = category;
  }

  public static CookingAltarTier2RecipeBuilder cookingAltarTier2(List<Ingredient> inputs, ItemLike successOutput) {
    return cookingAltarTier2(inputs, new ItemStack(successOutput), new ItemStack(Items.ROTTEN_FLESH), 200,
        RecipeCategory.FOOD);
  }

  public static CookingAltarTier2RecipeBuilder cookingAltarTier2(List<Ingredient> inputs, ItemLike successOutput,
      ItemLike failureOutput, int cookingTime, RecipeCategory category) {
    return new CookingAltarTier2RecipeBuilder(inputs, new ItemStack(successOutput), new ItemStack(failureOutput),
        cookingTime, category);
  }

  public static CookingAltarTier2RecipeBuilder cookingAltarTier2(List<Ingredient> inputs, ItemStack successOutput,
      ItemStack failureOutput, int cookingTime, RecipeCategory category) {
    return new CookingAltarTier2RecipeBuilder(inputs, successOutput, failureOutput, cookingTime, category);
  }

  public CookingAltarTier2RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  public void save(RecipeOutput recipeOutput, String id) {
    this.save(recipeOutput, ResourceLocation.parse(id));
  }

  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    NonNullList<Ingredient> inputList = NonNullList.create();
    inputList.addAll(this.inputs);
    CookingAltarTier2Recipe recipe = new CookingAltarTier2Recipe(inputList, this.successOutput, this.failureOutput,
        this.cookingTime);

    Advancement.Builder advancement = recipeOutput.advancement()
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    this.criteria.forEach(advancement::addCriterion);

    recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }
}

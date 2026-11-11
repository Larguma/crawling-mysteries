package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.recipe.CookingAltarTier1Recipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CookingAltarTier1RecipeBuilder {
  private final Ingredient input;
  private final Ingredient container;
  private final ItemStack result;
  private final int cookingTime;
  private final RecipeCategory category;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  public CookingAltarTier1RecipeBuilder(Ingredient input, Ingredient container, ItemStack result, int cookingTime,
      RecipeCategory category) {
    this.input = input;
    this.container = container;
    this.result = result;
    this.cookingTime = cookingTime;
    this.category = category;
  }

  public static CookingAltarTier1RecipeBuilder cookingAltarTier1(Ingredient input, ItemLike result) {
    return cookingAltarTier1(input, Ingredient.of(Items.BOWL), new ItemStack(result), 100, RecipeCategory.FOOD);
  }

  public static CookingAltarTier1RecipeBuilder cookingAltarTier1(Ingredient input, Ingredient container,
      ItemLike result, int cookingTime, RecipeCategory category) {
    return new CookingAltarTier1RecipeBuilder(input, container, new ItemStack(result), cookingTime, category);
  }

  public static CookingAltarTier1RecipeBuilder cookingAltarTier1(Ingredient input, Ingredient container,
      ItemStack result, int cookingTime, RecipeCategory category) {
    return new CookingAltarTier1RecipeBuilder(input, container, result, cookingTime, category);
  }

  public CookingAltarTier1RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  public void save(RecipeOutput recipeOutput, String id) {
    this.save(recipeOutput, ResourceLocation.parse(id));
  }

  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    CookingAltarTier1Recipe recipe = new CookingAltarTier1Recipe(this.input, this.container, this.result,
        this.cookingTime);

    Advancement.Builder advancement = recipeOutput.advancement()
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    this.criteria.forEach(advancement::addCriterion);

    recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }
}

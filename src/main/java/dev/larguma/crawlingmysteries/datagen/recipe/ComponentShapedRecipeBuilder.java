package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

/**
 * Custom recipe builder that supports data components in results
 * For ingredients with components, use written by hand JSON
 */
public class ComponentShapedRecipeBuilder implements RecipeBuilder {
  private final RecipeCategory category;
  private final Item result;
  private final int count;
  private final DataComponentPatch resultComponents;
  private final Map<Character, Ingredient> key = new LinkedHashMap<>();
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
  private String[] pattern;
  private String group;

  private ComponentShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count,
      DataComponentPatch components) {
    this.category = category;
    this.result = result.asItem();
    this.count = count;
    this.resultComponents = components;
  }

  public static ComponentShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result,
      DataComponentPatch components) {
    return new ComponentShapedRecipeBuilder(category, result, 1, components);
  }

  public static ComponentShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count,
      DataComponentPatch components) {
    return new ComponentShapedRecipeBuilder(category, result, count, components);
  }

  public ComponentShapedRecipeBuilder define(Character symbol, ItemLike item) {
    return define(symbol, Ingredient.of(item));
  }

  public ComponentShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
    if (this.key.containsKey(symbol)) {
      throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
    }
    this.key.put(symbol, ingredient);
    return this;
  }

  public ComponentShapedRecipeBuilder pattern(String pattern) {
    if (this.pattern == null) {
      this.pattern = new String[] { pattern };
    } else {
      String[] newPattern = new String[this.pattern.length + 1];
      System.arraycopy(this.pattern, 0, newPattern, 0, this.pattern.length);
      newPattern[this.pattern.length] = pattern;
      this.pattern = newPattern;
    }
    return this;
  }

  @Override
  public ComponentShapedRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  @Override
  public ComponentShapedRecipeBuilder group(String group) {
    this.group = group;
    return this;
  }

  @Override
  public Item getResult() {
    return this.result;
  }

  @Override
  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    ShapedRecipePattern recipePattern = ShapedRecipePattern.of(this.key, this.pattern);

    Advancement.Builder advancement = recipeOutput.advancement()
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    this.criteria.forEach(advancement::addCriterion);

    ItemStack resultStack = new ItemStack(this.result, this.count);
    resultStack.applyComponents(this.resultComponents);

    ShapedRecipe recipe = new ShapedRecipe(
        this.group == null ? "" : this.group,
        CraftingBookCategory.MISC,
        recipePattern,
        resultStack);

    recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }
}
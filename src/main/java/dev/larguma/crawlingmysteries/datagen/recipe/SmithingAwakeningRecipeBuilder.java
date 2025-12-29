
package dev.larguma.crawlingmysteries.datagen.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.recipe.SmithingAwakeningRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class SmithingAwakeningRecipeBuilder {
  private final Ingredient template;
  private final Ingredient base;
  private final Ingredient addition;
  private final RecipeCategory category;
  private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

  public SmithingAwakeningRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition,
      RecipeCategory category, Item item) {
    this.template = template;
    this.base = base;
    this.addition = addition;
    this.category = category;
  }

  public static SmithingAwakeningRecipeBuilder smithing(Ingredient template, Ingredient base, Ingredient addition,
      RecipeCategory category, Item item) {
    return new SmithingAwakeningRecipeBuilder(template, base, addition, category, item);
  }

  public SmithingAwakeningRecipeBuilder unlocks(String name, Criterion<?> criterion) {
    this.criteria.put(name, criterion);
    return this;
  }

  public void save(RecipeOutput recipeOutput, String id) {
    this.save(recipeOutput, ResourceLocation.parse(id));
  }

  public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    SmithingAwakeningRecipe recipe = new SmithingAwakeningRecipe(this.template, this.base, this.addition);

    Advancement.Builder advancement = recipeOutput.advancement()
        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
        .rewards(AdvancementRewards.Builder.recipe(id))
        .requirements(AdvancementRequirements.Strategy.OR);

    this.criteria.forEach(advancement::addCriterion);

    recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
  }
}

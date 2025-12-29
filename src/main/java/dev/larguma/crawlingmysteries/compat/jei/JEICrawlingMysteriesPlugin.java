package dev.larguma.crawlingmysteries.compat.jei;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.recipe.ModRecipes;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;

@JeiPlugin
public class JEICrawlingMysteriesPlugin implements IModPlugin {

  @Override
  public ResourceLocation getPluginUid() {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "jei_plugin");
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    registration.addRecipeCategories(new GrindstoneGrindCategory(registration.getJeiHelpers().getGuiHelper()));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registration) {
    RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

    List<RecipeHolder<SmithingRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeType.SMITHING).stream()
        .filter(r -> r.value().getSerializer() == ModRecipes.SMITHING_AWAKENING.get()).toList();

    registration.addRecipes(RecipeTypes.SMITHING, recipes);
    registration.addRecipes(GrindstoneGrindCategory.RECIPE_TYPE, Collections.singletonList(new GrindstoneGrindRecipe(
        new ItemStack(ModItems.PETRIFIED_EYE.get()),
        new ItemStack(ModItems.AWAKENED_EYE.get()),
        new ItemStack(Items.GRAVEL))));
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(Items.GRINDSTONE), GrindstoneGrindCategory.RECIPE_TYPE);
  }

}

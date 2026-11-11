package dev.larguma.crawlingmysteries.compat.jei;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.recipe.GrindstoneGrindRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GrindstoneGrindCategory implements IRecipeCategory<GrindstoneGrindRecipe> {

  public static final RecipeType<GrindstoneGrindRecipe> RECIPE_TYPE = RecipeType.create(CrawlingMysteries.MOD_ID,
      "grindstone_grind", GrindstoneGrindRecipe.class);

  private final IDrawable icon;

  public GrindstoneGrindCategory(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GRINDSTONE));
  }

  @Override
  public RecipeType<GrindstoneGrindRecipe> getRecipeType() {
    return RECIPE_TYPE;
  }

  @Override
  public Component getTitle() {
    return Component.translatable("jei.crawlingmysteries.grindstone_grind");
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public int getWidth() {
    return 162;
  }

  @Override
  public int getHeight() {
    return 68;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, GrindstoneGrindRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.INPUT, 10, 26).addIngredients(recipe.getInput());

    int successChancePercent = Math.round(recipe.getSuccessChance() * 100);
    int failureChancePercent = 100 - successChancePercent;

    builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 14)
        .addItemStack(recipe.getSuccessOutput())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip
            .add(Component.translatable("jei.crawlingmysteries.chance", successChancePercent + "%")));

    builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 38)
        .addItemStack(recipe.getFailureOutput())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip
            .add(Component.translatable("jei.crawlingmysteries.chance", failureChancePercent + "%")));
  }

  @Override
  public void draw(GrindstoneGrindRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
      double mouseX, double mouseY) {
    Minecraft minecraft = Minecraft.getInstance();

    this.icon.draw(guiGraphics, 72, 26);
    JeiDrawingHelper.drawHorizontalArrow(guiGraphics, 38, 55, 60, 34);
    JeiDrawingHelper.drawHorizontalArrow(guiGraphics, 98, 115, 120, 34);

    Component text = Component.translatable("jei.crawlingmysteries.right_click");
    int width = minecraft.font.width(text);
    guiGraphics.drawString(minecraft.font, text, (getWidth() - width) / 2, 5, 0xFF808080, false);
  }
}

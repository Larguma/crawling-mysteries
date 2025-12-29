package dev.larguma.crawlingmysteries.compat.jei;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GrindstoneGrindCategory implements IRecipeCategory<GrindstoneGrindRecipe> {

  public static final RecipeType<GrindstoneGrindRecipe> RECIPE_TYPE = RecipeType.create(CrawlingMysteries.MOD_ID,
      "grindstone_grind", GrindstoneGrindRecipe.class);

  private final IDrawable background;
  private final IDrawable icon;
  private final IDrawable arrow;

  public GrindstoneGrindCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createBlankDrawable(160, 60);
    this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.GRINDSTONE));
    this.arrow = guiHelper.createDrawable(ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
        79, 34, 24, 17);
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
  public IDrawable getBackground() {
    return this.background;
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, GrindstoneGrindRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.INPUT, 10, 22)
        .addItemStack(recipe.input());

    builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 10)
        .addItemStack(recipe.output())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          tooltip.add(Component.translatable("jei.crawlingmysteries.chance", "30%"));
        });

    builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 34)
        .addItemStack(recipe.failure())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          tooltip.add(Component.translatable("jei.crawlingmysteries.chance", "70%"));
        });
  }

  @Override
  public void draw(GrindstoneGrindRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
      double mouseX, double mouseY) {
    Minecraft minecraft = Minecraft.getInstance();

    this.icon.draw(guiGraphics, 72, 22);
    this.arrow.draw(guiGraphics, 38, 22);
    this.arrow.draw(guiGraphics, 98, 22);

    Component text = Component.translatable("jei.crawlingmysteries.right_click");
    int width = minecraft.font.width(text);
    guiGraphics.drawString(minecraft.font, text, (160 - width) / 2, 5, 0xFF808080, false);
  }
}

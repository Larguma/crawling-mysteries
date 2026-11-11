package dev.larguma.crawlingmysteries.compat.jei;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.recipe.CookingAltarTier1Recipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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

public class CookingAltarTier1Category implements IRecipeCategory<CookingAltarTier1Recipe> {

  public static final RecipeType<CookingAltarTier1Recipe> RECIPE_TYPE = RecipeType.create(CrawlingMysteries.MOD_ID,
      "cooking_altar_tier_1", CookingAltarTier1Recipe.class);

  private static final int INPUT_SLOT_X = 26;
  private static final int INPUT_SLOT_Y = 25;
  private static final int CAULDRON_X = 74;
  private static final int CAULDRON_Y = 15;
  private static final int CONTAINER_SLOT_X = 120;
  private static final int CONTAINER_SLOT_Y = 6;
  private static final int OUTPUT_SLOT_X = 120;
  private static final int OUTPUT_SLOT_Y = 44;

  private final IDrawable icon;
  private final IDrawableAnimated flame;

  public CookingAltarTier1Category(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
        new ItemStack(ModBlocks.COOKING_ALTAR_TIER_1.get()));
    this.flame = guiHelper.createAnimatedRecipeFlame(100);
  }

  @Override
  public RecipeType<CookingAltarTier1Recipe> getRecipeType() {
    return RECIPE_TYPE;
  }

  @Override
  public Component getTitle() {
    return Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1");
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
  public void setRecipe(IRecipeLayoutBuilder builder, CookingAltarTier1Recipe recipe, IFocusGroup focuses) {
    // Input ingredient
    builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
        .setStandardSlotBackground()
        .addIngredients(recipe.getInput())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step1"));
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step1.desc"));
        });

    // Empty thing
    builder.addSlot(RecipeIngredientRole.INPUT, CONTAINER_SLOT_X, CONTAINER_SLOT_Y)
        .setStandardSlotBackground()
        .addIngredients(recipe.getContainer())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step3"));
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step3.desc"));
        });

    // S tew
    builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
        .setStandardSlotBackground()
        .addItemStack(recipe.getResult())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.result"));
          tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step3.desc"));
        });
  }

  @Override
  public void draw(CookingAltarTier1Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
      double mouseX, double mouseY) {
    Minecraft minecraft = Minecraft.getInstance();

    JeiDrawingHelper.drawHorizontalArrow(guiGraphics, 49, 63, 68, 34);

    this.icon.draw(guiGraphics, CAULDRON_X, CAULDRON_Y);
    this.flame.draw(guiGraphics, CAULDRON_X + 1, CAULDRON_Y + 18);
    String timeStr = String.format("%.1fs", recipe.getCookingTime() / 20.0F);
    int timeWidth = minecraft.font.width(timeStr);
    guiGraphics.drawString(minecraft.font, timeStr, (CAULDRON_X + 10) - (timeWidth / 2), 49, 0xFF0284C7, false);

    JeiDrawingHelper.drawHorizontalArrow(guiGraphics, 95, 109, 114, 34);

    JeiDrawingHelper.drawVerticalArrow(guiGraphics, CONTAINER_SLOT_X + 7, 26, 35, 40);

    // Glowing thinghy
    guiGraphics.fill(OUTPUT_SLOT_X - 2, OUTPUT_SLOT_Y - 2, OUTPUT_SLOT_X + 18, OUTPUT_SLOT_Y + 18, 0x2522C55E);
    guiGraphics.fill(OUTPUT_SLOT_X - 1, OUTPUT_SLOT_Y - 1, OUTPUT_SLOT_X + 17, OUTPUT_SLOT_Y, 0xFF22C55E);
    guiGraphics.fill(OUTPUT_SLOT_X - 1, OUTPUT_SLOT_Y + 16, OUTPUT_SLOT_X + 17, OUTPUT_SLOT_Y + 17, 0xFF22C55E);
    guiGraphics.fill(OUTPUT_SLOT_X - 1, OUTPUT_SLOT_Y, OUTPUT_SLOT_X, OUTPUT_SLOT_Y + 16, 0xFF22C55E);
    guiGraphics.fill(OUTPUT_SLOT_X + 16, OUTPUT_SLOT_Y, OUTPUT_SLOT_X + 17, OUTPUT_SLOT_Y + 16, 0xFF22C55E);
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, CookingAltarTier1Recipe recipe,
      IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    if (mouseX >= CAULDRON_X - 4 && mouseX <= CAULDRON_X + 20 && mouseY >= CAULDRON_Y - 2 && mouseY <= 60) {
      String seconds = String.format("%.1f", recipe.getCookingTime() / 20.0F);
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step2"));
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_1.step2.desc", seconds));
    }
  }
}

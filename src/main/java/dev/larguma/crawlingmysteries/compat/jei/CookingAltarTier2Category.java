package dev.larguma.crawlingmysteries.compat.jei;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.recipe.CookingAltarTier2Recipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CookingAltarTier2Category implements IRecipeCategory<CookingAltarTier2Recipe> {

  public static final RecipeType<CookingAltarTier2Recipe> RECIPE_TYPE = RecipeType.create(CrawlingMysteries.MOD_ID,
      "cooking_altar_tier_2", CookingAltarTier2Recipe.class);

  private static final ResourceLocation DISTILLERY_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "textures/gui/alchemical_distillery.png");

  private static final int SUCCESS_SLOT_X = 138;
  private static final int SUCCESS_SLOT_Y = 8;
  private static final int FAILURE_SLOT_X = 138;
  private static final int FAILURE_SLOT_Y = 44;

  private static final int GAUGE_GREEN_X = 102;
  private static final int GAUGE_GREEN_Y = 4;
  private static final int GAUGE_RED_X = 102;
  private static final int GAUGE_RED_Y = 40;
  private static final int GAUGE_SIZE = 24;

  private final IDrawable icon;

  public CookingAltarTier2Category(IGuiHelper guiHelper) {
    this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
        new ItemStack(ModBlocks.COOKING_ALTAR_TIER_2.get()));
  }

  @Override
  public RecipeType<CookingAltarTier2Recipe> getRecipeType() {
    return RECIPE_TYPE;
  }

  @Override
  public Component getTitle() {
    return Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2");
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
  public void setRecipe(IRecipeLayoutBuilder builder, CookingAltarTier2Recipe recipe, IFocusGroup focuses) {
    int[][] slotPositions = {
        { 6, 16 },
        { 24, 16 },
        { 6, 34 },
        { 24, 34 }
    };

    List<Ingredient> inputs = recipe.getInputs();
    for (int i = 0; i < Math.min(inputs.size(), slotPositions.length); i++) {
      builder.addSlot(RecipeIngredientRole.INPUT, slotPositions[i][0], slotPositions[i][1])
          .setStandardSlotBackground()
          .addIngredients(inputs.get(i))
          .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip
              .add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.ingredient")));
    }

    builder.addSlot(RecipeIngredientRole.OUTPUT, SUCCESS_SLOT_X, SUCCESS_SLOT_Y)
        .setStandardSlotBackground()
        .addItemStack(recipe.getSuccessOutput())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip
            .add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.success")));

    if (!recipe.getFailureOutput().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, FAILURE_SLOT_X, FAILURE_SLOT_Y)
          .setStandardSlotBackground()
          .addItemStack(recipe.getFailureOutput())
          .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip
              .add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.failure")));
    }
  }

  @Override
  public void draw(CookingAltarTier2Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
      double mouseX, double mouseY) {

    JeiDrawingHelper.drawHorizontalArrow(guiGraphics, 46, 61, 66, 33);

    this.icon.draw(guiGraphics, 70, 25);

    // Horizontal
    guiGraphics.fill(86, 31, 91, 32, 0xFF374151);
    guiGraphics.fill(86, 32, 91, 33, 0xFF9CA3AF);
    guiGraphics.fill(86, 33, 91, 34, 0xFF6B7280);
    guiGraphics.fill(86, 34, 91, 35, 0xFF374151);
    // Top branch
    guiGraphics.fill(89, 14, 90, 32, 0xFF14532D);
    guiGraphics.fill(90, 15, 91, 32, 0xFF4ADE80);
    guiGraphics.fill(91, 15, 92, 32, 0xFF22C55E);
    guiGraphics.fill(92, 17, 93, 31, 0xFF14532D);
    drawPointer(guiGraphics, 90, 97, 16, 0xFF22C55E, 0xFF4ADE80, 0xFF14532D, 92, true);
    // Bottom branch
    guiGraphics.fill(89, 34, 90, 54, 0xFF7F1D1D);
    guiGraphics.fill(90, 34, 91, 52, 0xFFF87171);
    guiGraphics.fill(91, 34, 92, 52, 0xFFEF4444);
    guiGraphics.fill(92, 35, 93, 51, 0xFF7F1D1D);
    drawPointer(guiGraphics, 90, 97, 52, 0xFFEF4444, 0xFFF87171, 0xFF7F1D1D, 92, false);

    // Top
    drawMiniGauge(guiGraphics, GAUGE_GREEN_X, GAUGE_GREEN_Y, 0.0F, 0x8022C55E);
    // Bottom
    drawMiniGauge(guiGraphics, GAUGE_RED_X, GAUGE_RED_Y, 110.0F, 0x80EF4444);

    // Green
    drawPointer(guiGraphics, 126, 133, 16, 0xFF22C55E, 0xFF4ADE80, 0xFF14532D);
    // Red
    drawPointer(guiGraphics, 126, 133, 52, 0xFFEF4444, 0xFFF87171, 0xFF7F1D1D);

    // Green
    guiGraphics.fill(SUCCESS_SLOT_X - 2, SUCCESS_SLOT_Y - 2, SUCCESS_SLOT_X + 18, SUCCESS_SLOT_Y + 18, 0x2522C55E);
    guiGraphics.fill(SUCCESS_SLOT_X - 1, SUCCESS_SLOT_Y - 1, SUCCESS_SLOT_X + 17, SUCCESS_SLOT_Y, 0xFF22C55E);
    guiGraphics.fill(SUCCESS_SLOT_X - 1, SUCCESS_SLOT_Y + 16, SUCCESS_SLOT_X + 17, SUCCESS_SLOT_Y + 17, 0xFF22C55E);
    guiGraphics.fill(SUCCESS_SLOT_X - 1, SUCCESS_SLOT_Y, SUCCESS_SLOT_X, SUCCESS_SLOT_Y + 16, 0xFF22C55E);
    guiGraphics.fill(SUCCESS_SLOT_X + 16, SUCCESS_SLOT_Y, SUCCESS_SLOT_X + 17, SUCCESS_SLOT_Y + 16, 0xFF22C55E);
    guiGraphics.fill(SUCCESS_SLOT_X, SUCCESS_SLOT_Y, SUCCESS_SLOT_X + 16, SUCCESS_SLOT_Y + 16, 0x1A22C55E);
    // Red
    guiGraphics.fill(FAILURE_SLOT_X - 2, FAILURE_SLOT_Y - 2, FAILURE_SLOT_X + 18, FAILURE_SLOT_Y + 18, 0x30000000);
    guiGraphics.fill(FAILURE_SLOT_X - 1, FAILURE_SLOT_Y - 1, FAILURE_SLOT_X + 17, FAILURE_SLOT_Y, 0xFFDC2626);
    guiGraphics.fill(FAILURE_SLOT_X - 1, FAILURE_SLOT_Y + 16, FAILURE_SLOT_X + 17, FAILURE_SLOT_Y + 17, 0xFFDC2626);
    guiGraphics.fill(FAILURE_SLOT_X - 1, FAILURE_SLOT_Y, FAILURE_SLOT_X, FAILURE_SLOT_Y + 16, 0xFFDC2626);
    guiGraphics.fill(FAILURE_SLOT_X + 16, FAILURE_SLOT_Y, FAILURE_SLOT_X + 17, FAILURE_SLOT_Y + 16, 0xFFDC2626);
    guiGraphics.fill(FAILURE_SLOT_X, FAILURE_SLOT_Y, FAILURE_SLOT_X + 16, FAILURE_SLOT_Y + 16, 0x301F0505);
  }

  private void drawMiniGauge(GuiGraphics guiGraphics, int x, int y, float needleAngle, int glowColor) {
    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate((float) x, (float) y, 0);
    poseStack.scale(0.5F, 0.5F, 1.0F);

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    // 48x48 dial centered at (138, 44)
    guiGraphics.blit(DISTILLERY_TEXTURE, 0, 0, 114, 20, 48, 48, 256, 256);
    // Needle
    poseStack.pushPose();
    poseStack.translate(24.0F, 24.0F, 0);
    poseStack.mulPose(Axis.ZP.rotationDegrees(needleAngle));
    guiGraphics.blit(DISTILLERY_TEXTURE, -6, -10, 186, 2, 16, 15, 256, 256);
    poseStack.popPose();

    poseStack.popPose();
    // Accent border
    guiGraphics.fill(x - 1, y - 1, x + 25, y, glowColor);
    guiGraphics.fill(x - 1, y + 24, x + 25, y + 25, glowColor);
    guiGraphics.fill(x - 1, y, x, y + 24, glowColor);
    guiGraphics.fill(x + 24, y, x + 25, y + 24, glowColor);
  }

  private void drawPointer(GuiGraphics guiGraphics, int startX, int arrowBaseX, int centerY,
      int mainColor, int highlightColor, int outlineColor, int innerCornerX, boolean isTop) {
    // Top border
    if (!isTop && innerCornerX != -1) {
      guiGraphics.fill(innerCornerX, centerY - 2, arrowBaseX, centerY - 1, outlineColor);
    } else {
      guiGraphics.fill(startX, centerY - 2, arrowBaseX, centerY - 1, outlineColor);
    }

    // Core shaft
    guiGraphics.fill(startX, centerY - 1, arrowBaseX, centerY, highlightColor);
    guiGraphics.fill(startX, centerY, arrowBaseX, centerY + 1, mainColor);

    // Bottom border
    if (isTop && innerCornerX != -1) {
      guiGraphics.fill(innerCornerX, centerY + 1, arrowBaseX, centerY + 2, outlineColor);
    } else {
      guiGraphics.fill(startX, centerY + 1, arrowBaseX, centerY + 2, outlineColor);
    }

    // Arrowhead base
    guiGraphics.fill(arrowBaseX, centerY - 3, arrowBaseX + 2, centerY - 2, outlineColor);
    guiGraphics.fill(arrowBaseX, centerY - 2, arrowBaseX + 2, centerY, highlightColor);
    guiGraphics.fill(arrowBaseX, centerY, arrowBaseX + 2, centerY + 3, mainColor);
    guiGraphics.fill(arrowBaseX, centerY + 3, arrowBaseX + 2, centerY + 4, outlineColor);

    // Taper
    guiGraphics.fill(arrowBaseX + 2, centerY - 2, arrowBaseX + 3, centerY - 1, outlineColor);
    guiGraphics.fill(arrowBaseX + 2, centerY - 1, arrowBaseX + 3, centerY, highlightColor);
    guiGraphics.fill(arrowBaseX + 2, centerY, arrowBaseX + 3, centerY + 2, mainColor);
    guiGraphics.fill(arrowBaseX + 2, centerY + 2, arrowBaseX + 3, centerY + 3, outlineColor);

    // Tip 1
    guiGraphics.fill(arrowBaseX + 3, centerY - 1, arrowBaseX + 4, centerY, outlineColor);
    guiGraphics.fill(arrowBaseX + 3, centerY, arrowBaseX + 4, centerY + 1, highlightColor);
    guiGraphics.fill(arrowBaseX + 3, centerY + 1, arrowBaseX + 4, centerY + 2, outlineColor);

    // Tip 2
    guiGraphics.fill(arrowBaseX + 4, centerY, arrowBaseX + 5, centerY + 1, outlineColor);
  }

  private void drawPointer(GuiGraphics guiGraphics, int startX, int arrowBaseX, int centerY,
      int mainColor, int highlightColor, int outlineColor) {
    drawPointer(guiGraphics, startX, arrowBaseX, centerY, mainColor, highlightColor, outlineColor, -1, true);
  }

  @Override
  public void getTooltip(ITooltipBuilder tooltip, CookingAltarTier2Recipe recipe,
      IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    if (mouseX >= GAUGE_GREEN_X && mouseX <= GAUGE_GREEN_X + GAUGE_SIZE
        && mouseY >= GAUGE_GREEN_Y && mouseY <= GAUGE_GREEN_Y + GAUGE_SIZE) {
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.gauge_success"));
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.gauge_success.desc"));
    } else if (mouseX >= GAUGE_RED_X && mouseX <= GAUGE_RED_X + GAUGE_SIZE
        && mouseY >= GAUGE_RED_Y && mouseY <= GAUGE_RED_Y + GAUGE_SIZE) {
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.gauge_failure"));
      tooltip.add(Component.translatable("jei.crawlingmysteries.cooking_altar_tier_2.gauge_failure.desc"));
    }
  }
}

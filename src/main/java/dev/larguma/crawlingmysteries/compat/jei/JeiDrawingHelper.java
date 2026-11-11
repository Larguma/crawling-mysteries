package dev.larguma.crawlingmysteries.compat.jei;

import net.minecraft.client.gui.GuiGraphics;

public final class JeiDrawingHelper {

  public static final int COLOR_BORDER = 0xFF374151;
  public static final int COLOR_HIGHLIGHT = 0xFF9CA3AF;
  public static final int COLOR_MAIN = 0xFF6B7280;
  public static final int COLOR_SHADOW = 0xFF4B5563;

  private JeiDrawingHelper() {}

  public static void drawHorizontalArrow(GuiGraphics guiGraphics, int startX, int arrowBaseX, int tipX, int centerY) {
    // Shaft
    guiGraphics.fill(startX, centerY - 2, arrowBaseX, centerY - 1, COLOR_BORDER);
    guiGraphics.fill(startX, centerY - 1, arrowBaseX, centerY, COLOR_HIGHLIGHT);
    guiGraphics.fill(startX, centerY, arrowBaseX, centerY + 1, COLOR_MAIN);
    guiGraphics.fill(startX, centerY + 1, arrowBaseX, centerY + 2, COLOR_SHADOW);
    guiGraphics.fill(startX, centerY + 2, arrowBaseX, centerY + 3, COLOR_BORDER);

    // Arrowhead base
    guiGraphics.fill(arrowBaseX, centerY - 3, arrowBaseX + 2, centerY - 2, COLOR_BORDER);
    guiGraphics.fill(arrowBaseX, centerY - 2, arrowBaseX + 2, centerY, COLOR_HIGHLIGHT);
    guiGraphics.fill(arrowBaseX, centerY, arrowBaseX + 2, centerY + 1, COLOR_MAIN);
    guiGraphics.fill(arrowBaseX, centerY + 1, arrowBaseX + 2, centerY + 3, COLOR_SHADOW);
    guiGraphics.fill(arrowBaseX, centerY + 3, arrowBaseX + 2, centerY + 4, COLOR_BORDER);

    // Taper 1
    guiGraphics.fill(arrowBaseX + 2, centerY - 2, arrowBaseX + 3, centerY - 1, COLOR_BORDER);
    guiGraphics.fill(arrowBaseX + 2, centerY - 1, arrowBaseX + 3, centerY, COLOR_HIGHLIGHT);
    guiGraphics.fill(arrowBaseX + 2, centerY, arrowBaseX + 3, centerY + 1, COLOR_MAIN);
    guiGraphics.fill(arrowBaseX + 2, centerY + 1, arrowBaseX + 3, centerY + 2, COLOR_SHADOW);
    guiGraphics.fill(arrowBaseX + 2, centerY + 2, arrowBaseX + 3, centerY + 3, COLOR_BORDER);

    // Taper 2
    guiGraphics.fill(arrowBaseX + 3, centerY - 1, arrowBaseX + 4, centerY, COLOR_BORDER);
    guiGraphics.fill(arrowBaseX + 3, centerY, arrowBaseX + 4, centerY + 1, COLOR_HIGHLIGHT);
    guiGraphics.fill(arrowBaseX + 3, centerY + 1, arrowBaseX + 4, centerY + 2, COLOR_BORDER);

    // Tip
    guiGraphics.fill(arrowBaseX + 4, centerY, tipX, centerY + 1, COLOR_BORDER);
  }

  public static void drawHorizontalArrow(GuiGraphics guiGraphics, int startX, int tipX, int centerY) {
    drawHorizontalArrow(guiGraphics, startX, tipX - 5, tipX, centerY);
  }

  public static void drawVerticalArrow(GuiGraphics guiGraphics, int centerX, int startY, int arrowBaseY, int tipY) {
    // Shaft
    guiGraphics.fill(centerX - 2, startY, centerX - 1, arrowBaseY, COLOR_BORDER);
    guiGraphics.fill(centerX - 1, startY, centerX, arrowBaseY, COLOR_HIGHLIGHT);
    guiGraphics.fill(centerX, startY, centerX + 1, arrowBaseY, COLOR_MAIN);
    guiGraphics.fill(centerX + 1, startY, centerX + 2, arrowBaseY, COLOR_SHADOW);
    guiGraphics.fill(centerX + 2, startY, centerX + 3, arrowBaseY, COLOR_BORDER);

    // Arrowhead base
    guiGraphics.fill(centerX - 3, arrowBaseY, centerX - 2, arrowBaseY + 2, COLOR_BORDER);
    guiGraphics.fill(centerX - 2, arrowBaseY, centerX, arrowBaseY + 2, COLOR_HIGHLIGHT);
    guiGraphics.fill(centerX, arrowBaseY, centerX + 1, arrowBaseY + 2, COLOR_MAIN);
    guiGraphics.fill(centerX + 1, arrowBaseY, centerX + 3, arrowBaseY + 2, COLOR_SHADOW);
    guiGraphics.fill(centerX + 3, arrowBaseY, centerX + 4, arrowBaseY + 2, COLOR_BORDER);

    // Taper 1
    guiGraphics.fill(centerX - 2, arrowBaseY + 2, centerX - 1, arrowBaseY + 3, COLOR_BORDER);
    guiGraphics.fill(centerX - 1, arrowBaseY + 2, centerX, arrowBaseY + 3, COLOR_HIGHLIGHT);
    guiGraphics.fill(centerX, arrowBaseY + 2, centerX + 1, arrowBaseY + 3, COLOR_MAIN);
    guiGraphics.fill(centerX + 1, arrowBaseY + 2, centerX + 2, arrowBaseY + 3, COLOR_SHADOW);
    guiGraphics.fill(centerX + 2, arrowBaseY + 2, centerX + 3, arrowBaseY + 3, COLOR_BORDER);

    // Taper 2
    guiGraphics.fill(centerX - 1, arrowBaseY + 3, centerX, arrowBaseY + 4, COLOR_BORDER);
    guiGraphics.fill(centerX, arrowBaseY + 3, centerX + 1, arrowBaseY + 4, COLOR_HIGHLIGHT);
    guiGraphics.fill(centerX + 1, arrowBaseY + 3, centerX + 2, arrowBaseY + 4, COLOR_BORDER);

    // Tip
    guiGraphics.fill(centerX, arrowBaseY + 4, centerX + 1, tipY, COLOR_BORDER);
  }

  public static void drawVerticalArrow(GuiGraphics guiGraphics, int centerX, int startY, int tipY) {
    drawVerticalArrow(guiGraphics, centerX, startY, tipY - 5, tipY);
  }
}

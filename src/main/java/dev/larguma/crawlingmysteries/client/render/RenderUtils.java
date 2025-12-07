package dev.larguma.crawlingmysteries.client.render;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Utility class for common rendering operations.
 */
public final class RenderUtils {

  /**
   * Creates an ARGB color from individual components.
   */
  public static int argb(int alpha, int red, int green, int blue) {
    return (alpha << 24) | (red << 16) | (green << 8) | blue;
  }

  /**
   * Creates an ARGB color from a base RGB color and alpha value.
   */
  public static int withAlpha(int color, int alpha) {
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;
    return argb(alpha, r, g, b);
  }

  /**
   * Creates an ARGB color from a base RGB color and alpha value (0.0-1.0).
   */
  public static int withAlpha(int color, float alpha) {
    return withAlpha(color, (int) (alpha * 255));
  }

  /**
   * Draws a line between two points using filled rectangles.
   */
  public static void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int width, int color) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double length = Math.sqrt(dx * dx + dy * dy);

    if (length == 0) {
      return;
    }

    double halfWidth = width / 2.0;

    int segments = Math.max(1, (int) (length / 2));
    for (int i = 0; i < segments; i++) {
      double t1 = (double) i / segments;
      double t2 = (double) (i + 1) / segments;

      int segX1 = (int) (x1 + dx * t1);
      int segY1 = (int) (y1 + dy * t1);
      int segX2 = (int) (x1 + dx * t2);
      int segY2 = (int) (y1 + dy * t2);

      int minX = Math.min(segX1, segX2) - (int) halfWidth;
      int minY = Math.min(segY1, segY2) - (int) halfWidth;
      int maxX = Math.max(segX1, segX2) + (int) halfWidth;
      int maxY = Math.max(segY1, segY2) + (int) halfWidth;

      if (maxX - minX < width) {
        maxX = minX + width;
      }
      if (maxY - minY < width) {
        maxY = minY + width;
      }

      guiGraphics.fill(minX, minY, maxX, maxY, color);
    }
  }

  /**
   * Draws a filled circle using rectangles.
   */
  public static void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, float radius, int color, int alpha) {
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;
    int argb = argb(alpha, r, g, b);

    int segments = 16;
    for (int i = 0; i < segments; i++) {
      float angle1 = (float) (i * 2 * Math.PI / segments);
      float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

      int x1 = centerX + (int) (Math.cos(angle1) * radius);
      int y1 = centerY + (int) (Math.sin(angle1) * radius);
      int x2 = centerX + (int) (Math.cos(angle2) * radius);
      int y2 = centerY + (int) (Math.sin(angle2) * radius);

      guiGraphics.fill(
          Math.min(centerX, Math.min(x1, x2)),
          Math.min(centerY, Math.min(y1, y2)),
          Math.max(centerX, Math.max(x1, x2)),
          Math.max(centerY, Math.max(y1, y2)),
          argb);
    }
  }
}

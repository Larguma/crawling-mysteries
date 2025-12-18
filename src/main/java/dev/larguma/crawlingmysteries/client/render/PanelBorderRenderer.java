package dev.larguma.crawlingmysteries.client.render;

import net.minecraft.client.gui.GuiGraphics;

public final class PanelBorderRenderer {

  /**
   * Renders a decorative border around a panel.
   * 
   * @param guiGraphics  The graphics context
   * @param x            The X position of the panel (top-left corner)
   * @param y            The Y position of the panel (top-left corner)
   * @param width        The width of the panel
   * @param height       The height of the panel
   * @param primaryColor The primary color for the border
   * @param borderSize   The thickness of the border
   */
  public static void renderPanelBorder(GuiGraphics guiGraphics, int x, int y, int width, int height, int primaryColor,
      int borderSize) {
    int borderColor = 0x66000000 | (primaryColor & 0xFFFFFF);

    // Top-left
    guiGraphics.fill(x, y, x + borderSize, y + 2, borderColor);
    guiGraphics.fill(x, y, x + 2, y + borderSize, borderColor);
    // Top-right
    guiGraphics.fill(x + width - borderSize, y, x + width, y + 2, borderColor);
    guiGraphics.fill(x + width - 2, y, x + width, y + borderSize, borderColor);
    // Bottom-left
    guiGraphics.fill(x, y + height - 2, x + borderSize, y + height, borderColor);
    guiGraphics.fill(x, y + height - borderSize, x + 2, y + height, borderColor);
    // Bottom-right
    guiGraphics.fill(x + width - borderSize, y + height - 2, x + width, y + height, borderColor);
    guiGraphics.fill(x + width - 2, y + height - borderSize, x + width, y + height, borderColor);
  }
}

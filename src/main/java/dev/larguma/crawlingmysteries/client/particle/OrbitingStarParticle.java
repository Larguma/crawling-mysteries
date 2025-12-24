package dev.larguma.crawlingmysteries.client.particle;

import dev.larguma.crawlingmysteries.client.screen.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Orbiting star particles
 */
public final class OrbitingStarParticle {

  public static void renderOrbitingStars(GuiGraphics guiGraphics, int centerX, int centerY,
      int color, float animationTick, float orbitRadius, int numStars) {
    for (int i = 0; i < numStars; i++) {
      float phaseOffset = (float) (i * Math.PI * 2 / numStars);
      float angle = animationTick * 0.03f + phaseOffset;

      // Elliptical orbit
      float x = centerX + (float) Math.cos(angle) * orbitRadius;
      float y = centerY + (float) Math.sin(angle) * orbitRadius * 0.6f;

      // alpha based on position (fade when "behind")
      float depthAlpha = 0.5f + 0.5f * (float) Math.sin(angle);
      float pulseAlpha = 0.6f + 0.4f * (float) Math.sin(animationTick * 0.1f + phaseOffset);
      float alpha = depthAlpha * pulseAlpha;

      renderStar(guiGraphics, (int) x, (int) y, 3, color, alpha);
    }
  }

  public static void renderOrbitingStars(GuiGraphics guiGraphics, int centerX, int centerY, int color,
      float animationTick, int slotSize) {
    renderOrbitingStars(guiGraphics, centerX, centerY, color, animationTick, slotSize / 2 + 4, 3);
  }

  public static void renderStar(GuiGraphics guiGraphics, int x, int y, int size, int color, float alpha) {
    int argb = RenderUtils.withAlpha(color, alpha);

    guiGraphics.fill(x - size / 2, y - 1, x + size / 2 + 1, y + 2, argb);
    guiGraphics.fill(x - 1, y - size / 2, x + 2, y + size / 2 + 1, argb);

    int brightArgb = RenderUtils.withAlpha(0xFFFFFF, alpha * 0.8f);
    guiGraphics.fill(x, y, x + 1, y + 1, brightArgb);
  }
}

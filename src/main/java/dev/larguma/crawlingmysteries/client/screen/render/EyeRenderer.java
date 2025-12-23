package dev.larguma.crawlingmysteries.client.screen.render;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * Renders an eye that tracks the mouse cursor and blinks periodically.
 */
public final class EyeRenderer {

  private static float eyeTargetX = 0;
  private static float eyeTargetY = 0;
  private static float eyeCurrentX = 0;
  private static float eyeCurrentY = 0;
  private static float blinkProgress = 0;
  private static float nextBlinkTime = 100;
  private static boolean isBlinking = false;

  /**
   * Renders the eye at the specified position and size.
   * 
   * @param guiGraphics   The graphics context
   * @param mouseX        The current mouse X position
   * @param mouseY        The current mouse Y position
   * @param animationTick Current animation tick
   * @param primaryColor  The primary color for the eye
   * @param eyePosX       The X position of the eye (top-left corner)
   * @param eyePosY       The Y position of the eye (top-left corner)
   * @param size          The size of the eye
   */
  public static void renderEye(GuiGraphics guiGraphics, int mouseX, int mouseY, float animationTick, int primaryColor,
      int eyePosX, int eyePosY, int size) {

    float pulse = 0.3f + 0.2f * (float) Math.sin(animationTick * 0.05f);
    int glowAlpha = (int) (pulse * 100);
    int glowColor = (glowAlpha << 24) | (primaryColor & 0xFFFFFF);

    for (int i = 3; i > 0; i--) {
      int offset = i * 4;
      guiGraphics.fill(eyePosX - offset, eyePosY - offset,
          eyePosX + size + offset, eyePosY + size + offset, glowColor);
    }

    // Eye background (white of the eye)
    RenderSystem.enableBlend();
    guiGraphics.fill(eyePosX, eyePosY, eyePosX + size, eyePosY + size, 0xFF1A1A2E);

    // Pupil that tracks mouse
    int pupilSize = size / 3;
    float maxOffset = (size - pupilSize) / 4f;
    int pupilX = eyePosX + size / 2 - pupilSize / 2 + (int) (eyeCurrentX * maxOffset);
    int pupilY = eyePosY + size / 2 - pupilSize / 2 + (int) (eyeCurrentY * maxOffset);

    // Blink effect
    int blinkOffset = 0;
    if (isBlinking) {
      float blinkCurve = (float) Math.sin(blinkProgress * Math.PI);
      blinkOffset = (int) (size * 0.4f * blinkCurve);
    }

    // Draw iris
    int irisSize = size / 2;
    int irisX = pupilX - (irisSize - pupilSize) / 2;
    int irisY = pupilY - (irisSize - pupilSize) / 2 + blinkOffset / 2;
    int irisHeight = irisSize - blinkOffset;

    if (irisHeight > 2) {
      guiGraphics.fill(irisX, irisY, irisX + irisSize, irisY + irisHeight, 0xFF000000 | primaryColor);

      // Draw pupil (black center)
      int adjustedPupilY = pupilY + blinkOffset / 2;
      int adjustedPupilHeight = pupilSize - blinkOffset / 2;
      if (adjustedPupilHeight > 2) {
        guiGraphics.fill(pupilX, adjustedPupilY, pupilX + pupilSize, adjustedPupilY + adjustedPupilHeight, 0xFF0D0D1A);

        // Highlight
        int highlightSize = size / 10;
        guiGraphics.fill(pupilX + 3, adjustedPupilY + 3,
            pupilX + 3 + highlightSize, adjustedPupilY + 3 + Math.min(highlightSize, adjustedPupilHeight - 6),
            0xAAFFFFFF);
      }
    }

    // Eye border
    PanelBorderRenderer.renderPanelBorder(guiGraphics, eyePosX - 2, eyePosY - 2 + blinkOffset / 2, size + 4,
        size + 4 - blinkOffset, primaryColor, 6);

    RenderSystem.disableBlend();

    updateEyeTracking(mouseX, mouseY, animationTick, guiGraphics.guiWidth(), guiGraphics.guiHeight());
  }

  /**
   * Updates the blinking state based on elapsed time.
   * 
   * @param deltaTime Elapsed time in ticks
   */
  public static void tick(float deltaTime) {
    if (!isBlinking && deltaTime >= nextBlinkTime) {
      isBlinking = true;
      blinkProgress = 0;
    }
    if (isBlinking) {
      blinkProgress += 0.15f;
      if (blinkProgress >= 1.0f) {
        isBlinking = false;
        nextBlinkTime = deltaTime + 60 + (float) (Math.random() * 120);
      }
    }
  }

  private static void updateEyeTracking(int mouseX, int mouseY, float partialTick, int guiWidth, int guiHeight) {
    // Calculate target based on mouse position relative to screen center
    float targetX = (mouseX - guiWidth / 2f) / (guiWidth / 2f);
    float targetY = (mouseY - guiHeight / 2f) / (guiHeight / 2f);

    eyeTargetX = Mth.clamp(targetX, -1f, 1f);
    eyeTargetY = Mth.clamp(targetY, -1f, 1f);

    // Smooth interpolation
    float speed = 0.1f;
    eyeCurrentX += (eyeTargetX - eyeCurrentX) * speed;
    eyeCurrentY += (eyeTargetY - eyeCurrentY) * speed;
  }
}

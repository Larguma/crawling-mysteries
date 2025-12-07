package dev.larguma.crawlingmysteries.client.render;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Helper class for rendering spell-related UI elements consistently.
 */
public final class SpellSlotRenderer {

  public static final ResourceLocation SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "gui/spell_slot");
  public static final ResourceLocation SLOT_SELECTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "gui/spell_slot_selected");

  public static final float PULSE_SPEED = 0.05f;
  public static final float BOB_SPEED_NORMAL = 0.06f;
  public static final float BOB_SPEED_SELECTED = 0.12f;
  public static final float BOB_AMOUNT_NORMAL = 1.5f;
  public static final float BOB_AMOUNT_SELECTED = 3.0f;

  public static final float BURST_DURATION = 15.0f; // ticks
  public static final float BURST_MAX_SCALE = 2.5f;

  private static final int[] GLOW_SIZES_LARGE = { 32, 28, 24 };
  private static final int[] GLOW_SIZES_SMALL = { 24, 20, 16 };
  private static final float[] GLOW_ALPHA_MULTIPLIERS = { 0.3f, 0.5f, 0.7f };

  /**
   * Renders a pulsing glow effect behind a spell slot.
   * 
   * @param guiGraphics   The graphics context
   * @param centerX       Center X position
   * @param centerY       Center Y position
   * @param glowColor     The RGB color for the glow
   * @param animationTick Current animation tick for pulse calculation
   * @param large         Whether to use larger glow sizes
   */
  public static void renderPulsingGlow(GuiGraphics guiGraphics, int centerX, int centerY, int glowColor,
      float animationTick, boolean large) {
    float pulse = (float) (0.4f + 0.4f * Math.sin(animationTick * PULSE_SPEED));
    int baseAlpha = (int) (pulse * 255);

    int[] glowSizes = large ? GLOW_SIZES_LARGE : GLOW_SIZES_SMALL;

    for (int i = 0; i < glowSizes.length; i++) {
      int size = glowSizes[i];
      int alpha = (int) (baseAlpha * GLOW_ALPHA_MULTIPLIERS[i]);
      int argb = RenderUtils.withAlpha(glowColor, alpha);

      guiGraphics.fill(
          centerX - size / 2,
          centerY - size / 2,
          centerX + size / 2,
          centerY + size / 2,
          argb);
    }
  }

  /**
   * Renders the slot background texture with animation support.
   * 
   * @param guiGraphics The graphics context
   * @param x           Top-left X position
   * @param y           Top-left Y position
   * @param size        Size of the texture
   * @param selected    Whether to use the selected texture
   */
  public static void renderSlotBackground(GuiGraphics guiGraphics, int x, int y, int size, boolean selected) {
    ResourceLocation texture = selected ? SLOT_SELECTED_TEXTURE : SLOT_TEXTURE;
    Minecraft minecraft = Minecraft.getInstance();

    TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);

    guiGraphics.blit(x, y, 0, size, size, sprite);
  }

  /**
   * Renders a spell icon with animation support with optional cooldown darkening.
   * 
   * @param guiGraphics The graphics context
   * @param spell       The spell to render
   * @param x           Top-left X position
   * @param y           Top-left Y position
   * @param iconSize    Size of the icon
   * @param onCooldown  Whether the spell is on cooldown
   */
  public static void renderSpellIcon(GuiGraphics guiGraphics, Spell spell, int x, int y, int iconSize,
      boolean onCooldown) {
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spell.icon());

    float tint = onCooldown ? 0.4f : 1.0f;
    RenderSystem.setShaderColor(tint, tint, tint, 1.0f);

    guiGraphics.blit(x, y, 0, iconSize, iconSize, sprite);

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
  }

  /**
   * Renders a radial cooldown overlay with centered timer text.
   * 
   * @param guiGraphics The graphics context
   * @param spell       The spell to render cooldown for
   * @param centerX     Center X position
   * @param centerY     Center Y position
   * @param iconSize    Size of the icon (used for radius calculation)
   */
  public static void renderRadialCooldownOverlay(GuiGraphics guiGraphics, Spell spell, int centerX, int centerY,
      int iconSize) {
    float progress = ClientSpellCooldownManager.getCooldownProgress(spell);
    int remainingSeconds = ClientSpellCooldownManager.getRemainingCooldownSeconds(spell);
    String formattedCooldown = ClientSpellCooldownManager.getRemainingCooldownFormatted(spell);

    int radius = iconSize / 2 + 2;
    int segments = 32;
    int filledSegments = (int) (segments * progress);

    for (int i = 0; i < filledSegments; i++) {
      double angle = -Math.PI / 2 + (2 * Math.PI * i / segments);

      int x1 = centerX + (int) (Math.cos(angle) * radius);
      int y1 = centerY + (int) (Math.sin(angle) * radius);

      int overlayColor = 0x40000000;
      RenderUtils.drawLine(guiGraphics, centerX, centerY, x1, y1, 3, overlayColor);
    }

    if (remainingSeconds > 0) {
      Font font = Minecraft.getInstance().font;
      int textWidth = font.width(formattedCooldown);
      guiGraphics.drawString(
          font,
          formattedCooldown,
          centerX - textWidth / 2,
          centerY - font.lineHeight / 2,
          0xFFFFFFFF,
          true);
    }
  }

  /**
   * Calculates the bob offset for animating spell icons.
   * 
   * @param animationTick Current animation tick
   * @param phaseOffset   Phase offset for this particular icon
   * @param selected      Whether this icon is selected (larger bob)
   * @return The vertical offset in pixels
   */
  public static int calculateBobOffset(float animationTick, float phaseOffset, boolean selected) {
    float bobAmount = selected ? BOB_AMOUNT_SELECTED : BOB_AMOUNT_NORMAL;
    float bobSpeed = selected ? BOB_SPEED_SELECTED : BOB_SPEED_NORMAL;
    return (int) (Math.sin(animationTick * bobSpeed + phaseOffset) * bobAmount);
  }

  /**
   * Renders a burst effect when a spell comes off cooldown.
   * 
   * @param guiGraphics   The graphics context
   * @param centerX       Center X position
   * @param centerY       Center Y position
   * @param burstColor    The RGB color for the burst
   * @param burstProgress Progress of the burst animation (0.0 = start, 1.0 = end)
   * @param baseSize      Base size of the burst effect
   */
  public static void renderCooldownCompleteBurst(GuiGraphics guiGraphics, int centerX, int centerY, int burstColor,
      float burstProgress, int baseSize) {
    if (burstProgress >= 1.0f || burstProgress < 0.0f) {
      return;
    }

    float easedProgress = 1.0f - (1.0f - burstProgress) * (1.0f - burstProgress);
    float scale = 1.0f + (BURST_MAX_SCALE - 1.0f) * easedProgress;
    float alpha = 1.0f - easedProgress;
    int currentSize = (int) (baseSize * scale);

    for (int ring = 0; ring < 3; ring++) {
      float ringScale = 1.0f - (ring * 0.15f);
      float ringAlpha = alpha * (1.0f - ring * 0.3f);
      int ringSize = (int) (currentSize * ringScale);

      if (ringAlpha > 0) {
        int argb = RenderUtils.withAlpha(burstColor, ringAlpha);
        renderBurstRing(guiGraphics, centerX, centerY, ringSize, argb);
      }
    }

    // Central flash
    if (burstProgress < 0.3f) {
      float flashAlpha = (0.3f - burstProgress) / 0.3f * 0.8f;
      int flashSize = baseSize + 4;
      int flashArgb = RenderUtils.withAlpha(0xFFFFFF, flashAlpha);
      guiGraphics.fill(
          centerX - flashSize / 2,
          centerY - flashSize / 2,
          centerX + flashSize / 2,
          centerY + flashSize / 2,
          flashArgb);
    }
  }

  /**
   * Renders a single ring for the burst effect.
   */
  private static void renderBurstRing(GuiGraphics guiGraphics, int centerX, int centerY, int size, int color) {
    int halfSize = size / 2;
    int thickness = Math.max(2, size / 8);

    // top
    guiGraphics.fill(centerX - halfSize, centerY - halfSize, centerX + halfSize, centerY - halfSize + thickness, color);
    // bottom
    guiGraphics.fill(centerX - halfSize, centerY + halfSize - thickness, centerX + halfSize, centerY + halfSize, color);
    // left
    guiGraphics.fill(centerX - halfSize, centerY - halfSize, centerX - halfSize + thickness, centerY + halfSize, color);
    // right
    guiGraphics.fill(centerX + halfSize - thickness, centerY - halfSize, centerX + halfSize, centerY + halfSize, color);
  }

  /**
   * Calculates the burst progress based on time since cooldown completed.
   * 
   * @param ticksSinceCooldownEnd Ticks since the cooldown ended
   * @return Progress from 0.0 to 1.0, or 1.0+ if burst is complete
   */
  public static float calculateBurstProgress(float ticksSinceCooldownEnd) {
    return ticksSinceCooldownEnd / BURST_DURATION;
  }
}

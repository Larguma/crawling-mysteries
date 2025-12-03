package dev.larguma.crawlingmysteries.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;

public class FloatingRune {
  private static final int MAX_RUNES = 15;
  private static final Random RANDOM = new Random();

  private static final int[][] RUNE_PATTERNS = {
      { 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0 },
      { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
      { 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0 },
      { 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1 },
      { 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0 },
      { 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0 },
      { 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1 },
  };

  private static final int[] RUNE_COLORS = {
      0x6B33D7, // Purple
      0x7D90FD, // Light blue
      0x9B59B6, // Amethyst
      0x8E44AD, // Dark purple
      0x5DADE2, // Sky blue
      0xAF7AC5 // Light purple
  };

  private float x, y;
  private float prevX, prevY;
  private float vx, vy;
  private int runePattern;
  private int color;
  private float scale;
  private float alpha;
  private float alphaTarget;
  private float alphaSpeed;
  private int age;
  private int maxAge;
  private float wobbleOffset;

  FloatingRune(float x, float y, float vx, float vy, int runePattern, int color, float scale, int maxAge) {
    this.x = x;
    this.y = y;
    this.prevX = x;
    this.prevY = y;
    this.vx = vx;
    this.vy = vy;
    this.runePattern = runePattern;
    this.color = color;
    this.scale = scale;
    this.alpha = 0;
    this.alphaTarget = 0.3f + RANDOM.nextFloat() * 0.3f;
    this.alphaSpeed = 0.01f + RANDOM.nextFloat() * 0.02f;
    this.age = 0;
    this.maxAge = maxAge;
    this.wobbleOffset = RANDOM.nextFloat() * (float) Math.PI * 2;
  }

  void tick(float animationTick) {
    prevX = x;
    prevY = y;

    float wobble = (float) Math.sin(animationTick * 0.03f + wobbleOffset) * 0.3f;
    x += vx + wobble;
    y += vy;

    age++;

    if (age < 30) {
      alpha += alphaSpeed;
      if (alpha > alphaTarget) {
        alpha = alphaTarget;
      }
    } else if (age > maxAge - 40) {
      alpha -= alphaSpeed;
      if (alpha < 0) {
        alpha = 0;
      }
    } else {
      float pulse = (float) Math.sin(animationTick * 0.05f + wobbleOffset) * 0.1f;
      alpha = alphaTarget + pulse;
    }
  }

  boolean isDead() {
    return age >= maxAge;
  }

  float getX(float partialTick) {
    return prevX + (x - prevX) * partialTick;
  }

  float getY(float partialTick) {
    return prevY + (y - prevY) * partialTick;
  }

  public static List<FloatingRune> init(int screenWidth, int screenHeight) {
    List<FloatingRune> runes = new ArrayList<>();
    for (int i = 0; i < MAX_RUNES; i++) {
      runes.add(createRune(screenWidth, screenHeight, true));
    }
    return runes;
  }

  private static FloatingRune createRune(int screenWidth, int screenHeight, boolean randomAge) {
    float x = RANDOM.nextFloat() * screenWidth;
    float y = RANDOM.nextFloat() * screenHeight;

    float vx = (RANDOM.nextFloat() - 0.5f) * 0.2f;
    float vy = (RANDOM.nextFloat() - 0.5f) * 0.15f - 0.1f;

    int runePattern = RANDOM.nextInt(RUNE_PATTERNS.length);
    int color = RUNE_COLORS[RANDOM.nextInt(RUNE_COLORS.length)];
    float scale = 0.8f + RANDOM.nextFloat() * 0.8f;
    int maxAge = 300 + RANDOM.nextInt(200);

    FloatingRune rune = new FloatingRune(x, y, vx, vy, runePattern, color, scale, maxAge);

    if (randomAge) {
      rune.age = RANDOM.nextInt(maxAge - 50);
      if (rune.age > 30) {
        rune.alpha = rune.alphaTarget;
      }
    }

    return rune;
  }

  public static void update(List<FloatingRune> runes, int screenWidth, int screenHeight, float animationTick) {
    for (int i = runes.size() - 1; i >= 0; i--) {
      FloatingRune rune = runes.get(i);
      rune.tick(animationTick);

      if (rune.isDead() || rune.x < -50 || rune.x > screenWidth + 50 || rune.y < -50 || rune.y > screenHeight + 50) {
        runes.set(i, createRune(screenWidth, screenHeight, false));
      }
    }
  }

  public static void render(GuiGraphics guiGraphics, List<FloatingRune> runes, float partialTick) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (FloatingRune rune : runes) {
      if (rune.alpha <= 0)
        continue;

      float x = rune.getX(partialTick);
      float y = rune.getY(partialTick);

      int r = (rune.color >> 16) & 0xFF;
      int g = (rune.color >> 8) & 0xFF;
      int b = rune.color & 0xFF;
      int a = (int) (rune.alpha * 255);
      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      int glowAlpha = (int) (rune.alpha * 10);
      int glowArgb = (glowAlpha << 24) | (r << 16) | (g << 8) | b;
      int glowSize = (int) (12 * rune.scale);
      guiGraphics.fill(
          (int) (x - glowSize / 2),
          (int) (y - glowSize / 2),
          (int) (x + glowSize / 2),
          (int) (y + glowSize / 2),
          glowArgb);

      drawRunePattern(guiGraphics, (int) x, (int) y, rune.runePattern, rune.scale, argb);
    }

    RenderSystem.disableBlend();
  }

  private static void drawRunePattern(GuiGraphics guiGraphics, int centerX, int centerY, int patternIndex, float scale,
      int color) {
    int[] pattern = RUNE_PATTERNS[patternIndex % RUNE_PATTERNS.length];
    int pixelSize = Math.max(1, (int) (2 * scale));

    int patternWidth = 3;
    int patternHeight = 5;

    int startX = centerX - (patternWidth * pixelSize) / 2;
    int startY = centerY - (patternHeight * pixelSize) / 2;

    for (int py = 0; py < patternHeight; py++) {
      for (int px = 0; px < patternWidth; px++) {
        int index = py * patternWidth + px;
        if (index < pattern.length && pattern[index] == 1) {
          int drawX = startX + px * pixelSize;
          int drawY = startY + py * pixelSize;
          guiGraphics.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, color);
        }
      }
    }
  }
}

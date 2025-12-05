package dev.larguma.crawlingmysteries.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.gui.GuiGraphics;

public class SpellParticle {
  private static final int MAX_PARTICLES_PER_SPELL = 8;
  private static final Random RANDOM = new Random();

  private float angle;
  private float orbitRadius;
  private float orbitSpeed;
  private float size;
  private int color;
  private float alpha;
  private float alphaSpeed;
  private boolean fadingIn;
  private float verticalOffset;

  SpellParticle(float angle, float orbitRadius, float orbitSpeed, float size, int color) {
    this.angle = angle;
    this.orbitRadius = orbitRadius;
    this.orbitSpeed = orbitSpeed;
    this.size = size;
    this.color = color;
    this.alpha = 0.0f;
    this.alphaSpeed = 0.02f + RANDOM.nextFloat() * 0.03f;
    this.fadingIn = true;
    this.verticalOffset = RANDOM.nextFloat() * (float) Math.PI * 2;
  }

  void tick(float animationTick) {
    angle += orbitSpeed;
    if (angle > Math.PI * 2) {
      angle -= (float) Math.PI * 2;
    }

    if (fadingIn) {
      alpha += alphaSpeed;
      if (alpha >= 0.8f) {
        alpha = 0.8f;
        fadingIn = false;
      }
    } else {
      alpha -= alphaSpeed * 0.5f;
      if (alpha <= 0.2f) {
        alpha = 0.2f;
        fadingIn = true;
      }
    }
  }

  float getX(int centerX, float animationTick) {
    return centerX + (float) Math.cos(angle) * orbitRadius;
  }

  float getY(int centerY, float animationTick) {
    float bobbing = (float) Math.sin(animationTick * 0.05f + verticalOffset) * 2;
    return centerY + (float) Math.sin(angle) * orbitRadius + bobbing;
  }

  public static List<SpellParticle> createParticles(Spell spell) {
    List<SpellParticle> particles = new ArrayList<>();
    int[] colors = spell.colors();

    for (int i = 0; i < MAX_PARTICLES_PER_SPELL; i++) {
      float angle = RANDOM.nextFloat() * (float) Math.PI * 2;
      float orbitRadius = 20 + RANDOM.nextFloat() * 15;
      float orbitSpeed = 0.02f + RANDOM.nextFloat() * 0.03f;
      if (RANDOM.nextBoolean()) {
        orbitSpeed = -orbitSpeed;
      }
      float size = 2 + RANDOM.nextFloat() * 3;
      int color = colors[RANDOM.nextInt(colors.length)];

      particles.add(new SpellParticle(angle, orbitRadius, orbitSpeed, size, color));
    }

    return particles;
  }

  public static void updateParticles(List<SpellParticle> particles, float animationTick) {
    for (SpellParticle particle : particles) {
      particle.tick(animationTick);
    }
  }

  public static void renderParticles(GuiGraphics guiGraphics, List<SpellParticle> particles, int slotCenterX,
      int slotCenterY, float animationTick) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (SpellParticle particle : particles) {
      float x = particle.getX(slotCenterX, animationTick);
      float y = particle.getY(slotCenterY, animationTick);

      int r = (particle.color >> 16) & 0xFF;
      int g = (particle.color >> 8) & 0xFF;
      int b = particle.color & 0xFF;
      int a = (int) (particle.alpha * 255);
      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      float size = particle.size;

      int glowAlpha = (int) (particle.alpha * 100);
      int glowArgb = (glowAlpha << 24) | (r << 16) | (g << 8) | b;
      guiGraphics.fill(
          (int) (x - size - 1),
          (int) (y - size - 1),
          (int) (x + size + 1),
          (int) (y + size + 1),
          glowArgb);

      guiGraphics.fill(
          (int) (x - size / 2),
          (int) (y - size / 2),
          (int) (x + size / 2),
          (int) (y + size / 2),
          argb);
    }

    RenderSystem.disableBlend();
  }
}

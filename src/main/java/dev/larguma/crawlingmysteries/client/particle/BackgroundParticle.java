package dev.larguma.crawlingmysteries.client.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.client.screen.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Floating background particles
 */
public class BackgroundParticle {
  private static final int MAX_PARTICLES = 50;
  private static final Random RANDOM = new Random();

  private static final int[] PARTICLE_COLORS = {
      0x6B33D7, // Purple
      0x7D90FD, // Light blue
      0x9B59B6, // Amethyst
      0x8E44AD, // Dark purple
      0x5DADE2, // Sky blue
      0xAF7AC5 // Light purple
  };

  private float x, y;
  private float prevX, prevY;
  private final float vx, vy;
  private int age;
  private final int maxAge;
  private final int color;
  private final float size;

  private BackgroundParticle(float x, float y, float vx, float vy, int age, int maxAge, int color, float size) {
    this.x = x;
    this.y = y;
    this.prevX = x;
    this.prevY = y;
    this.vx = vx;
    this.vy = vy;
    this.age = age;
    this.maxAge = maxAge;
    this.color = color;
    this.size = size;
  }

  private void tick() {
    prevX = x;
    prevY = y;
    x += vx;
    y += vy;
    age++;
  }

  private boolean isDead() {
    return age >= maxAge;
  }

  private float getAlpha() {
    float fadeIn = Math.min(1.0f, age / 20.0f);
    float fadeOut = Math.min(1.0f, (maxAge - age) / 20.0f);
    return fadeIn * fadeOut * 0.6f;
  }

  private float getX(float partialTick) {
    return prevX + (x - prevX) * partialTick;
  }

  private float getY(float partialTick) {
    return prevY + (y - prevY) * partialTick;
  }

  public static List<BackgroundParticle> init(int screenWidth, int screenHeight) {
    List<BackgroundParticle> particles = new ArrayList<>();
    for (int i = 0; i < MAX_PARTICLES; i++) {
      particles.add(createParticle(screenWidth, screenHeight, true));
    }
    return particles;
  }

  public static void update(List<BackgroundParticle> particles, int screenWidth, int screenHeight) {
    for (int i = particles.size() - 1; i >= 0; i--) {
      BackgroundParticle particle = particles.get(i);
      particle.tick();

      if (particle.isDead()) {
        particles.set(i, createParticle(screenWidth, screenHeight, false));
      }
    }
  }

  public static void render(GuiGraphics guiGraphics, List<BackgroundParticle> particles, float partialTick) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (BackgroundParticle particle : particles) {
      float alpha = particle.getAlpha();
      int argb = RenderUtils.withAlpha(particle.color, alpha);

      float x = particle.getX(partialTick);
      float y = particle.getY(partialTick);
      float size = particle.size * alpha;

      guiGraphics.fill(
          (int) (x - size / 2),
          (int) (y - size / 2),
          (int) (x + size / 2),
          (int) (y + size / 2),
          argb);
    }

    RenderSystem.disableBlend();
  }

  private static BackgroundParticle createParticle(int screenWidth, int screenHeight, boolean randomAge) {
    float x = RANDOM.nextFloat() * screenWidth;
    float y = RANDOM.nextFloat() * screenHeight;
    float vx = (RANDOM.nextFloat() - 0.5f) * 0.5f;
    float vy = -RANDOM.nextFloat() * 0.8f - 0.2f;
    int maxAge = 60 + RANDOM.nextInt(120);
    int age = randomAge ? RANDOM.nextInt(maxAge) : 0;
    int color = PARTICLE_COLORS[RANDOM.nextInt(PARTICLE_COLORS.length)];
    float size = 1.5f + RANDOM.nextFloat() * 2.5f;

    return new BackgroundParticle(x, y, vx, vy, age, maxAge, color, size);
  }
}

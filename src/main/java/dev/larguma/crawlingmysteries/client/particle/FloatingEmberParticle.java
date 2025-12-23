package dev.larguma.crawlingmysteries.client.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.client.screen.render.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Floating ember particles that spawn around a point and drift upward with
 * twinkle effects.
 */
public class FloatingEmberParticle {
  private static final Random RANDOM = new Random();

  private float x, y;
  private float u, v;
  private float lifetime;
  private final float maxLifetime;
  private final float size;
  private final int color;

  public FloatingEmberParticle(float x, float y, float u, float v, float lifetime, float size, int color) {
    this.x = x;
    this.y = y;
    this.u = u;
    this.v = v;
    this.lifetime = lifetime;
    this.maxLifetime = lifetime;
    this.size = size;
    this.color = color;
  }

  public void update(float deltaTicks) {
    x += u * deltaTicks;
    y += v * deltaTicks;

    // wandering motion
    u += (RANDOM.nextFloat() - 0.5f) * 0.02f * deltaTicks;
    v -= 0.005f * deltaTicks; // upward drift

    lifetime -= deltaTicks;
  }

  public boolean isDead() {
    return lifetime <= 0;
  }

  public void render(GuiGraphics guiGraphics) {
    float lifeRatio = lifetime / maxLifetime;

    // Fade in and out
    float alpha;
    if (lifeRatio > 0.8f) {
      alpha = (1.0f - lifeRatio) / 0.2f; // Fade in
    } else if (lifeRatio < 0.3f) {
      alpha = lifeRatio / 0.3f; // Fade out
    } else {
      alpha = 1.0f;
    }
    alpha *= 0.7f;

    float twinkle = 0.7f + 0.3f * (float) Math.sin(lifetime * 0.5f);
    alpha *= twinkle;

    int currentSize = (int) (size * (0.5f + 0.5f * lifeRatio));
    if (currentSize < 1)
      currentSize = 1;

    // particle
    int argb = RenderUtils.withAlpha(color, alpha);
    guiGraphics.fill((int) x - currentSize / 2, (int) y - currentSize / 2,
        (int) x + currentSize / 2 + 1, (int) y + currentSize / 2 + 1, argb);

    // bright center
    if (currentSize > 1) {
      int brightArgb = RenderUtils.withAlpha(0xFFFFFF, alpha * 0.6f);
      guiGraphics.fill((int) x, (int) y, (int) x + 1, (int) y + 1, brightArgb);
    }
  }

  public static FloatingEmberParticle createAround(int centerX, int centerY, int color, float minDistance,
      float maxDistance) {
    float angle = RANDOM.nextFloat() * (float) Math.PI * 2;
    float distance = minDistance + RANDOM.nextFloat() * (maxDistance - minDistance);
    float x = centerX + (float) Math.cos(angle) * distance;
    float y = centerY + (float) Math.sin(angle) * distance;

    float u = (RANDOM.nextFloat() - 0.5f) * 0.3f;
    float v = -0.2f - RANDOM.nextFloat() * 0.4f;

    float lifetime = 30 + RANDOM.nextFloat() * 40;
    float size = 1.5f + RANDOM.nextFloat() * 2f;

    return new FloatingEmberParticle(x, y, u, v, lifetime, size, color);
  }

  public static FloatingEmberParticle createAround(int centerX, int centerY, int color) {
    return createAround(centerX, centerY, color, 8, 20);
  }

  public static FloatingEmberParticle createInArea(int x, int y, int width, int height, int color) {
    float spawnX, spawnY;

    if (RANDOM.nextFloat() < 0.7f) {
      // Spawn along edges
      int edge = RANDOM.nextInt(4);
      switch (edge) {
        case 0: // top
          spawnX = x + RANDOM.nextFloat() * width;
          spawnY = y - 2 + RANDOM.nextFloat() * 4;
          break;
        case 1: // bottom
          spawnX = x + RANDOM.nextFloat() * width;
          spawnY = y + height - 2 + RANDOM.nextFloat() * 4;
          break;
        case 2: // left
          spawnX = x - 2 + RANDOM.nextFloat() * 4;
          spawnY = y + RANDOM.nextFloat() * height;
          break;
        default: // right
          spawnX = x + width - 2 + RANDOM.nextFloat() * 4;
          spawnY = y + RANDOM.nextFloat() * height;
          break;
      }
    } else {
      // Spawn inside
      spawnX = x + RANDOM.nextFloat() * width;
      spawnY = y + RANDOM.nextFloat() * height;
    }

    float u = (RANDOM.nextFloat() - 0.5f) * 0.3f;
    float v = -0.2f - RANDOM.nextFloat() * 0.4f;

    float lifetime = 30 + RANDOM.nextFloat() * 40;
    float size = 1.5f + RANDOM.nextFloat() * 2f;

    return new FloatingEmberParticle(spawnX, spawnY, u, v, lifetime, size, color);
  }

  public static void updateParticles(List<FloatingEmberParticle> particles, float deltaTicks) {
    particles.removeIf(p -> {
      p.update(deltaTicks);
      return p.isDead();
    });
  }

  public static void spawnParticles(List<FloatingEmberParticle> particles, int centerX, int centerY, int color,
      int maxParticles, float spawnRate) {
    if (particles.size() < maxParticles && RANDOM.nextFloat() < spawnRate) {
      particles.add(createAround(centerX, centerY, color));
    }
  }

  public static void spawnParticlesInArea(List<FloatingEmberParticle> particles, int x, int y, int width, int height,
      int color, int maxParticles, float spawnRate) {
    if (particles.size() < maxParticles && RANDOM.nextFloat() < spawnRate) {
      particles.add(createInArea(x, y, width, height, color));
    }
  }

  public static void renderParticles(GuiGraphics guiGraphics, List<FloatingEmberParticle> particles) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (FloatingEmberParticle particle : particles) {
      particle.render(guiGraphics);
    }
  }

  public static void updateAndRender(GuiGraphics guiGraphics, List<FloatingEmberParticle> particles, int centerX,
      int centerY, int color, float deltaTicks, int maxParticles, float spawnRate, boolean shouldSpawn) {
    updateParticles(particles, deltaTicks);
    if (shouldSpawn) {
      spawnParticles(particles, centerX, centerY, color, maxParticles, spawnRate);
    }
    renderParticles(guiGraphics, particles);
  }

  public static void updateAndRenderInArea(GuiGraphics guiGraphics, List<FloatingEmberParticle> particles, int x, int y,
      int width, int height, int color, float deltaTicks, int maxParticles, float spawnRate, boolean shouldSpawn) {
    updateParticles(particles, deltaTicks);
    if (shouldSpawn) {
      spawnParticlesInArea(particles, x, y, width, height, color, maxParticles, spawnRate);
    }
    renderParticles(guiGraphics, particles);
  }

  public static List<FloatingEmberParticle> createList() {
    return new ArrayList<>();
  }
}

package dev.larguma.crawlingmysteries.client.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.gui.GuiGraphics;

public class SpellParticle {
  private static final int MAX_PARTICLES_PER_SPELL = 8;
  private static final Random RANDOM = new Random();

  private static final Map<String, int[]> SPELL_COLORS = new HashMap<>();

  static {
    // Default mystical colors
    SPELL_COLORS.put("default", new int[] { 0x6B33D7, 0x9B59B6, 0x7D90FD });
    // Spectral-based - ghostly cyan/teal colors
    SPELL_COLORS.put("spectral", new int[] { 0x00CED1, 0x40E0D0, 0x48D1CC, 0x7FFFD4 });
    // Fire-based spells - warm orange/red
    SPELL_COLORS.put("fire", new int[] { 0xFF4500, 0xFF6347, 0xFFA500, 0xFFD700 });
    // Ice-based spells - cool blue/white
    SPELL_COLORS.put("ice", new int[] { 0x87CEEB, 0xADD8E6, 0xB0E0E6, 0xE0FFFF });
    // Nature-based spells - green tones
    SPELL_COLORS.put("nature", new int[] { 0x32CD32, 0x90EE90, 0x98FB98, 0x7CFC00 });
    // Dark/shadow spells - purple/black tones
    SPELL_COLORS.put("shadow", new int[] { 0x4B0082, 0x8B008B, 0x9400D3, 0x800080 });
    // Lightning spells - electric yellow/blue
    SPELL_COLORS.put("lightning", new int[] { 0xFFFF00, 0xADD8E6, 0x87CEEB, 0xF0E68C });
  }

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

  public static int[] getColorsForSpell(Spell spell) {
    if (spell == null) {
      return SPELL_COLORS.get("default");
    }

    String spellId = spell.id();

    if (SPELL_COLORS.containsKey(spellId)) {
      return SPELL_COLORS.get(spellId);
    }

    if (spellId.contains("fire") || spellId.contains("flame") || spellId.contains("burn")) {
      return SPELL_COLORS.get("fire");
    }
    if (spellId.contains("ice") || spellId.contains("frost") || spellId.contains("freeze")) {
      return SPELL_COLORS.get("ice");
    }
    if (spellId.contains("nature") || spellId.contains("life") || spellId.contains("heal")) {
      return SPELL_COLORS.get("nature");
    }
    if (spellId.contains("shadow") || spellId.contains("dark") || spellId.contains("void")) {
      return SPELL_COLORS.get("shadow");
    }
    if (spellId.contains("lightning") || spellId.contains("thunder") || spellId.contains("shock")) {
      return SPELL_COLORS.get("lightning");
    }
    if (spellId.contains("spectral") || spellId.contains("ghost") || spellId.contains("spirit")) {
      return SPELL_COLORS.get("spectral");
    }

    return SPELL_COLORS.get("default");
  }

  public static int getPrimaryColorForSpell(Spell spell) {
    int[] colors = getColorsForSpell(spell);
    return colors[0];
  }

  public static List<SpellParticle> createParticles(Spell spell) {
    List<SpellParticle> particles = new ArrayList<>();
    int[] colors = getColorsForSpell(spell);

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

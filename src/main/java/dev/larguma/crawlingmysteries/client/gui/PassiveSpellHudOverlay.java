package dev.larguma.crawlingmysteries.client.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.ConfigClient;
import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.particle.FloatingEmberParticle;
import dev.larguma.crawlingmysteries.client.particle.OrbitingStarParticle;
import dev.larguma.crawlingmysteries.client.render.SpellSlotRenderer;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class PassiveSpellHudOverlay implements LayeredDraw.Layer {

  public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
      "passive_spell_hud");

  private static final int ICON_SIZE = 24;
  private static final int SLOT_TEXTURE_SIZE = 28;
  private static final int ICON_SPACING = 6;
  private static final int MARGIN_RIGHT = 10;
  private static final int MARGIN_BOTTOM = 40;

  private static final int MAX_PARTICLES_PER_SPELL = 8;
  private static final float PARTICLE_SPAWN_RATE = 0.15f;

  private float animationTick = 0;

  private final Map<String, Boolean> previousCooldownStates = new HashMap<>();
  private final Map<String, Float> burstAnimationTimers = new HashMap<>();
  private final Map<String, List<FloatingEmberParticle>> spellParticles = new HashMap<>();

  @Override
  public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
    Minecraft minecraft = Minecraft.getInstance();

    if (minecraft.player == null || minecraft.options.hideGui || !ConfigClient.CLIENT.renderPassiveSpellHud.get()) {
      return;
    }

    List<Spell> passiveSpells = getPassiveSpells(minecraft);
    if (passiveSpells.isEmpty()) {
      return;
    }

    float deltaTicks = deltaTracker.getRealtimeDeltaTicks();
    animationTick += deltaTicks;

    updateBurstTimers(passiveSpells, deltaTicks);

    int screenWidth = guiGraphics.guiWidth();
    int screenHeight = guiGraphics.guiHeight();

    // bottom right
    int startX = screenWidth - MARGIN_RIGHT - SLOT_TEXTURE_SIZE;
    int startY = screenHeight - MARGIN_BOTTOM - SLOT_TEXTURE_SIZE;

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (int i = 0; i < passiveSpells.size(); i++) {
      Spell spell = passiveSpells.get(i);
      int slotX = startX;
      int slotY = startY - (i * (SLOT_TEXTURE_SIZE + ICON_SPACING));

      renderPassiveSpellSlot(guiGraphics, minecraft, spell, slotX, slotY, deltaTicks);
    }

    RenderSystem.disableBlend();
  }

  private void updateBurstTimers(List<Spell> spells, float deltaTicks) {
    for (Spell spell : spells) {
      String spellId = spell.id();
      boolean currentlyOnCooldown = ClientSpellCooldownManager.isOnCooldown(spell);
      Boolean wasOnCooldown = previousCooldownStates.get(spellId);

      if (wasOnCooldown != null && wasOnCooldown && !currentlyOnCooldown) {
        burstAnimationTimers.put(spellId, 0.0f);
      }

      Float burstTimer = burstAnimationTimers.get(spellId);
      if (burstTimer != null) {
        burstTimer += deltaTicks;
        if (burstTimer >= SpellSlotRenderer.BURST_DURATION) {
          burstAnimationTimers.remove(spellId);
        } else {
          burstAnimationTimers.put(spellId, burstTimer);
        }
      }

      previousCooldownStates.put(spellId, currentlyOnCooldown);
    }
  }

  private void renderPassiveSpellSlot(GuiGraphics guiGraphics, Minecraft minecraft, Spell spell, int slotX, int slotY,
      float deltaTicks) {
    boolean onCooldown = ClientSpellCooldownManager.isOnCooldown(spell);

    int slotCenterX = slotX + SLOT_TEXTURE_SIZE / 2;
    int slotCenterY = slotY + SLOT_TEXTURE_SIZE / 2;
    int glowColor = spell.getPrimaryColor();

    Float burstTimer = burstAnimationTimers.get(spell.id());
    if (burstTimer != null) {
      float burstProgress = SpellSlotRenderer.calculateBurstProgress(burstTimer);
      SpellSlotRenderer.renderCooldownCompleteBurst(guiGraphics, slotCenterX, slotCenterY, glowColor, burstProgress,
          SLOT_TEXTURE_SIZE);
    }

    if (!onCooldown) {
      SpellSlotRenderer.renderPulsingGlow(guiGraphics, slotCenterX, slotCenterY, glowColor, animationTick, false);
    }

    SpellSlotRenderer.renderSlotBackground(guiGraphics, slotX, slotY, SLOT_TEXTURE_SIZE, !onCooldown);

    int iconX = slotX + (SLOT_TEXTURE_SIZE - ICON_SIZE) / 2;
    int iconY = slotY + (SLOT_TEXTURE_SIZE - ICON_SIZE) / 2;

    if (!onCooldown) {
      iconY += SpellSlotRenderer.calculateBobOffset(animationTick, 0, false);
    }

    SpellSlotRenderer.renderSpellIcon(guiGraphics, spell, iconX, iconY, ICON_SIZE, onCooldown);

    if (onCooldown) {
      SpellSlotRenderer.renderRadialCooldownOverlay(guiGraphics, spell, slotCenterX, slotCenterY, ICON_SIZE);
    } else {
      OrbitingStarParticle.renderOrbitingStars(guiGraphics, slotCenterX, slotCenterY, glowColor, animationTick,
          SLOT_TEXTURE_SIZE);

      List<FloatingEmberParticle> particles = spellParticles.computeIfAbsent(spell.id(),
          k -> FloatingEmberParticle.createList());
      FloatingEmberParticle.updateAndRender(guiGraphics, particles, slotCenterX, slotCenterY, glowColor, deltaTicks,
          MAX_PARTICLES_PER_SPELL, PARTICLE_SPAWN_RATE, true);
    }
  }

  private List<Spell> getPassiveSpells(Minecraft minecraft) {
    if (minecraft.player == null) {
      return List.of();
    }

    return ModSpells.getAvailableSpells(minecraft.player, false).stream()
        .filter(spell -> !spell.showOnWheel()).toList();
  }
}

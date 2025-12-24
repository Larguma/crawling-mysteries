package dev.larguma.crawlingmysteries.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.ConfigClient;
import dev.larguma.crawlingmysteries.client.event.KeyMappingsEvents;
import dev.larguma.crawlingmysteries.client.particle.BackgroundParticle;
import dev.larguma.crawlingmysteries.client.particle.FloatingRuneParticle;
import dev.larguma.crawlingmysteries.client.particle.SpellParticle;
import dev.larguma.crawlingmysteries.client.screen.render.EyeRenderer;
import dev.larguma.crawlingmysteries.client.screen.render.RenderUtils;
import dev.larguma.crawlingmysteries.client.screen.render.SpellSlotRenderer;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpellSelectMenuScreen extends Screen {

  private static final int SLOT_SIZE = 32;
  private static final int SLOT_TEXTURE_SIZE = 36;
  private static final int RADIUS = 80;
  private static final int CENTER_DEADZONE = 40;

  private float animationTick = 0;
  private boolean animate;

  private static final int PRIMARY_COLOR = 0x6B33D7; // Purple
  private static final int SECONDARY_COLOR = 0x7D90FD; // Light blue

  private List<BackgroundParticle> particles = new ArrayList<>();
  private List<SpellParticle> spellParticles = new ArrayList<>();
  private List<FloatingRuneParticle> floatingRunes = new ArrayList<>();
  private List<Spell> availableSpells;
  private int selectedIndex = -1;
  private int previousSelectedIndex = -1;
  private Spell hoveredSpell = null;

  public SpellSelectMenuScreen() {
    super(Component.translatable("screen.crawlingmysteries.spell_select"));
  }

  @Override
  protected void init() {
    super.init();
    animate = ConfigClient.CLIENT.renderScreenAnimations.get();

    if (this.minecraft != null && this.minecraft.player != null) {
      this.availableSpells = ModSpells.getAvailableSpells(this.minecraft.player);
    } else {
      this.availableSpells = List.of();
    }

    particles = BackgroundParticle.init(this.width, this.height);
    floatingRunes = FloatingRuneParticle.init(this.width, this.height);
  }

  @Override
  public void tick() {
    super.tick();
    if (animate) {
      BackgroundParticle.update(particles, this.width, this.height);
      SpellParticle.updateParticles(spellParticles, animationTick);
      FloatingRuneParticle.update(floatingRunes, this.width, this.height, animationTick);
      EyeRenderer.tick(animationTick);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.animationTick += this.minecraft.getTimer().getRealtimeDeltaTicks();

    renderTransparentBackground(guiGraphics);
    if (animate) {
      EyeRenderer.renderEye(guiGraphics, mouseX, mouseY, animationTick, PRIMARY_COLOR,
          7, 7, 32);
    }
    BackgroundParticle.render(guiGraphics, particles, partialTick);
    FloatingRuneParticle.render(guiGraphics, floatingRunes, partialTick);

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    int centerX = this.width / 2;
    int centerY = this.height / 2;

    updateHoveredSpell(mouseX, mouseY, centerX, centerY);

    renderRuneCircle(guiGraphics, centerX, centerY);

    renderSpellSlots(guiGraphics, centerX, centerY, partialTick);

    renderCenterIndicator(guiGraphics, centerX, centerY);

    if (hoveredSpell != null) {
      renderSpellInfo(guiGraphics, mouseX, mouseY);
    } else if (availableSpells.isEmpty()) {
      renderNoSpellsMessage(guiGraphics, centerX, centerY);
    }
  }

  private void renderRuneCircle(GuiGraphics guiGraphics, int centerX, int centerY) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    float rotation = 0;
    float pulse = 1.0f;
    if (animate) {
      rotation = animationTick * 0.02f;
      pulse = (float) (0.3f + 0.2f * Math.sin(animationTick * SpellSlotRenderer.PULSE_SPEED));
    }

    int outerRadius = RADIUS + 30;
    drawRuneRing(guiGraphics, centerX, centerY, outerRadius, rotation, pulse, PRIMARY_COLOR);

    int innerRadius = RADIUS - 25;
    drawRuneRing(guiGraphics, centerX, centerY, innerRadius, -rotation * 1.5f, pulse, SECONDARY_COLOR);

    int middleRadius = RADIUS + 2;
    drawDecorativeRing(guiGraphics, centerX, centerY, middleRadius, pulse);

    RenderSystem.disableBlend();
  }

  private void drawRuneRing(GuiGraphics guiGraphics, int centerX, int centerY, int radius, float rotation, float alpha,
      int color) {
    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = color & 0xFF;

    int runeCount = 12;
    for (int i = 0; i < runeCount; i++) {
      double angle = rotation + (2 * Math.PI * i / runeCount);
      int x = centerX + (int) (Math.cos(angle) * radius);
      int y = centerY + (int) (Math.sin(angle) * radius);

      float runeAlpha = alpha;
      if (animate) {
        runeAlpha = alpha * (0.5f + 0.5f * (float) Math.sin(animationTick * 0.05f + i));
      }
      int a = (int) (runeAlpha * 255);
      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      drawRuneSymbol(guiGraphics, x, y, i % 4, argb, rotation + i);
    }

    int segments = 24;
    for (int i = 0; i < segments; i++) {
      double angle1 = rotation + (2 * Math.PI * i / segments);
      double angle2 = rotation + (2 * Math.PI * (i + 0.7) / segments);

      int x1 = centerX + (int) (Math.cos(angle1) * radius);
      int y1 = centerY + (int) (Math.sin(angle1) * radius);
      int x2 = centerX + (int) (Math.cos(angle2) * radius);
      int y2 = centerY + (int) (Math.sin(angle2) * radius);

      float segmentAlpha = alpha * 0.3f;
      int a = (int) (segmentAlpha * 255);
      int argb = (a << 24) | (r << 16) | (g << 8) | b;

      RenderUtils.drawLine(guiGraphics, x1, y1, x2, y2, 1, argb);
    }
  }

  private void drawRuneSymbol(GuiGraphics guiGraphics, int x, int y, int type, int color, float rotation) {
    int size = 4;
    switch (type) {
      case 0: // Diamond
        guiGraphics.fill(x - size / 2, y - size, x + size / 2, y, color);
        guiGraphics.fill(x - size / 2, y, x + size / 2, y + size, color);
        break;
      case 1: // Cross
        guiGraphics.fill(x - size, y - 1, x + size, y + 1, color);
        guiGraphics.fill(x - 1, y - size, x + 1, y + size, color);
        break;
      case 2: // Square
        guiGraphics.fill(x - size / 2, y - size / 2, x + size / 2, y + size / 2, color);
        break;
      case 3: // Triangle approximation
        guiGraphics.fill(x - 1, y - size, x + 1, y - size + 2, color);
        guiGraphics.fill(x - 2, y - size + 2, x + 2, y - size + 4, color);
        guiGraphics.fill(x - 3, y - size + 4, x + 3, y - size + 6, color);
        break;
    }
  }

  private void drawDecorativeRing(GuiGraphics guiGraphics, int centerX, int centerY, int radius, float alpha) {
    int r1 = (PRIMARY_COLOR >> 16) & 0xFF;
    int g1 = (PRIMARY_COLOR >> 8) & 0xFF;
    int b1 = PRIMARY_COLOR & 0xFF;

    int dots = 36;
    for (int i = 0; i < dots; i++) {
      double angle = 2 * Math.PI * i / dots;
      int x = centerX + (int) (Math.cos(angle) * radius);
      int y = centerY + (int) (Math.sin(angle) * radius);

      float dotAlpha = alpha;
      if (animate) {
        dotAlpha = alpha * (0.4f + 0.3f * (float) Math.sin(animationTick * 0.08f + i * 0.5f));
      }
      int a = (int) (dotAlpha * 255);
      int argb = (a << 24) | (r1 << 16) | (g1 << 8) | b1;

      int dotSize = (i % 3 == 0) ? 2 : 1;
      guiGraphics.fill(x - dotSize, y - dotSize, x + dotSize, y + dotSize, argb);
    }

  }

  private void renderCenterIndicator(GuiGraphics guiGraphics, int centerX, int centerY) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    float pulse = 1.0f;
    if (animate) {
      pulse = (float) (0.5f + 0.3f * Math.sin(animationTick * SpellSlotRenderer.PULSE_SPEED));
    }
    int innerAlpha = (int) (pulse * 180);
    RenderUtils.drawCircle(guiGraphics, centerX, centerY, 8, PRIMARY_COLOR, innerAlpha);

    RenderSystem.disableBlend();
  }

  private void updateHoveredSpell(int mouseX, int mouseY, int centerX, int centerY) {
    if (availableSpells.isEmpty()) {
      hoveredSpell = null;
      selectedIndex = -1;
      if (previousSelectedIndex != -1) {
        spellParticles.clear();
        previousSelectedIndex = -1;
      }
      return;
    }

    // distance and angle from center
    double dx = mouseX - centerX;
    double dy = mouseY - centerY;
    double distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < CENTER_DEADZONE) {
      hoveredSpell = null;
      selectedIndex = -1;
      if (previousSelectedIndex != -1) {
        spellParticles.clear();
        previousSelectedIndex = -1;
      }
      return;
    }

    // radians, from top, clockwise
    double angle = Math.atan2(dx, -dy);
    if (angle < 0) {
      angle += 2 * Math.PI;
    }

    double sliceAngle = 2 * Math.PI / availableSpells.size();
    int index = (int) ((angle + sliceAngle / 2) / sliceAngle) % availableSpells.size();

    selectedIndex = index;
    hoveredSpell = availableSpells.get(index);

    if (selectedIndex != previousSelectedIndex) {
      spellParticles = SpellParticle.createParticles(hoveredSpell);
      previousSelectedIndex = selectedIndex;
      playHoverSound();
    }
  }

  private void playHoverSound() {
    if (this.minecraft != null) {
      this.minecraft.getSoundManager().play(
          SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.5f, 0.2f));
    }
  }

  private void playSelectSound() {
    if (this.minecraft != null) {
      this.minecraft.getSoundManager().play(
          SimpleSoundInstance.forUI(SoundEvents.ENCHANTMENT_TABLE_USE, 1.2f, 0.6f));
    }
  }

  private void renderSpellSlots(GuiGraphics guiGraphics, int centerX, int centerY, float partialTick) {
    if (availableSpells.isEmpty()) {
      return;
    }

    int spellCount = availableSpells.size();
    double angleStep = 2 * Math.PI / spellCount;

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (int i = 0; i < spellCount; i++) {
      Spell spell = availableSpells.get(i);
      double angle = angleStep * i - Math.PI / 2;
      int slotCenterX = centerX + (int) (Math.cos(angle) * RADIUS);
      int slotCenterY = centerY + (int) (Math.sin(angle) * RADIUS);
      boolean isSelected = (i == selectedIndex);
      renderConnectionLine(guiGraphics, centerX, centerY, slotCenterX, slotCenterY, isSelected, spell);
    }

    for (int i = 0; i < spellCount; i++) {
      Spell spell = availableSpells.get(i);

      // calculate position (from top, clockwise)
      double angle = angleStep * i - Math.PI / 2;
      int slotX = centerX + (int) (Math.cos(angle) * RADIUS) - SLOT_TEXTURE_SIZE / 2;
      int slotY = centerY + (int) (Math.sin(angle) * RADIUS) - SLOT_TEXTURE_SIZE / 2;
      int slotCenterX = slotX + SLOT_TEXTURE_SIZE / 2;
      int slotCenterY = slotY + SLOT_TEXTURE_SIZE / 2;

      boolean isSelected = (i == selectedIndex);

      if (isSelected && animate) {
        int glowColor = spell.getPrimaryColor();
        SpellSlotRenderer.renderPulsingGlow(guiGraphics, slotCenterX, slotCenterY, glowColor, animationTick, true);
        SpellParticle.renderParticles(guiGraphics, spellParticles, slotCenterX, slotCenterY, animationTick);
      }

      SpellSlotRenderer.renderSlotBackground(guiGraphics, slotX, slotY, SLOT_TEXTURE_SIZE, isSelected);

      float phaseOffset = i * 0.8f;
      int bobOffset = 0;
      if (animate) {
        bobOffset = SpellSlotRenderer.calculateBobOffset(animationTick, phaseOffset, isSelected);
      }

      int iconX = slotX + (SLOT_TEXTURE_SIZE - SLOT_SIZE) / 2;
      int iconY = slotY + (SLOT_TEXTURE_SIZE - SLOT_SIZE) / 2 + bobOffset;

      boolean onCooldown = ClientSpellCooldownManager.isOnCooldown(spell);
      SpellSlotRenderer.renderSpellIcon(guiGraphics, spell, iconX, iconY, SLOT_SIZE, onCooldown);

      if (onCooldown) {
        SpellSlotRenderer.renderRadialCooldownOverlay(guiGraphics, spell, slotCenterX, slotCenterY, SLOT_SIZE);
      }
    }

    RenderSystem.disableBlend();
  }

  private void renderConnectionLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, boolean isHighlighted,
      Spell spell) {
    if (!isHighlighted) {
      return;
    }

    float pulse = 1.0f;
    if (animate) {
      pulse = (float) (0.4f + 0.4f * Math.sin(animationTick * SpellSlotRenderer.PULSE_SPEED));
    }
    int alpha = (int) (pulse * 200);
    int color = spell.getPrimaryColor();
    int lineWidth = 3;

    int argb = RenderUtils.withAlpha(color, alpha);
    RenderUtils.drawLine(guiGraphics, x1, y1, x2, y2, lineWidth, argb);

    int glowArgb = RenderUtils.withAlpha(color, (int) (alpha * 0.3f));
    RenderUtils.drawLine(guiGraphics, x1, y1, x2, y2, lineWidth + 4, glowArgb);
  }

  private void renderSpellInfo(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    if (hoveredSpell == null)
      return;

    Component spellName = hoveredSpell.name();
    Component description = hoveredSpell.description();

    boolean onCooldown = ClientSpellCooldownManager.isOnCooldown(hoveredSpell);
    Component cooldownText = null;
    if (onCooldown) {
      String formattedCooldown = ClientSpellCooldownManager.getRemainingCooldownFormatted(hoveredSpell);
      cooldownText = Component.translatable("screen.crawlingmysteries.spell_select.cooldown", formattedCooldown);
    }

    int nameWidth = this.font.width(spellName);
    int descWidth = this.font.width(description);
    int cooldownWidth = cooldownText != null ? this.font.width(cooldownText) : 0;

    int maxTextWidth = Math.max(nameWidth, Math.max(descWidth, cooldownWidth));
    int panelWidth = maxTextWidth + 10;
    int panelHeight = onCooldown ? 47 : 33;

    int offsetX = 12;
    int offsetY = -12;
    int panelX = mouseX + offsetX;
    int panelY = mouseY + offsetY - panelHeight;

    if (panelX + panelWidth > this.width) {
      panelX = mouseX - offsetX - panelWidth;
    }
    if (panelY < 0) {
      panelY = mouseY + offsetY;
    }
    if (panelX < 0) {
      panelX = 0;
    }

    renderTooltipPanel(guiGraphics, panelX, panelY, panelWidth, panelHeight);

    int textCenterX = panelX + panelWidth / 2;

    guiGraphics.drawString(
        this.font,
        spellName,
        textCenterX - nameWidth / 2,
        panelY + 5,
        onCooldown ? 0x888888 : 0xFFFFFF,
        true);

    guiGraphics.drawString(
        this.font,
        description,
        textCenterX - descWidth / 2,
        panelY + 19,
        0xAAAAAA,
        true);

    if (cooldownText != null) {
      guiGraphics.drawString(
          this.font,
          cooldownText,
          textCenterX - cooldownWidth / 2,
          panelY + 33,
          0xFF5555,
          true);
    }
  }

  private void renderTooltipPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
    int bgColor = 0xE0101020;
    guiGraphics.fill(x, y, x + width, y + height, bgColor);

    int borderColor1 = (0xCC << 24) | PRIMARY_COLOR;
    int borderColor2 = (0x88 << 24) | SECONDARY_COLOR;

    // Top
    guiGraphics.fill(x, y, x + width, y + 2, borderColor1);
    // Bottom
    guiGraphics.fill(x, y + height - 2, x + width, y + height, borderColor2);
    // Left
    guiGraphics.fill(x, y, x + 2, y + height, borderColor1);
    // Right
    guiGraphics.fill(x + width - 2, y, x + width, y + height, borderColor2);

    int accentColor = (0xFF << 24) | PRIMARY_COLOR;
    // Top-left
    guiGraphics.fill(x, y, x + 6, y + 2, accentColor);
    guiGraphics.fill(x, y, x + 2, y + 6, accentColor);
    // Top-right
    guiGraphics.fill(x + width - 6, y, x + width, y + 2, accentColor);
    guiGraphics.fill(x + width - 2, y, x + width, y + 6, accentColor);
    // Bottom-left
    guiGraphics.fill(x, y + height - 2, x + 6, y + height, accentColor);
    guiGraphics.fill(x, y + height - 6, x + 2, y + height, accentColor);
    // Bottom-right
    guiGraphics.fill(x + width - 6, y + height - 2, x + width, y + height, accentColor);
    guiGraphics.fill(x + width - 2, y + height - 6, x + width, y + height, accentColor);
  }

  private void renderNoSpellsMessage(GuiGraphics guiGraphics, int centerX, int centerY) {
    Component message = Component.translatable("screen.crawlingmysteries.spell_select.no_spells");
    int messageWidth = this.font.width(message);

    int panelWidth = messageWidth + 10;
    int panelHeight = 20;
    int panelX = centerX - panelWidth / 2;
    int panelY = centerY - panelHeight / 2;

    renderTooltipPanel(guiGraphics, panelX, panelY, panelWidth, panelHeight);

    guiGraphics.drawString(
        this.font,
        message,
        centerX - messageWidth / 2,
        panelY + 6,
        0xAAAAAA,
        true);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button == 0 && hoveredSpell != null && selectedIndex >= 0) {
      if (ClientSpellCooldownManager.isOnCooldown(hoveredSpell)) {
        playCooldownSound();
        return true;
      }

      playSelectSound();

      PacketDistributor.sendToServer(new SpellSelectPacket(hoveredSpell.id()));
      this.onClose();
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  private void playCooldownSound() {
    if (this.minecraft != null) {
      this.minecraft.getSoundManager().play(
          SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5f, 0.5f));
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == KeyMappingsEvents.OPEN_SPELL_MENU.get().getKey().getValue()) {
      this.onClose();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}

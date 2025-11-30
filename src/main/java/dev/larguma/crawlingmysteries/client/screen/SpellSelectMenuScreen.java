package dev.larguma.crawlingmysteries.client.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpellSelectMenuScreen extends Screen {

  // TODO: make art
  @SuppressWarnings("unused")
  private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "textures/gui/spell_menu_background.png");
  @SuppressWarnings("unused")
  private static final ResourceLocation SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "textures/gui/spell_slot.png");
  @SuppressWarnings("unused")
  private static final ResourceLocation SLOT_SELECTED_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "textures/gui/spell_slot_selected.png");

  private static final int SLOT_SIZE = 32;
  private static final int RADIUS = 80;
  private static final int CENTER_DEADZONE = 30;

  private List<Spell> availableSpells;
  private int selectedIndex = -1;
  private Spell hoveredSpell = null;

  public SpellSelectMenuScreen() {
    super(Component.translatable("screen." + CrawlingMysteries.MOD_ID + ".spell_select"));
  }

  @Override
  protected void init() {
    super.init();
    if (this.minecraft != null && this.minecraft.player != null) {
      this.availableSpells = ModSpells.getAvailableSpells(this.minecraft.player);
    } else {
      this.availableSpells = List.of();
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderTransparentBackground(guiGraphics);

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    int centerX = this.width / 2;
    int centerY = this.height / 2;

    updateHoveredSpell(mouseX, mouseY, centerX, centerY);

    renderSpellSlots(guiGraphics, centerX, centerY);
    
    if (hoveredSpell != null) {
      renderSpellInfo(guiGraphics, centerX, centerY);
    } else if (availableSpells.isEmpty()) {
      renderNoSpellsMessage(guiGraphics, centerX, centerY);
    }

  }

  private void updateHoveredSpell(int mouseX, int mouseY, int centerX, int centerY) {
    if (availableSpells.isEmpty()) {
      hoveredSpell = null;
      selectedIndex = -1;
      return;
    }

    // distance and angle from center
    double dx = mouseX - centerX;
    double dy = mouseY - centerY;
    double distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < CENTER_DEADZONE) {
      hoveredSpell = null;
      selectedIndex = -1;
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
  }

  private void renderSpellSlots(GuiGraphics guiGraphics, int centerX, int centerY) {
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

      // calculate position (from top, clockwise)
      double angle = angleStep * i - Math.PI / 2;
      int slotX = centerX + (int) (Math.cos(angle) * RADIUS) - SLOT_SIZE / 2;
      int slotY = centerY + (int) (Math.sin(angle) * RADIUS) - SLOT_SIZE / 2;

      boolean isSelected = (i == selectedIndex);

      int bgColor = isSelected ? 0xCC6B33D7 : 0x80333333;
      guiGraphics.fill(slotX - 2, slotY - 2, slotX + SLOT_SIZE + 2, slotY + SLOT_SIZE + 2, bgColor);
      guiGraphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x80000000);

      renderSpellIcon(guiGraphics, spell, slotX, slotY, isSelected);
    }

    RenderSystem.disableBlend();
  }

  private void renderSpellIcon(GuiGraphics guiGraphics, Spell spell, int x, int y, boolean selected) {
    var itemRegistry = BuiltInRegistries.ITEM;
    var item = itemRegistry.get(spell.sourceItem());

    if (item != null && item != Items.AIR) {
      int itemX = x + (SLOT_SIZE - 16) / 2;
      int itemY = y + (SLOT_SIZE - 16) / 2;
      guiGraphics.renderItem(new ItemStack(item), itemX, itemY);
    } else {
      guiGraphics.fill(x + 4, y + 4, x + SLOT_SIZE - 4, y + SLOT_SIZE - 4, 0xFFFF00FF);
    }

    if (selected) {
      RenderSystem.enableBlend();
      int glowColor = 0x406B33D7;
      guiGraphics.fill(x - 4, y - 4, x + SLOT_SIZE + 4, y + SLOT_SIZE + 4, glowColor);
    }
  }

  private void renderSpellInfo(GuiGraphics guiGraphics, int centerX, int centerY) {
    if (hoveredSpell == null)
      return;

    Component spellName = hoveredSpell.name();
    int nameWidth = this.font.width(spellName);
    guiGraphics.drawString(
        this.font,
        spellName,
        centerX - nameWidth / 2,
        centerY - 10,
        0xFFFFFF,
        true);

    Component description = hoveredSpell.description();
    int descWidth = this.font.width(description);
    guiGraphics.drawString(
        this.font,
        description,
        centerX - descWidth / 2,
        centerY + 100,
        0xAAAAAA,
        true);

    Component hint = Component.translatable("screen." + CrawlingMysteries.MOD_ID + ".spell_select.hint");
    int hintWidth = this.font.width(hint);
    guiGraphics.drawString(
        this.font,
        hint,
        centerX - hintWidth / 2,
        centerY + 5,
        0x888888,
        true);
  }

  private void renderNoSpellsMessage(GuiGraphics guiGraphics, int centerX, int centerY) {
    Component message = Component.translatable("screen." + CrawlingMysteries.MOD_ID + ".spell_select.no_spells");
    int messageWidth = this.font.width(message);
    guiGraphics.drawString(
        this.font,
        message,
        centerX - messageWidth / 2,
        centerY,
        0xAAAAAA,
        true);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button == 0 && hoveredSpell != null) {
      PacketDistributor.sendToServer(new SpellSelectPacket(hoveredSpell.id()));
      this.onClose();
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
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

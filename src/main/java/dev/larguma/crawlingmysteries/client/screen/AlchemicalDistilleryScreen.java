package dev.larguma.crawlingmysteries.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.networking.packet.DistilleryActionPacket;
import dev.larguma.crawlingmysteries.screen.custom.AlchemicalDistilleryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class AlchemicalDistilleryScreen extends AbstractContainerScreen<AlchemicalDistilleryMenu> {
  private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      CrawlingMysteries.MOD_ID, "textures/gui/alchemical_distillery.png");

  private static final int GUI_WIDTH = 181;
  private static final int GUI_HEIGHT = 187;
  private static final int TEXTURE_WIDTH = 256;
  private static final int TEXTURE_HEIGHT = 256;

  private static final int GAUGE_CENTER_X = 138;
  private static final int GAUGE_CENTER_Y = 44;

  private static final int ARROW_U = 186;
  private static final int ARROW_V = 2;
  private static final int ARROW_WIDTH = 16;
  private static final int ARROW_HEIGHT = 15;
  private static final int ARROW_PIVOT_X = 6;
  private static final int ARROW_PIVOT_Y = 10;

  private static final float MAX_NEEDLE_ANGLE = 130.0F;

  private Button startButton;
  private Button stabilizeButton;

  public AlchemicalDistilleryScreen(AlchemicalDistilleryMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = GUI_WIDTH;
    this.imageHeight = GUI_HEIGHT;

    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.inventoryLabelX = 10;
    this.inventoryLabelY = 93;
  }

  @Override
  protected void init() {
    super.init();

    this.startButton = Button.builder(
        Component.translatable("screen.crawlingmysteries.alchemical_distillery.start"),
        button -> onStartPressed())
        .bounds(this.leftPos + 114, this.topPos + 70, 50, 20)
        .build();
    this.addRenderableWidget(this.startButton);

    this.stabilizeButton = Button.builder(
        Component.translatable("screen.crawlingmysteries.alchemical_distillery.stabilize"),
        button -> onStabilizePressed())
        .bounds(this.leftPos + 114, this.topPos + 70, 50, 20)
        .build();
    this.addRenderableWidget(this.stabilizeButton);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    this.renderTooltip(guiGraphics, mouseX, mouseY);

    boolean isCooking = this.menu.isCooking();
    this.startButton.visible = !isCooking;
    this.stabilizeButton.visible = isCooking;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);

    int x = this.leftPos;
    int y = this.topPos;

    guiGraphics.blit(TEXTURE, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    renderNeedle(guiGraphics, x, y);

    if (this.menu.isCooking()) {
      renderProgressIndicator(guiGraphics, x, y);
    }
  }

  private void renderNeedle(GuiGraphics guiGraphics, int guiX, int guiY) {
    int needlePos = this.menu.getNeedlePosition();

    float angle = (needlePos / 100.0F) * MAX_NEEDLE_ANGLE;
    int gaugeCenterX = guiX + GAUGE_CENTER_X;
    int gaugeCenterY = guiY + GAUGE_CENTER_Y;

    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate(gaugeCenterX, gaugeCenterY, 0);
    poseStack.mulPose(Axis.ZP.rotationDegrees(angle));

    guiGraphics.blit(TEXTURE, -ARROW_PIVOT_X, -ARROW_PIVOT_Y, ARROW_U, ARROW_V, ARROW_WIDTH, ARROW_HEIGHT,
        TEXTURE_WIDTH, TEXTURE_HEIGHT);

    poseStack.popPose();
  }

  private void renderProgressIndicator(GuiGraphics guiGraphics, int guiX, int guiY) {
    int progress = this.menu.getCookingProgress();

    String progressText = progress + "%";
    int textX = guiX + GAUGE_CENTER_X + 1 - this.font.width(progressText) / 2;
    int textY = guiY + GAUGE_CENTER_Y + 10;
    guiGraphics.drawString(this.font, progressText, textX, textY, 0xFFFFFF, true);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, this.titleLabelX + 35, this.titleLabelY + 1, 0xcdd6f4, false);
  }

  private void onStartPressed() {
    if (!this.menu.hasValidIngredients()) {
      playErrorSound();
      unfocusButtons();
      return;
    }

    PacketDistributor.sendToServer(
        new DistilleryActionPacket(this.menu.getBlockEntity().getBlockPos(), DistilleryActionPacket.Action.START));
    unfocusButtons();
  }

  private void onStabilizePressed() {
    PacketDistributor.sendToServer(
        new DistilleryActionPacket(this.menu.getBlockEntity().getBlockPos(), DistilleryActionPacket.Action.STABILIZE));
    unfocusButtons();
  }

  private void unfocusButtons() {
    this.startButton.setFocused(false);
    this.stabilizeButton.setFocused(false);
  }

  private void playErrorSound() {
    if (this.minecraft != null) {
      this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F, 0.5F));
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == 32 && this.menu.isCooking()) { // spacebar
      onStabilizePressed();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (this.menu.isCooking() && button == 0) {
      int gaugeX = this.leftPos + GAUGE_CENTER_X;
      int gaugeY = this.topPos + GAUGE_CENTER_Y;
      double distance = Math.sqrt(Math.pow(mouseX - gaugeX, 2) + Math.pow(mouseY - gaugeY, 2));

      if (distance < 30) {
        onStabilizePressed();
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    boolean result = super.mouseReleased(mouseX, mouseY, button);
    // Clear focus from buttons after mouse release to remove white border
    this.setFocused(null);
    return result;
  }
}

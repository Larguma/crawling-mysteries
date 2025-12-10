package dev.larguma.crawlingmysteries.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.particle.FloatingEmberParticle;
import dev.larguma.crawlingmysteries.client.render.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BetterToastOverlay implements LayeredDraw.Layer {

  public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
      "better_toast");

  private static final int PRIMARY_COLOR = 0x9966FF; // Purple
  private static final int ERROR_COLOR = 0xFF6666; // Red
  private static final int SUCCESS_COLOR = 0x66FF99; // Green
  private static final int WARNING_COLOR = 0xFFCC66; // Orange

  private static final float SLIDE_IN_DURATION = 8.0f; // ticks
  private static final float DISPLAY_DURATION = 60.0f; // ticks
  private static final float FADE_OUT_DURATION = 15.0f; // ticks
  private static final float TOTAL_DURATION = SLIDE_IN_DURATION + DISPLAY_DURATION + FADE_OUT_DURATION;

  private static final int PADDING_X = 8;
  private static final int PADDING_Y = 5;
  private static final int MARGIN_TOP = 5;
  private static final int MARGIN_LEFT = 5;
  private static final int TOAST_SPACING = 4;
  private static final int MAX_TOASTS = 5;
  private static final int ICON_SIZE = 16;
  private static final int ICON_PADDING = 4;

  private static float globalAnimationTick = 0;

  private static final List<BetterToast> activeToasts = new ArrayList<>();

  @Override
  public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
    Minecraft minecraft = Minecraft.getInstance();

    if (minecraft.player == null || minecraft.options.hideGui) {
      return;
    }

    if (activeToasts.isEmpty()) {
      return;
    }

    float deltaTicks = deltaTracker.getRealtimeDeltaTicks();
    globalAnimationTick += deltaTicks;

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    int currentY = MARGIN_TOP;

    Iterator<BetterToast> iterator = activeToasts.iterator();
    while (iterator.hasNext()) {
      BetterToast toast = iterator.next();
      toast.tick(deltaTicks);

      if (toast.isExpired()) {
        iterator.remove();
        continue;
      }

      int toastHeight = renderToast(guiGraphics, minecraft, toast, currentY, deltaTicks);
      currentY += toastHeight + TOAST_SPACING;
    }

    RenderSystem.disableBlend();
  }

  private int renderToast(GuiGraphics guiGraphics, Minecraft minecraft, BetterToast toast, int y, float deltaTicks) {
    Font font = minecraft.font;
    String text = toast.getMessage().getString();
    int textWidth = font.width(text);

    boolean hasIcon = toast.hasIcon();
    int iconSpace = hasIcon ? ICON_SIZE + ICON_PADDING : 0;
    int toastWidth = textWidth + PADDING_X * 2 + iconSpace;
    int toastHeight = Math.max(font.lineHeight + PADDING_Y * 2, hasIcon ? ICON_SIZE + PADDING_Y * 2 : 0);

    float slideProgress = toast.getSlideProgress();
    float fadeAlpha = toast.getFadeAlpha();

    if (fadeAlpha <= 0.05f) {
      return toastHeight;
    }

    int x = MARGIN_LEFT;
    int startY = y - toastHeight - 10;
    int targetY = y;

    int currentYPos = (int) (startY + (targetY - startY) * slideProgress);

    int accentColor = toast.getColor();

    renderEtherealGlow(guiGraphics, x, currentYPos, toastWidth, toastHeight, accentColor, fadeAlpha);

    renderFloatingEmber(guiGraphics, x, currentYPos, toastWidth, toastHeight, accentColor, fadeAlpha, toast,
        deltaTicks);

    renderToastPanel(guiGraphics, x, currentYPos, toastWidth, toastHeight, accentColor, fadeAlpha);

    int contentX = x + PADDING_X / 2;
    int textY = currentYPos + (toastHeight - font.lineHeight) / 2 + 2;

    if (hasIcon) {
      int iconY = currentYPos + (toastHeight - ICON_SIZE) / 2;

      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fadeAlpha);
      if (toast.getIconItem().isPresent()) {
        guiGraphics.renderItem(toast.getIconItem().get(), contentX, iconY);
      } else if (toast.getIconTexture().isPresent()) {
        ResourceLocation textureLoc = toast.getIconTexture().get();
        RenderSystem.setShaderTexture(0, textureLoc);
        guiGraphics.blit(textureLoc, contentX, iconY, ICON_SIZE, ICON_SIZE, 0, 0, 16, 16, 16, 16);
      }
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

      contentX += ICON_SIZE + ICON_PADDING;
    }

    int glowColor = RenderUtils.withAlpha(accentColor, fadeAlpha * 0.3f);
    guiGraphics.drawString(font, text, contentX + 1, textY + 1, glowColor, false);
    guiGraphics.drawString(font, text, contentX - 1, textY, glowColor, false);

    int textColor = RenderUtils.withAlpha(0xFFFFFF, fadeAlpha);
    guiGraphics.drawString(font, text, contentX, textY, textColor, true);

    return toastHeight;
  }

  private void renderEtherealGlow(GuiGraphics guiGraphics, int x, int y, int width, int height, int accentColor,
      float alpha) {
    float pulseIntensity = 0.5f + 0.2f * (float) Math.sin(globalAnimationTick * 0.1f);

    for (int i = 3; i >= 1; i--) {
      float glowAlpha = alpha * 0.08f * pulseIntensity * (4 - i) / 3f;
      int glowColor = RenderUtils.withAlpha(accentColor, glowAlpha);
      int offset = i * 2;
      guiGraphics.fill(x - offset, y - offset, x + width + offset, y + height + offset, glowColor);
    }
  }

  private void renderFloatingEmber(GuiGraphics guiGraphics, int x, int y, int width, int height, int accentColor,
      float alpha, BetterToast toast, float deltaTicks) {
    boolean shouldSpawn = toast.getLifetime() > SLIDE_IN_DURATION
        && toast.getLifetime() < SLIDE_IN_DURATION + DISPLAY_DURATION;

    FloatingEmberParticle.updateAndRenderInArea(guiGraphics, toast.getParticles(), x, y, width, height, accentColor,
        deltaTicks, 12, 0.15f, shouldSpawn);
  }

  private void renderToastPanel(GuiGraphics guiGraphics, int x, int y, int width, int height, int accentColor,
      float alpha) {
    float pulse = 0.85f + 0.1f * (float) Math.sin(globalAnimationTick * 0.08f);
    int bgColor = RenderUtils.withAlpha(0x0a0a18, alpha * pulse * 0.85f);
    guiGraphics.fill(x, y, x + width, y + height, bgColor);

    int innerGlow = RenderUtils.withAlpha(accentColor, alpha * 0.08f);
    guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, innerGlow);

    float borderPulse = 0.6f + 0.4f * (float) Math.sin(globalAnimationTick * 0.12f);
    int borderColor1 = RenderUtils.withAlpha(accentColor, alpha * 0.7f * borderPulse);
    int borderColor2 = RenderUtils.withAlpha(accentColor, alpha * 0.4f * borderPulse);

    // top
    guiGraphics.fill(x, y, x + width, y + 1, borderColor1);
    // bottom
    guiGraphics.fill(x, y + height - 1, x + width, y + height, borderColor2);
    // left
    guiGraphics.fill(x, y, x + 1, y + height, borderColor1);
    // right
    guiGraphics.fill(x + width - 1, y, x + width, y + height, borderColor2);

    float cornerPulse = 0.7f + 0.3f * (float) Math.sin(globalAnimationTick * 0.15f + 0.5f);
    int cornerColor = RenderUtils.withAlpha(accentColor, alpha * cornerPulse);
    int cornerGlow = RenderUtils.withAlpha(0xFFFFFF, alpha * 0.5f * cornerPulse);

    // top-left
    guiGraphics.fill(x, y, x + 5, y + 1, cornerColor);
    guiGraphics.fill(x, y, x + 1, y + 5, cornerColor);
    guiGraphics.fill(x + 1, y + 1, x + 2, y + 2, cornerGlow);

    // top-right
    guiGraphics.fill(x + width - 5, y, x + width, y + 1, cornerColor);
    guiGraphics.fill(x + width - 1, y, x + width, y + 5, cornerColor);
    guiGraphics.fill(x + width - 2, y + 1, x + width - 1, y + 2, cornerGlow);

    // bottom-left
    guiGraphics.fill(x, y + height - 1, x + 5, y + height, cornerColor);
    guiGraphics.fill(x, y + height - 5, x + 1, y + height, cornerColor);
    guiGraphics.fill(x + 1, y + height - 2, x + 2, y + height - 1, cornerGlow);

    // bottom-right
    guiGraphics.fill(x + width - 5, y + height - 1, x + width, y + height, cornerColor);
    guiGraphics.fill(x + width - 1, y + height - 5, x + width, y + height, cornerColor);
    guiGraphics.fill(x + width - 2, y + height - 2, x + width - 1, y + height - 1, cornerGlow);

    int glowColor = RenderUtils.withAlpha(accentColor, alpha * 0.2f);
    guiGraphics.fill(x + 2, y + 2, x + width - 2, y + 3, glowColor);

    int accentLine = RenderUtils.withAlpha(accentColor, alpha * 0.25f);
    guiGraphics.fill(x + 3, y + height - 2, x + width - 3, y + height - 1, accentLine);
  }

  public static void showMessage(Component message) {
    showMessage(message, ToastType.INFO, Optional.empty(), Optional.empty());
  }

  public static void showMessage(Component message, ToastType type) {
    showMessage(message, type, Optional.empty(), Optional.empty());
  }

  public static void showMessage(Component message, ToastType type, ItemStack iconItem) {
    showMessage(message, type, Optional.of(iconItem), Optional.empty());
  }

  public static void showMessage(Component message, ToastType type, ResourceLocation iconTexture) {
    showMessage(message, type, Optional.empty(), Optional.of(iconTexture));
  }

  public static void showMessage(Component message, ToastType type, Optional<ItemStack> iconItem,
      Optional<ResourceLocation> iconTexture) {
    if (activeToasts.size() >= MAX_TOASTS) {
      activeToasts.remove(0);
    }
    activeToasts.add(new BetterToast(message, type, iconItem, iconTexture));
  }

  public static void clear() {
    activeToasts.clear();
  }

  public enum ToastType {
    INFO(PRIMARY_COLOR),
    SUCCESS(SUCCESS_COLOR),
    WARNING(WARNING_COLOR),
    ERROR(ERROR_COLOR);

    private final int color;

    ToastType(int color) {
      this.color = color;
    }

    public int getColor() {
      return color;
    }
  }

  private static class BetterToast {
    private final Component message;
    private final ToastType type;
    private final Optional<ItemStack> iconItem;
    private final Optional<ResourceLocation> iconTexture;
    private float lifetime;
    private final List<FloatingEmberParticle> particles;

    public BetterToast(Component message, ToastType type, Optional<ItemStack> iconItem,
        Optional<ResourceLocation> iconTexture) {
      this.message = message;
      this.type = type;
      this.iconItem = iconItem;
      this.iconTexture = iconTexture;
      this.lifetime = 0;
      this.particles = FloatingEmberParticle.createList();
    }

    public void tick(float deltaTicks) {
      lifetime += deltaTicks;
    }

    public List<FloatingEmberParticle> getParticles() {
      return particles;
    }

    public float getLifetime() {
      return lifetime;
    }

    public boolean isExpired() {
      return lifetime >= TOTAL_DURATION;
    }

    public Component getMessage() {
      return message;
    }

    public int getColor() {
      return type.getColor();
    }

    public boolean hasIcon() {
      return iconItem.isPresent() || iconTexture.isPresent();
    }

    public Optional<ItemStack> getIconItem() {
      return iconItem;
    }

    public Optional<ResourceLocation> getIconTexture() {
      return iconTexture;
    }

    public float getSlideProgress() {
      if (lifetime < SLIDE_IN_DURATION) {
        float t = lifetime / SLIDE_IN_DURATION;
        // Ease out cubic
        return 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
      }
      return 1.0f;
    }

    public float getFadeAlpha() {
      if (lifetime < SLIDE_IN_DURATION) {
        // Fade in during slide
        return Math.min(1.0f, lifetime / (SLIDE_IN_DURATION * 0.5f));
      } else if (lifetime > SLIDE_IN_DURATION + DISPLAY_DURATION) {
        // Fade out
        float fadeProgress = (lifetime - SLIDE_IN_DURATION - DISPLAY_DURATION) / FADE_OUT_DURATION;
        return 1.0f - Math.min(1.0f, fadeProgress);
      }
      return 1.0f;
    }
  }
}

package dev.larguma.crawlingmysteries.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
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

      int toastHeight = renderToast(guiGraphics, minecraft, toast, currentY);
      currentY += toastHeight + TOAST_SPACING;
    }

    RenderSystem.disableBlend();
  }

  private int renderToast(GuiGraphics guiGraphics, Minecraft minecraft, BetterToast toast, int y) {
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

    int textColor = RenderUtils.withAlpha(0xFFFFFF, fadeAlpha);
    guiGraphics.drawString(font, text, contentX, textY, textColor, true);

    return toastHeight;
  }

  private void renderToastPanel(GuiGraphics guiGraphics, int x, int y, int width, int height, int accentColor,
      float alpha) {
    int bgColor = RenderUtils.withAlpha(0x101020, alpha * 0.9f);
    guiGraphics.fill(x, y, x + width, y + height, bgColor);

    int borderColor1 = RenderUtils.withAlpha(accentColor, alpha * 0.8f);
    int borderColor2 = RenderUtils.withAlpha(accentColor, alpha * 0.5f);

    // top
    guiGraphics.fill(x, y, x + width, y + 1, borderColor1);
    // bottom
    guiGraphics.fill(x, y + height - 1, x + width, y + height, borderColor2);
    // left
    guiGraphics.fill(x, y, x + 1, y + height, borderColor1);
    // right
    guiGraphics.fill(x + width - 1, y, x + width, y + height, borderColor2);

    int cornerColor = RenderUtils.withAlpha(accentColor, alpha);
    // top-left
    guiGraphics.fill(x, y, x + 4, y + 1, cornerColor);
    guiGraphics.fill(x, y, x + 1, y + 4, cornerColor);
    // top-right
    guiGraphics.fill(x + width - 4, y, x + width, y + 1, cornerColor);
    guiGraphics.fill(x + width - 1, y, x + width, y + 4, cornerColor);
    // bottom-left
    guiGraphics.fill(x, y + height - 1, x + 4, y + height, cornerColor);
    guiGraphics.fill(x, y + height - 4, x + 1, y + height, cornerColor);
    // bottom-right
    guiGraphics.fill(x + width - 4, y + height - 1, x + width, y + height, cornerColor);
    guiGraphics.fill(x + width - 1, y + height - 4, x + width, y + height, cornerColor);

    int glowColor = RenderUtils.withAlpha(accentColor, alpha * 0.15f);
    guiGraphics.fill(x + 2, y + 2, x + width - 2, y + 4, glowColor);
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

    public BetterToast(Component message, ToastType type, Optional<ItemStack> iconItem,
        Optional<ResourceLocation> iconTexture) {
      this.message = message;
      this.type = type;
      this.iconItem = iconItem;
      this.iconTexture = iconTexture;
      this.lifetime = 0;
    }

    public void tick(float deltaTicks) {
      lifetime += deltaTicks;
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

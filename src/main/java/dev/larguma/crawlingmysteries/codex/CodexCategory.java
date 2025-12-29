package dev.larguma.crawlingmysteries.codex;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Category for organizing codex entries.
 * Loaded from JSON file.
 */
public class CodexCategory {
  private final String id;
  private final int color;

  public CodexCategory(String id, int color) {
    this.id = id;
    this.color = color;
  }

  public String getId() {
    return id;
  }

  public int getColor() {
    return color;
  }

  public Component getName() {
    return Component.translatable("codex.crawlingmysteries.category." + id);
  }

  public Component getDescription() {
    return Component.translatable("codex.crawlingmysteries.category." + id + ".desc");
  }

  public ResourceLocation getIcon() {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
        "textures/gui/codex/category/" + id + ".png");
  }
}
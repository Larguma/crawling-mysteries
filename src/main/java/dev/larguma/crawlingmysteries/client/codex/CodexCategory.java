package dev.larguma.crawlingmysteries.client.codex;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Categories for organizing codex entries.
 */
public enum CodexCategory {
  TRINKETS("trinkets", 0x6B33D7),
  SPELLS("spells", 0x32CD32),
  LORE("lore", 0x9B59B6),
  BESTIARY("bestiary", 0xFF6347);

  private final String id;
  private final int color;

  CodexCategory(String id, int color) {
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

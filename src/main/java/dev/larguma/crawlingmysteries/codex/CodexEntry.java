package dev.larguma.crawlingmysteries.codex;

import java.util.List;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a single entry in the Cryptic Codex wiki.
 */
public record CodexEntry(
    String id,
    CodexCategory category,
    Component title,
    Component subtitle,
    ResourceLocation icon,
    List<CodexPage> pages,
    String unlockCondition) {

  public static Builder builder(String id, CodexCategory category) {
    return new Builder(id, category);
  }

  public ResourceLocation getRegistryId() {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id);
  }

  public static class Builder {
    private final String id;
    private final CodexCategory category;
    private Component title;
    private Component subtitle = Component.empty();
    private ResourceLocation icon;
    private List<CodexPage> pages = List.of();
    private String unlockCondition = "ALWAYS";

    private Builder(String id, CodexCategory category) {
      this.id = id;
      this.category = category;
      this.title = Component.translatable("codex.crawlingmysteries.entry." + id);
      this.icon = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
          "textures/gui/codex/entry_" + id + ".png");
    }

    public Builder title(Component title) {
      this.title = title;
      return this;
    }

    public Builder title(String translationKey) {
      this.title = Component.translatable(translationKey);
      return this;
    }

    public Builder subtitle(Component subtitle) {
      this.subtitle = subtitle;
      return this;
    }

    public Builder subtitle(String translationKey) {
      this.subtitle = Component.translatable(translationKey);
      return this;
    }

    public Builder icon(ResourceLocation icon) {
      this.icon = icon;
      return this;
    }

    public Builder icon(String path) {
      this.icon = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, path);
      return this;
    }

    public Builder pages(CodexPage... pages) {
      this.pages = List.of(pages);
      return this;
    }

    public Builder pages(List<CodexPage> pages) {
      this.pages = pages;
      return this;
    }

    public Builder unlockCondition(String condition) {
      this.unlockCondition = condition;
      return this;
    }

    public CodexEntry build() {
      return new CodexEntry(id, category, title, subtitle, icon, pages, unlockCondition);
    }
  }
}

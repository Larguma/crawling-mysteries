package dev.larguma.crawlingmysteries.codex;

import net.minecraft.network.chat.Component;

/**
 * Represents a single page of content within a codex entry.
 */
public record CodexPage(PageType type, Component content, String extraData) {

  public enum PageType {
    TEXT,
    IMAGE,
    ITEM_SHOWCASE,
    SPELL_INFO,
    CRAFTING,
    ENTITY_DISPLAY
  }
}

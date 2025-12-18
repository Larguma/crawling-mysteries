package dev.larguma.crawlingmysteries.client.codex;

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

  public static CodexPage text(String translationKey) {
    return new CodexPage(PageType.TEXT, Component.translatable(translationKey), null);
  }

  public static CodexPage text(Component content) {
    return new CodexPage(PageType.TEXT, content, null);
  }

  /**
   * Creates an image page.
   * 
   * @param imagePath The image path (e.g., "textures/gui/codex/images/origins_illustration.png")
   */
  public static CodexPage image(String imagePath) {
    return new CodexPage(PageType.IMAGE, Component.empty(), imagePath);
  }

  /**
   * Creates a page that showcases an item with its GUI model.
   * 
   * @param itemId The item ID (e.g., "crawlingmysteries:cryptic_eye")
   */
  public static CodexPage itemShowcase(String itemId) {
    return new CodexPage(PageType.ITEM_SHOWCASE, Component.empty(), itemId);
  }

  /**
   * Creates a page that displays information about a spell.
   * 
   * @param spellId The spell ID (e.g., "crawlingmysteries:be_totem")
   */
  public static CodexPage spellInfo(String spellId) {
    return new CodexPage(PageType.SPELL_INFO, Component.empty(), spellId);
  }

  /**
   * Creates a page that displays a 3D animated entity model.
   * 
   * @param entityId The entity type ID (e.g.,
   *                 "crawlingmysteries:eternal_guardian")
   */
  public static CodexPage entityDisplay(String entityId) {
    return new CodexPage(PageType.ENTITY_DISPLAY, Component.empty(), entityId);
  }

  /**
   * Creates a page that displays a 3D animated entity model with a description.
   * 
   * @param entityId    The entity type ID (e.g.,
   *                    "crawlingmysteries:eternal_guardian")
   * @param description The description content to display below the entity
   */
  public static CodexPage entityDisplay(String entityId, Component description) {
    return new CodexPage(PageType.ENTITY_DISPLAY, description, entityId);
  }
}

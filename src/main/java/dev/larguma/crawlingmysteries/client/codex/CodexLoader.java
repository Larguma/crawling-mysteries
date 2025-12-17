package dev.larguma.crawlingmysteries.client.codex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.codex.CodexPage.PageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Loads codex entries from JSON files in assets/crawlingmysteries/codex/
 * 
 * JSON Structure:
 * {
 * "id": "cryptic_eye",
 * "category": "trinkets",
 * "title": "Cryptic Eye",
 * "subtitle": "A watchful companion",
 * "icon": "textures/item/cryptic_eye_2d.png",
 * "unlock_condition": "ALWAYS",
 * "pages": [
 * {
 * "type": "text",
 * "content": "The Cryptic Eye appeared with you..."
 * },
 * {
 * "type": "item_showcase",
 * "item": "crawlingmysteries:cryptic_eye"
 * }
 * ]
 * }
 */
public class CodexLoader {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String CODEX_PATH = "codex";

  /**
   * Loads all codex entries from the resource pack.
   */
  public static List<CodexEntry> loadAllEntries() {
    List<CodexEntry> entries = new ArrayList<>();
    ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

    // Find all JSON files in the codex directory
    Map<ResourceLocation, Resource> resources = resourceManager.listResources(CODEX_PATH,
        path -> path.getPath().endsWith(".json"));

    for (Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
      ResourceLocation location = entry.getKey();
      Resource resource = entry.getValue();

      try {
        Optional<CodexEntry> codexEntry = loadEntry(resource, location);
        codexEntry.ifPresent(entries::add);
      } catch (Exception e) {
        CrawlingMysteries.LOGGER.error("Failed to load codex entry from {}: {}", location, e.getMessage());
      }
    }

    CrawlingMysteries.LOGGER.info("Loaded {} codex entries", entries.size());
    return entries;
  }

  /**
   * Loads a single codex entry from a resource.
   */
  private static Optional<CodexEntry> loadEntry(Resource resource, ResourceLocation location) {
    try (InputStream inputStream = resource.open();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      JsonObject json = GSON.fromJson(reader, JsonObject.class);
      return Optional.of(parseEntry(json, location));

    } catch (IOException e) {
      CrawlingMysteries.LOGGER.error("Error reading codex file {}: {}", location, e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Parses a JsonObject into a CodexEntry.
   */
  private static CodexEntry parseEntry(JsonObject json, ResourceLocation source) {
    String id = getStringOrDefault(json, "id", source.getPath().replace(CODEX_PATH + "/", "").replace(".json", ""));
    String categoryStr = getStringOrDefault(json, "category", "trinkets");
    CodexCategory category = parseCategory(categoryStr);

    String title = getStringOrDefault(json, "title", id);
    String subtitle = getStringOrDefault(json, "subtitle", "");
    String iconPath = getStringOrDefault(json, "icon", "textures/gui/codex/entry_" + id + ".png");
    String unlockStr = getStringOrDefault(json, "unlock_condition", "ALWAYS");

    ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, iconPath);

    List<CodexPage> pages = new ArrayList<>();
    if (json.has("pages") && json.get("pages").isJsonArray()) {
      JsonArray pagesArray = json.getAsJsonArray("pages");
      for (JsonElement pageElement : pagesArray) {
        if (pageElement.isJsonObject()) {
          pages.add(parsePage(pageElement.getAsJsonObject()));
        }
      }
    }

    return new CodexEntry(
        id,
        category,
        Component.literal(title),
        Component.literal(subtitle),
        icon,
        pages,
        unlockStr);
  }

  /**
   * Parses a page from JSON.
   */
  private static CodexPage parsePage(JsonObject json) {
    PageType type;
    String typeStr = getStringOrDefault(json, "type", "text");
    String extraData = getStringOrDefault(json, "extra", "");
    String content = getStringOrDefault(json, "content", "");

    switch (typeStr.toLowerCase()) {
      case "crafting" -> type = PageType.CRAFTING;
      case "entity_display" -> type = PageType.ENTITY_DISPLAY;
      case "image" -> type = PageType.IMAGE;
      case "item_showcase" -> type = PageType.ITEM_SHOWCASE;
      case "spell_info" -> type = PageType.SPELL_INFO;
      default -> type = PageType.TEXT;
    }

    return new CodexPage(type, Component.literal(content), extraData);
  }

  /**
   * Parses a category string to enum.
   */
  private static CodexCategory parseCategory(String category) {
    // TODO: find how to do that dynamically
    return switch (category.toLowerCase()) {
      case "trinkets" -> CodexCategory.TRINKETS;
      case "spells" -> CodexCategory.SPELLS;
      case "lore" -> CodexCategory.LORE;
      case "bestiary" -> CodexCategory.BESTIARY;
      default -> CodexCategory.TRINKETS;
    };
  }

  /**
   * Helper to get a string from JSON with a default value.
   */
  private static String getStringOrDefault(JsonObject json, String key, String defaultValue) {
    if (json.has(key) && json.get(key).isJsonPrimitive()) {
      return json.get(key).getAsString();
    }
    return defaultValue;
  }
}

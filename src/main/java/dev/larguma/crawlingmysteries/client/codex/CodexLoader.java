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
import dev.larguma.crawlingmysteries.client.codex.CodexEntry.UnlockCondition;
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
    UnlockCondition unlockCondition = parseUnlockCondition(unlockStr);

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
        unlockCondition);
  }

  /**
   * Parses a page from JSON.
   */
  private static CodexPage parsePage(JsonObject json) {
    String typeStr = getStringOrDefault(json, "type", "text");
    PageType type = parsePageType(typeStr);

    String content = getStringOrDefault(json, "content", "");
    String extraData = null;

    //TODO: find how to do that dynamically
    switch (type) {
      case ITEM_SHOWCASE -> extraData = getStringOrDefault(json, "item", "");
      case SPELL_INFO -> extraData = getStringOrDefault(json, "spell", "");
      case IMAGE -> extraData = getStringOrDefault(json, "image", "");
      case CRAFTING -> extraData = getStringOrDefault(json, "recipe", "");
      case ENTITY_DISPLAY -> extraData = getStringOrDefault(json, "entity", "");
      default -> {
      }
    }

    return new CodexPage(type, Component.literal(content), extraData);
  }

  /**
   * Parses a category string to enum.
   */
  private static CodexCategory parseCategory(String category) {
    //TODO: find how to do that dynamically
    return switch (category.toLowerCase()) {
      case "trinkets" -> CodexCategory.TRINKETS;
      case "spells" -> CodexCategory.SPELLS;
      case "lore" -> CodexCategory.LORE;
      case "bestiary" -> CodexCategory.BESTIARY;
      default -> CodexCategory.TRINKETS;
    };
  }

  /**
   * Parses an unlock condition string to enum.
   */
  private static UnlockCondition parseUnlockCondition(String condition) {
    //TODO: find how to do that dynamically
    return switch (condition.toUpperCase()) {
      case "HAS_CRYPTIC_EYE" -> UnlockCondition.HAS_CRYPTIC_EYE;
      case "HAS_ETERNAL_GUARDIANS_BAND" -> UnlockCondition.HAS_ETERNAL_GUARDIANS_BAND;
      case "HAS_ETERNAL_GUARDIAN_MASK" -> UnlockCondition.HAS_ETERNAL_GUARDIAN_MASK;
      case "KILLED_ETERNAL_GUARDIAN" -> UnlockCondition.KILLED_ETERNAL_GUARDIAN;
      case "DISCOVERED" -> UnlockCondition.DISCOVERED;
      default -> UnlockCondition.ALWAYS;
    };
  }

  /**
   * Parses a page type string to enum.
   */
  private static PageType parsePageType(String type) {
    //TODO: find how to do that dynamically
    return switch (type.toLowerCase()) {
      case "image" -> PageType.IMAGE;
      case "item_showcase", "item" -> PageType.ITEM_SHOWCASE;
      case "spell_info", "spell" -> PageType.SPELL_INFO;
      case "crafting", "recipe" -> PageType.CRAFTING;
      case "entity_display", "entity" -> PageType.ENTITY_DISPLAY;
      default -> PageType.TEXT;
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

package dev.larguma.crawlingmysteries.codex;

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
import dev.larguma.crawlingmysteries.codex.CodexPage.PageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Loads codex entries from JSON files in assets/crawlingmysteries/codex/
 */
public class CodexLoader {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String CODEX_PATH = "codex";

  // #region Loading

  /**
   * Loads all codex entries from the resource pack.
   */
  public static List<CodexEntry> loadAllEntries(List<CodexCategory> availableCategories) {
    List<CodexEntry> entries = new ArrayList<>();
    ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

    // Find all JSON files in the codex directory, excluding categories.json
    Map<ResourceLocation, Resource> resources = resourceManager.listResources(CODEX_PATH,
        path -> path.getPath().endsWith(".json") && !path.getPath().endsWith("/categories.json"));

    for (Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
      ResourceLocation location = entry.getKey();
      Resource resource = entry.getValue();

      try {
        Optional<CodexEntry> codexEntry = loadEntry(resource, location, availableCategories);
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
  private static Optional<CodexEntry> loadEntry(Resource resource, ResourceLocation location,
      List<CodexCategory> categories) {
    try (InputStream inputStream = resource.open();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      JsonObject json = GSON.fromJson(reader, JsonObject.class);
      return Optional.of(parseEntry(json, location, categories));

    } catch (IOException e) {
      CrawlingMysteries.LOGGER.error("Error reading codex file {}: {}", location, e.getMessage());
      return Optional.empty();
    }
  }

  public static List<CodexCategory> loadAllCategories() {
    List<CodexCategory> categories = new ArrayList<>();
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
        CODEX_PATH + "/categories.json");

    Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(location);
    if (resource.isPresent()) {
      try (InputStream inputStream = resource.get().open();
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

        JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
        for (JsonElement element : jsonArray) {
          if (element.isJsonObject()) {
            categories.add(parseCategoryObject(element.getAsJsonObject()));
          }
        }
      } catch (Exception e) {
        CrawlingMysteries.LOGGER.error("Failed to load codex categories from {}: {}", location, e.getMessage());
      }
    } else {
      CrawlingMysteries.LOGGER.warn("Codex categories file not found: {}", location);
    }

    CrawlingMysteries.LOGGER.info("Loaded {} codex categories", categories.size());
    return categories;
  }

  // #endregion Loading

  // #region Parsing

  /**
   * Parses a JsonObject into a CodexEntry.
   */
  private static CodexEntry parseEntry(JsonObject json, ResourceLocation source, List<CodexCategory> categories) {
    String id = getStringOrDefault(json, "id", source.getPath().replace(CODEX_PATH + "/", "").replace(".json", ""));
    String categoryStr = getStringOrDefault(json, "category", "trinkets");
    CodexCategory category = parseCategory(categoryStr, categories);

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

    return new CodexEntry(id, category, Component.translatable(title), Component.translatable(subtitle), icon, pages, unlockStr);
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

    return new CodexPage(type, Component.translatable(content), extraData);
  }

  /**
   * Parses a category string to category object.
   */
  private static CodexCategory parseCategory(String categoryId, List<CodexCategory> categories) {
    return categories.stream()
        .filter(c -> c.getId().equalsIgnoreCase(categoryId))
        .findFirst().orElseGet(() -> {
          if (!categories.isEmpty())
            return categories.get(0);
          return new CodexCategory("unknown", 0xFFFFFF);
        });
  }

  /**
   * Parses a category from JSON object.
   */
  private static CodexCategory parseCategoryObject(JsonObject json) {
    String id = getStringOrDefault(json, "name", "unknown");
    String colorStr = getStringOrDefault(json, "color", "0xFFFFFF");

    int color = 0xFFFFFF;
    try {
      color = Integer.decode(colorStr);
    } catch (NumberFormatException e) {
      CrawlingMysteries.LOGGER.warn("Invalid color format for category {}: {}", id, colorStr);
    }

    return new CodexCategory(id, color);
  }

  // #endregion Parsing

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

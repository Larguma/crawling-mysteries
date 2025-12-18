package dev.larguma.crawlingmysteries.client.codex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.resources.ResourceLocation;

/**
 * Registry for all Cryptic Codex entries.
 * Entries are loaded from JSON files in assets/crawlingmysteries/codex/
 */
public class CodexRegistry {

  private static final Map<ResourceLocation, CodexEntry> ENTRIES = new HashMap<>();
  private static final Map<CodexCategory, List<CodexEntry>> ENTRIES_BY_CATEGORY = new HashMap<>();
  private static List<CodexCategory> CATEGORIES = new ArrayList<>();
  private static boolean initialized = false;

  /**
   * Initialize the registry. Must be called after resources are available.
   * This is called lazily when entries are first accessed.
   */
  public static void init() {
    if (initialized) {
      return;
    }

    ENTRIES.clear();
    ENTRIES_BY_CATEGORY.clear();
    CATEGORIES.clear();

    CATEGORIES = CodexLoader.loadAllCategories();
    for (CodexCategory category : CATEGORIES) {
      ENTRIES_BY_CATEGORY.put(category, new ArrayList<>());
    }

    List<CodexEntry> loadedEntries = CodexLoader.loadAllEntries(CATEGORIES);
    for (CodexEntry entry : loadedEntries) {
      register(entry);
    }

    initialized = true;
    CrawlingMysteries.LOGGER.info("Codex registry initialized with {} entries and {} categories", ENTRIES.size(),
        CATEGORIES.size());
  }

  /**
   * Force reload all entries from JSON files.
   */
  public static void reload() {
    initialized = false;
    init();
  }

  private static void register(CodexEntry entry) {
    ResourceLocation id = entry.getRegistryId();
    ENTRIES.put(id, entry);
    // Ensure the list exists even if category wasn't in initial load list (edge
    // case)
    ENTRIES_BY_CATEGORY.computeIfAbsent(entry.category(), k -> new ArrayList<>()).add(entry);
    CrawlingMysteries.LOGGER.debug("Registered codex entry: {}", id);
  }

  public static Optional<CodexEntry> get(ResourceLocation id) {
    ensureInitialized();
    return Optional.ofNullable(ENTRIES.get(id));
  }

  public static Optional<CodexEntry> get(String id) {
    return get(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, id));
  }

  public static List<CodexEntry> getAll() {
    ensureInitialized();
    return new ArrayList<>(ENTRIES.values());
  }

  public static List<CodexEntry> getByCategory(CodexCategory category) {
    ensureInitialized();
    return new ArrayList<>(ENTRIES_BY_CATEGORY.getOrDefault(category, List.of()));
  }

  public static List<CodexCategory> getCategories() {
    ensureInitialized();
    return new ArrayList<>(CATEGORIES);
  }

  public static int getEntryCount() {
    ensureInitialized();
    return ENTRIES.size();
  }

  public static int getEntryCount(CodexCategory category) {
    ensureInitialized();
    return ENTRIES_BY_CATEGORY.getOrDefault(category, List.of()).size();
  }

  private static void ensureInitialized() {
    if (!initialized) {
      init();
    }
  }
}
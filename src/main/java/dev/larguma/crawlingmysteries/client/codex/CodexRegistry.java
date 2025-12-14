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
  private static boolean initialized = false;

  /**
   * Initialize the registry. Must be called after resources are available.
   * This is called lazily when entries are first accessed.
   */
  public static void init() {
    if (initialized) {
      return;
    }

    // Clear existing entries
    ENTRIES.clear();
    ENTRIES_BY_CATEGORY.clear();

    // Initialize category lists
    for (CodexCategory category : CodexCategory.values()) {
      ENTRIES_BY_CATEGORY.put(category, new ArrayList<>());
    }

    // Load entries from JSON files
    List<CodexEntry> loadedEntries = CodexLoader.loadAllEntries();
    for (CodexEntry entry : loadedEntries) {
      register(entry);
    }

    initialized = true;
    CrawlingMysteries.LOGGER.info("Codex registry initialized with {} entries", ENTRIES.size());
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
    ENTRIES_BY_CATEGORY.get(entry.category()).add(entry);
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

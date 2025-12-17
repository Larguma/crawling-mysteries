package dev.larguma.crawlingmysteries.client.codex;

import java.util.HashSet;
import java.util.Set;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import dev.larguma.crawlingmysteries.networking.packet.UnlockCodexEntryPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class CodexUnlockManager {

  // Client-side cache of unlocked entries, synced from server
  private static Set<String> clientUnlockedEntries = new HashSet<>();

  /**
   * Called by the client when receiving a sync packet from the server.
   */
  public static void setUnlockedEntries(Set<String> entries) {
    clientUnlockedEntries = new HashSet<>(entries);
  }

  /**
   * Checks if a codex entry is unlocked and marks it as permanently unlocked if conditions are met.
   */
  public static boolean isUnlocked(Player player, CodexEntry entry) {
    String condition = entry.unlockCondition();
    if (condition == null || condition.equalsIgnoreCase("ALWAYS")) {
      return true;
    }

    String entryId = entry.id();
    if (clientUnlockedEntries.contains(entryId)) {
      return true;
    }

    String[] parts = condition.split("_", 2);
    if (parts.length < 2) {
      return false;
    }

    String type = parts[0];
    String value = parts[1];

    boolean unlocked = switch (type.toUpperCase()) {
      case "HAS" -> checkHasItem(player, value, entryId);
      case "KILLED" -> checkKilledEntity(player, value, entryId);
      default -> false;
    };

    return unlocked;
  }

  private static boolean checkHasItem(Player player, String itemId, String entryId) {
    var item = BuiltInRegistries.ITEM
        .get(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, itemId.toLowerCase()));
    
    boolean hasItem = ItemHelper.hasItem(player, item);

    if (hasItem) {
      markAsUnlocked(entryId);
      return true;
    }

    // Check stats as fallback
    if (player instanceof LocalPlayer localPlayer) {
      try {
        boolean hasInteracted = localPlayer.getStats().getValue(Stats.ITEM_PICKED_UP, item) > 0
            || localPlayer.getStats().getValue(Stats.ITEM_DROPPED, item) > 0
            || localPlayer.getStats().getValue(Stats.ITEM_USED, item) > 0
            || localPlayer.getStats().getValue(Stats.ITEM_CRAFTED, item) > 0
            || localPlayer.getStats().getValue(Stats.ITEM_BROKEN, item) > 0;
        
        if (hasInteracted) {
          markAsUnlocked(entryId);
          return true;
        }
      } catch (Exception e) {
        CrawlingMysteries.LOGGER.error("Failed to check statistic for item {}", itemId, e);
      }
    }

    return false;
  }

  private static boolean checkKilledEntity(Player player, String entityId, String entryId) {
    if (player instanceof LocalPlayer localPlayer) {
      try {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, entityId.toLowerCase()));
        boolean hasKilled = localPlayer.getStats().getValue(Stats.ENTITY_KILLED, entityType) > 0;
        
        if (hasKilled) {
          markAsUnlocked(entryId);
          return true;
        }
      } catch (Exception e) {
        CrawlingMysteries.LOGGER.error("Failed to check statistic for entity {}", entityId, e);
      }
    }
    return false;
  }

  /**
   * Marks an entry as permanently unlocked by sending a packet to the server.
   */
  private static void markAsUnlocked(String entryId) {
    if (!clientUnlockedEntries.contains(entryId)) {
      clientUnlockedEntries.add(entryId);
      PacketDistributor.sendToServer(new UnlockCodexEntryPacket(entryId));
    }
  }

  /**
   * Clears the client-side cache. Called when disconnecting from a server.
   */
  public static void clearCache() {
    clientUnlockedEntries.clear();
  }
}

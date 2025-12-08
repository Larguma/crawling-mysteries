package dev.larguma.crawlingmysteries.client.spell;

import java.util.HashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.spell.Spell;

public class ClientSpellCooldownManager {

  private static final Map<String, Long> cooldownEnds = new HashMap<>();
  private static final Map<String, Integer> cooldownDurations = new HashMap<>();

  /**
   * Called when receiving cooldown sync from server.
   */
  public static void setCooldown(String spellId, long remainingTicks, int totalTicks) {
    long currentTime = System.currentTimeMillis();
    long remainingMs = remainingTicks * 50; // 1 tick = 50ms
    cooldownEnds.put(spellId, currentTime + remainingMs);
    cooldownDurations.put(spellId, totalTicks);
  }

  public static boolean isOnCooldown(Spell spell) {
    if (spell == null) {
      return false;
    }
    return isOnCooldown(spell.id());
  }

  public static boolean isOnCooldown(String spellId) {
    Long endTime = cooldownEnds.get(spellId);
    if (endTime == null) {
      return false;
    }
    return System.currentTimeMillis() < endTime;
  }

  public static long getRemainingCooldownTicks(Spell spell) {
    if (spell == null) {
      return 0;
    }
    return getRemainingCooldownTicks(spell.id());
  }

  public static long getRemainingCooldownTicks(String spellId) {
    Long endTime = cooldownEnds.get(spellId);
    if (endTime == null) {
      return 0;
    }
    long remainingMs = endTime - System.currentTimeMillis();
    return Math.max(0, remainingMs / 50);
  }

  public static int getRemainingCooldownSeconds(Spell spell) {
    long ticks = getRemainingCooldownTicks(spell);
    return (int) Math.ceil(ticks / 20.0);
  }

  public static String getRemainingCooldownFormatted(Spell spell) {
    int seconds = getRemainingCooldownSeconds(spell);
    if (seconds <= 0) {
      return "0s";
    } else if (seconds >= 60) {
      int mins = seconds / 60;
      int secs = seconds % 60;
      return String.format("%dm %ds", mins, secs);
    } else if (seconds >= 3600) {
      int hours = seconds / 3600;
      int mins = (seconds % 3600) / 60;
      return String.format("%dh %dm", hours, mins);
    }
    return String.format("%ds", seconds);
  }

  /**
   * Get cooldown progress (0.0 = ready, 1.0 = just started cooldown).
   */
  public static float getCooldownProgress(Spell spell) {
    if (!isOnCooldown(spell)) {
      return 0.0f;
    }

    Integer totalTicks = cooldownDurations.get(spell.id());
    if (totalTicks == null || totalTicks <= 0) {
      return 0.0f;
    }

    long remainingTicks = getRemainingCooldownTicks(spell);
    return Math.min(1.0f, (float) remainingTicks / totalTicks);
  }

  public static void clearCooldown(String spellId) {
    cooldownEnds.remove(spellId);
    cooldownDurations.remove(spellId);
  }

  public static void clearAllCooldowns() {
    cooldownEnds.clear();
    cooldownDurations.clear();
  }
}

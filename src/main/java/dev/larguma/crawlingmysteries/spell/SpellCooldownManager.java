package dev.larguma.crawlingmysteries.spell;

import java.util.HashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.networking.packet.SpellCooldownSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpellCooldownManager {

  public static boolean isOnCooldown(ServerPlayer player, Spell spell) {
    if (spell.cooldownTicks() <= 0) {
      return false;
    }

    Map<String, Long> cooldowns = player.getData(ModDataAttachments.SPELL_COOLDOWNS);
    Long cooldownEnd = cooldowns.get(spell.id());

    if (cooldownEnd == null) {
      return false;
    }

    long currentTick = player.level().getGameTime();
    return currentTick < cooldownEnd;
  }

  public static long getRemainingCooldown(ServerPlayer player, Spell spell) {
    if (spell.cooldownTicks() <= 0) {
      return 0;
    }

    Map<String, Long> cooldowns = player.getData(ModDataAttachments.SPELL_COOLDOWNS);
    Long cooldownEnd = cooldowns.get(spell.id());

    if (cooldownEnd == null) {
      return 0;
    }

    long currentTick = player.level().getGameTime();
    return Math.max(0, cooldownEnd - currentTick);
  }

  public static int getRemainingCooldownSeconds(ServerPlayer player, Spell spell) {
    long ticks = getRemainingCooldown(player, spell);
    return (int) Math.ceil(ticks / 20.0);
  }

  public static String getRemainingCooldownFormatted(ServerPlayer player, Spell spell) {
    return cooldownFormatted(getRemainingCooldownSeconds(player, spell));
  }

  public static String getTotalCooldownFormatted(Spell spell) {
    return cooldownFormatted(spell.cooldownTicks() / 20);
  }

  public static String cooldownFormatted(int seconds) {
    if (seconds <= 0) {
      return "0s";
    } else if (seconds >= 3600) {
      int hours = seconds / 3600;
      int mins = (seconds % 3600) / 60;
      if (mins == 0) {
        return String.format("%dh", hours);
      }
      return String.format("%dh %dm", hours, mins);
    } else if (seconds >= 60) {
      int mins = seconds / 60;
      int secs = seconds % 60;
      if (secs == 0) {
        return String.format("%dm", mins);
      }
      return String.format("%dm %ds", mins, secs);
    } else {
      return String.format("%ds", seconds);
    }
  }

  public static void setCooldown(ServerPlayer player, Spell spell) {
    if (spell.cooldownTicks() <= 0) {
      return;
    }

    long currentTick = player.level().getGameTime();
    long cooldownEnd = currentTick + spell.cooldownTicks();

    Map<String, Long> cooldowns = new HashMap<>(player.getData(ModDataAttachments.SPELL_COOLDOWNS));
    cooldowns.put(spell.id(), cooldownEnd);
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, cooldowns);

    syncCooldownToClient(player, spell);
  }

  public static void clearCooldown(ServerPlayer player, Spell spell) {
    Map<String, Long> cooldowns = new HashMap<>(player.getData(ModDataAttachments.SPELL_COOLDOWNS));
    cooldowns.remove(spell.id());
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, cooldowns);

    PacketDistributor.sendToPlayer(player, new SpellCooldownSyncPacket(spell.id(), 0, spell.cooldownTicks()));
  }

  public static void clearAllCooldowns(ServerPlayer player) {
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, new HashMap<>());
  }

  public static void syncCooldownToClient(ServerPlayer player, Spell spell) {
    long remaining = getRemainingCooldown(player, spell);
    PacketDistributor.sendToPlayer(player, new SpellCooldownSyncPacket(spell.id(), remaining, spell.cooldownTicks()));
  }

  public static void syncAllCooldownsToClient(ServerPlayer player) {
    for (Spell spell : ModSpells.getAllSpells()) {
      syncCooldownToClient(player, spell);
    }
  }
}

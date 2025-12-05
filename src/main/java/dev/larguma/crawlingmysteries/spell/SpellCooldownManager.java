package dev.larguma.crawlingmysteries.spell;

import java.util.HashMap;
import java.util.Map;

import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import net.minecraft.server.level.ServerPlayer;

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

  public static void setCooldown(ServerPlayer player, Spell spell) {
    if (spell.cooldownTicks() <= 0) {
      return;
    }

    long currentTick = player.level().getGameTime();
    long cooldownEnd = currentTick + spell.cooldownTicks();

    Map<String, Long> cooldowns = new HashMap<>(player.getData(ModDataAttachments.SPELL_COOLDOWNS));
    cooldowns.put(spell.id(), cooldownEnd);
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, cooldowns);
  }

  public static void clearCooldown(ServerPlayer player, Spell spell) {
    Map<String, Long> cooldowns = new HashMap<>(player.getData(ModDataAttachments.SPELL_COOLDOWNS));
    cooldowns.remove(spell.id());
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, cooldowns);
  }

  public static void clearAllCooldowns(ServerPlayer player) {
    player.setData(ModDataAttachments.SPELL_COOLDOWNS, new HashMap<>());
  }
}

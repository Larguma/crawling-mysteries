package dev.larguma.crawlingmysteries.networking.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.networking.packet.BetterToastPacket;
import dev.larguma.crawlingmysteries.networking.packet.DistilleryActionPacket;
import dev.larguma.crawlingmysteries.networking.packet.RequestStatsPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.networking.packet.SyncUnlockedEntriesPacket;
import dev.larguma.crawlingmysteries.networking.packet.UnlockCodexEntryPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import dev.larguma.crawlingmysteries.spell.SpellHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

  /**
   * Handles the request from the client to sync player stats and unlocked codex
   * entries.
   */
  public static void handleRequestStats(final RequestStatsPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    player.getStats().sendStats(player);
    Set<String> unlockedEntries = player.getData(ModDataAttachments.UNLOCKED_CODEX_ENTRIES);
    PacketDistributor.sendToPlayer(player, new SyncUnlockedEntriesPacket(unlockedEntries));
  }

  /**
   * Handles the request to unlock a codex entry permanently for the player.
   */
  public static void handleUnlockCodexEntry(final UnlockCodexEntryPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    String entryId = data.entryId();

    Set<String> unlockedEntries = new HashSet<>(player.getData(ModDataAttachments.UNLOCKED_CODEX_ENTRIES));
    if (!unlockedEntries.contains(entryId)) {
      unlockedEntries.add(entryId);
      player.setData(ModDataAttachments.UNLOCKED_CODEX_ENTRIES, unlockedEntries);
      PacketDistributor.sendToPlayer(player, new SyncUnlockedEntriesPacket(unlockedEntries));
    }
  }

  /**
   * Handles the spell selection made by the player.
   */
  public static void handleSpellSelect(final SpellSelectPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    String id = data.id();
    String lastSpell = player.getData(ModDataAttachments.LAST_USED_SPELL);

    if (id.isEmpty()) {
      id = lastSpell;
    }

    if (!lastSpell.equals(id)) {
      player.setData(ModDataAttachments.LAST_USED_SPELL, id);
    }

    Optional<Spell> spell = ModSpells.getSpell(id);
    if (spell.isEmpty()) {
      PacketDistributor.sendToPlayer(player, new BetterToastPacket(
          Component.translatable("spell.crawlingmysteries.empty"), BetterToastPacket.TYPE_ERROR));
      return;
    }

    Spell spellData = spell.get();

    if (SpellCooldownManager.isOnCooldown(player, spellData)) {
      String formattedCooldown = SpellCooldownManager.getRemainingCooldownFormatted(player, spellData);
      PacketDistributor.sendToPlayer(player,
          new BetterToastPacket(Component.translatable("message.crawlingmysteries.spell.on_cooldown",
              spellData.name().getString(), formattedCooldown), BetterToastPacket.TYPE_WARNING));
      return;
    }

    boolean success = switch (id) {
      case "spectral_gaze" -> SpellHandlerHelper.handleSpectralGaze(player);
      case "feed_totem" -> SpellHandlerHelper.handleFeedTotem(player);
      default -> {
        PacketDistributor.sendToPlayer(player, new BetterToastPacket(
            Component.translatable("spell.crawlingmysteries.unimplemented", id), BetterToastPacket.TYPE_ERROR));
        yield false;
      }
    };

    if (success) {
      SpellCooldownManager.setCooldown(player, spellData);
    }
  }

  /**
   * Handles distillery actions.
   */
  public static void handleDistilleryAction(final DistilleryActionPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();

    if (player.level().getBlockEntity(data.pos()) instanceof CookingAltarTier2BlockEntity blockEntity) {
      if (player.distanceToSqr(data.pos().getX() + 0.5, data.pos().getY() + 0.5, data.pos().getZ() + 0.5) > 64) {
        return;
      }

      if (data.action() == DistilleryActionPacket.Action.START) {
        blockEntity.startCooking();
      } else if (data.action() == DistilleryActionPacket.Action.STABILIZE) {
        blockEntity.stabilizeNeedle();
      }
    }
  }
}

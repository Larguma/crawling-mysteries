package dev.larguma.crawlingmysteries.networking.handler;

import java.util.Optional;

import dev.larguma.crawlingmysteries.networking.packet.BetterToastPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import dev.larguma.crawlingmysteries.spell.SpellHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

  public static void handleSpellSelect(final SpellSelectPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    String id = data.id();

    Optional<Spell> spell = ModSpells.getSpell(id);
    if (spell.isEmpty()) {
      PacketDistributor.sendToPlayer(player, new BetterToastPacket(
          Component.translatable("spell.crawlingmysteries.unknown", id), BetterToastPacket.TYPE_ERROR));
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
}

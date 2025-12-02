package dev.larguma.crawlingmysteries.networking.handler;

import java.util.Optional;

import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import dev.larguma.crawlingmysteries.spell.SpellHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

  public static void handleSpellSelect(final SpellSelectPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    String id = data.id();

    Optional<Spell> spell = ModSpells.getSpell(id);
    if (spell.isEmpty()) {
      player.displayClientMessage(Component.translatable("spell.crawlingmysteries.unknown", id), true);
      return;
    }

    switch (id) {
      case "spectral_gaze" -> SpellHandlerHelper.handleSpectralGaze(player);
      default -> player.displayClientMessage(Component.translatable("spell.crawlingmysteries.unimplemented" , id), true);
    }
  }
}

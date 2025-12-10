package dev.larguma.crawlingmysteries.networking.handler;

import dev.larguma.crawlingmysteries.client.gui.BetterToastOverlay;
import dev.larguma.crawlingmysteries.client.spell.ClientSpellCooldownManager;
import dev.larguma.crawlingmysteries.networking.packet.BetterToastPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellCooldownSyncPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

  public static void handleSpellCooldownSync(final SpellCooldownSyncPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      if (packet.remainingTicks() <= 0) {
        ClientSpellCooldownManager.clearCooldown(packet.spellId());
      } else {
        ClientSpellCooldownManager.setCooldown(packet.spellId(), packet.remainingTicks(), packet.totalTicks());
      }
    });
  }

  public static void handleBetterToast(final BetterToastPacket packet, final IPayloadContext context) {
    context.enqueueWork(() -> {
      BetterToastOverlay.ToastType type = BetterToastOverlay.ToastType.values()[packet.toastType()];
      BetterToastOverlay.showMessage(packet.message(), type, packet.getIconItem(), packet.getIconTexture());
    });
  }
}

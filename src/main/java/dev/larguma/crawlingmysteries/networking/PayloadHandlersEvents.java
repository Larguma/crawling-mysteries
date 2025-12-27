package dev.larguma.crawlingmysteries.networking;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.networking.handler.ClientPayloadHandler;
import dev.larguma.crawlingmysteries.networking.handler.ServerPayloadHandler;
import dev.larguma.crawlingmysteries.networking.packet.BetterToastPacket;
import dev.larguma.crawlingmysteries.networking.packet.RequestStatsPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellCooldownSyncPacket;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.networking.packet.SyncUnlockedEntriesPacket;
import dev.larguma.crawlingmysteries.networking.packet.TavernMusicPacket;
import dev.larguma.crawlingmysteries.networking.packet.UnlockCodexEntryPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class PayloadHandlersEvents {
  @SubscribeEvent
  public static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar("1");
    registrar.playToServer(
        SpellSelectPacket.TYPE,
        SpellSelectPacket.STREAM_CODEC,
        ServerPayloadHandler::handleSpellSelect);
    registrar.playToServer(
        RequestStatsPacket.TYPE,
        RequestStatsPacket.STREAM_CODEC,
        ServerPayloadHandler::handleRequestStats);
    registrar.playToServer(
        UnlockCodexEntryPacket.TYPE,
        UnlockCodexEntryPacket.STREAM_CODEC,
        ServerPayloadHandler::handleUnlockCodexEntry);
    registrar.playToClient(
        SpellCooldownSyncPacket.TYPE,
        SpellCooldownSyncPacket.STREAM_CODEC,
        ClientPayloadHandler::handleSpellCooldownSync);
    registrar.playToClient(
        BetterToastPacket.TYPE,
        BetterToastPacket.STREAM_CODEC,
        ClientPayloadHandler::handleBetterToast);
    registrar.playToClient(
        SyncUnlockedEntriesPacket.TYPE,
        SyncUnlockedEntriesPacket.STREAM_CODEC,
        ClientPayloadHandler::handleSyncUnlockedEntries);
    registrar.playToClient(
        TavernMusicPacket.TYPE,
        TavernMusicPacket.STREAM_CODEC,
        ClientPayloadHandler::handleTavernMusic);
  }
}

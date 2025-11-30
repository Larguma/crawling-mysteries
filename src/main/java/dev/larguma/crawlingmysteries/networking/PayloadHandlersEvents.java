package dev.larguma.crawlingmysteries.networking;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.networking.handler.ServerPayloadHandler;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
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
  }
}

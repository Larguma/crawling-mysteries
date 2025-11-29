package dev.larguma.crawlingmysteries.networking.handler;

import dev.larguma.crawlingmysteries.networking.packet.KeycodePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

  public static void handleDataOnMain(final KeycodePacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    player.displayClientMessage(Component.literal("spell menu opened"), true);
  }
}

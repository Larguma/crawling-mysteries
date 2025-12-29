package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TavernMusicPacket(boolean insideTavern) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<TavernMusicPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "tavern_music"));

  public static final StreamCodec<ByteBuf, TavernMusicPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, TavernMusicPacket::insideTavern,
      TavernMusicPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

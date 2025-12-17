package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestStatsPacket() implements CustomPacketPayload {

  public static final Type<RequestStatsPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "request_stats"));

  public static final StreamCodec<ByteBuf, RequestStatsPacket> STREAM_CODEC = StreamCodec
      .unit(new RequestStatsPacket());

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

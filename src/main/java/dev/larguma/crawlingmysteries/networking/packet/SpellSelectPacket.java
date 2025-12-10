package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpellSelectPacket(String id) implements CustomPacketPayload {

  public static final Type<SpellSelectPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "spell_select"));

  public static final StreamCodec<ByteBuf, SpellSelectPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      SpellSelectPacket::id,
      SpellSelectPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

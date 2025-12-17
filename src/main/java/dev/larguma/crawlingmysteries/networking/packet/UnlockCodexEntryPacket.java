package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UnlockCodexEntryPacket(String entryId) implements CustomPacketPayload {

  public static final Type<UnlockCodexEntryPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "unlock_codex_entry"));

  public static final StreamCodec<ByteBuf, UnlockCodexEntryPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      UnlockCodexEntryPacket::entryId,
      UnlockCodexEntryPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

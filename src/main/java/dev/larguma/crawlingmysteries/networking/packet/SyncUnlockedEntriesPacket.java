package dev.larguma.crawlingmysteries.networking.packet;

import java.util.HashSet;
import java.util.Set;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncUnlockedEntriesPacket(Set<String> unlockedEntries) implements CustomPacketPayload {

  public static final Type<SyncUnlockedEntriesPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "sync_unlocked_entries"));

  public static final StreamCodec<ByteBuf, SyncUnlockedEntriesPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).map(HashSet::new, list -> list.stream().toList()),
      SyncUnlockedEntriesPacket::unlockedEntries,
      SyncUnlockedEntriesPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

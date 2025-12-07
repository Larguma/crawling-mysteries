package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpellCooldownSyncPacket(String spellId, long remainingTicks, int totalTicks)
    implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SpellCooldownSyncPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "spell_cooldown_sync"));

  public static final StreamCodec<ByteBuf, SpellCooldownSyncPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      SpellCooldownSyncPacket::spellId,
      ByteBufCodecs.VAR_LONG,
      SpellCooldownSyncPacket::remainingTicks,
      ByteBufCodecs.VAR_INT,
      SpellCooldownSyncPacket::totalTicks,
      SpellCooldownSyncPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DistilleryActionPacket(BlockPos pos, Action action) implements CustomPacketPayload {

  public enum Action {
    START,
    STABILIZE
  }

  public static final Type<DistilleryActionPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "distillery_action"));

  public static final StreamCodec<ByteBuf, DistilleryActionPacket> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, DistilleryActionPacket::pos,
      ByteBufCodecs.VAR_INT.map(i -> Action.values()[i], Action::ordinal), DistilleryActionPacket::action,
      DistilleryActionPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

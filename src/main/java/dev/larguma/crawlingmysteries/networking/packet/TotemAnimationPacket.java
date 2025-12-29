package dev.larguma.crawlingmysteries.networking.packet;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TotemAnimationPacket(ItemStack itemStack) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<TotemAnimationPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "totem_animation"));

  public static final StreamCodec<RegistryFriendlyByteBuf, TotemAnimationPacket> STREAM_CODEC = StreamCodec.composite(
      ItemStack.STREAM_CODEC, TotemAnimationPacket::itemStack,
      TotemAnimationPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

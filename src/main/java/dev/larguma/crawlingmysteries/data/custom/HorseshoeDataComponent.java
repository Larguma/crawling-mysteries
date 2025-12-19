package dev.larguma.crawlingmysteries.data.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HorseshoeDataComponent(int tier) {
  public static final Codec<HorseshoeDataComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.INT.fieldOf("tier").forGetter(HorseshoeDataComponent::tier)).apply(instance, HorseshoeDataComponent::new));

  public static final StreamCodec<ByteBuf, HorseshoeDataComponent> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      HorseshoeDataComponent::tier,
      HorseshoeDataComponent::new);

  public static final int MAX_TIER = 4;

  /**
   * Get fall damage reduction percentage for this tier
   * Tier 1: 50%, Tier 2: 75%, Tier 3: 90%, Tier 4: 100%
   */
  public float getFallReduction() {
    return switch (tier) {
      case 1 -> 0.50f;
      case 2 -> 0.75f;
      case 3 -> 0.90f;
      case 4 -> 1.00f;
      default -> 0.0f;
    };
  }

  public boolean isMaxTier() {
    return tier >= MAX_TIER;
  }

}

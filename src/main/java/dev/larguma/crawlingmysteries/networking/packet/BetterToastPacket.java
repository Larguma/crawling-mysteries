package dev.larguma.crawlingmysteries.networking.packet;

import java.util.Optional;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record BetterToastPacket(Component message, int toastType, String iconData) implements CustomPacketPayload {

  private static final String ITEM_PREFIX = "item:";
  private static final String TEXTURE_PREFIX = "texture:";

  public static final CustomPacketPayload.Type<BetterToastPacket> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "better_toast"));

  public static final StreamCodec<RegistryFriendlyByteBuf, BetterToastPacket> STREAM_CODEC = StreamCodec.composite(
      ComponentSerialization.STREAM_CODEC, BetterToastPacket::message,
      ByteBufCodecs.VAR_INT, BetterToastPacket::toastType,
      ByteBufCodecs.STRING_UTF8, BetterToastPacket::iconData,
      BetterToastPacket::new);

  public BetterToastPacket(Component message, int toastType) {
    this(message, toastType, "");
  }

  public BetterToastPacket(Component message, int toastType, Item item) {
    this(message, toastType, ITEM_PREFIX + BuiltInRegistries.ITEM.getKey(item).toString());
  }

  public BetterToastPacket(Component message, int toastType, ResourceLocation texture) {
    this(message, toastType, TEXTURE_PREFIX + texture.toString());
  }

  // BetterToastOverlay.ToastType
  public static final int TYPE_INFO = 0;
  public static final int TYPE_SUCCESS = 1;
  public static final int TYPE_WARNING = 2;
  public static final int TYPE_ERROR = 3;

  public boolean isItemIcon() {
    return iconData != null && iconData.startsWith(ITEM_PREFIX);
  }

  public boolean isTextureIcon() {
    return iconData != null && iconData.startsWith(TEXTURE_PREFIX);
  }

  public boolean hasIcon() {
    return iconData != null && !iconData.isEmpty();
  }

  public Optional<ItemStack> getIconItem() {
    if (!isItemIcon()) {
      return Optional.empty();
    }
    String itemIdStr = iconData.substring(ITEM_PREFIX.length());
    ResourceLocation id = ResourceLocation.parse(itemIdStr);
    Item item = BuiltInRegistries.ITEM.get(id);
    if (item == Items.AIR) {
      return Optional.empty();
    }
    return Optional.of(new ItemStack(item));
  }

  public Optional<ResourceLocation> getIconTexture() {
    if (!isTextureIcon()) {
      return Optional.empty();
    }
    String textureStr = iconData.substring(TEXTURE_PREFIX.length());
    return Optional.of(ResourceLocation.parse(textureStr));
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

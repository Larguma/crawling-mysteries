package larguma.crawling_mysteries.networking.packet.c2s;

import java.util.Objects;

import dev.emi.trinkets.api.TrinketsApi;
import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.item.custom.EternalGuardianMaskItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class EternalGuardianMaskEffectC2SPacket {

  private static final String MESSAGE_ETERNAL_GUARDIAN_MASK_EFFECT_ENABLED = "message.crawling-mysteries.eternal_guardian_mask_effect_enabled";
  private static final String MESSAGE_ETERNAL_GUARDIAN_MASK_EFFECT_DISABLED = "message.crawling-mysteries.eternal_guardian_mask_effect_disabled";

  public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
      PacketByteBuf buf, PacketSender responseSender) {

    TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((ref, stack) -> {

      if (!stack.isEmpty() && Objects.equals(stack.getItem(), ModItems.ETERNAL_GUARDIAN_MASK)) {
        EternalGuardianMaskItem mask = (EternalGuardianMaskItem) stack.getItem();
        mask.toggle(stack);
        player.playSound(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (mask.isEnabled(stack)) {
          player.sendMessage(Text.translatable(MESSAGE_ETERNAL_GUARDIAN_MASK_EFFECT_ENABLED), true);
        } else {
          player.sendMessage(Text.translatable(MESSAGE_ETERNAL_GUARDIAN_MASK_EFFECT_DISABLED), true);
        }
      }
    }));
  }
}

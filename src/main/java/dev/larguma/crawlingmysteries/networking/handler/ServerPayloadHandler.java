package dev.larguma.crawlingmysteries.networking.handler;

import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import dev.larguma.crawlingmysteries.networking.packet.SpellSelectPacket;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;

public class ServerPayloadHandler {

  public static void handleSpellSelect(final SpellSelectPacket data, final IPayloadContext context) {
    ServerPlayer player = (ServerPlayer) context.player();
    String spellId = data.spellId();

    var spellOpt = ModSpells.getSpell(spellId);
    if (spellOpt.isEmpty()) {
      player.displayClientMessage(Component.literal("Unknown spell: " + spellId), true);
      return;
    }

    switch (spellId) {
      case "spectral_gaze" -> handleSpectralGaze(player);
      default -> player.displayClientMessage(Component.literal("Spell not implemented: " + spellId), true);
    }
  }

  private static void handleSpectralGaze(ServerPlayer player) {
    var curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.no_mask_equipped"), true);
      return;
    }

    var inventory = curiosHandler.get();
    var maskSlot = inventory.findFirstCurio(ModItems.ETERNAL_GUARDIAN_MASK.get());

    if (maskSlot.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.no_mask_equipped"), true);
      return;
    }

    ItemStack maskStack = maskSlot.get().stack();
    if (maskStack.getItem() instanceof EternalGuardianMaskItem maskItem) {
      maskItem.toggle(maskStack);
      boolean isEnabled = maskItem.isEnabled(maskStack);

      if (isEnabled) {
        player.displayClientMessage(Component.translatable("message.crawlingmysteries.spectral_gaze_enabled"), true);
      } else {
        player.displayClientMessage(Component.translatable("message.crawlingmysteries.spectral_gaze_disabled"), true);
      }
    }
  }
}

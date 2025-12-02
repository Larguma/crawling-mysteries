package dev.larguma.crawlingmysteries.spell;

import java.util.Optional;

import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SpellHandlerHelper {
  public static void handleSpectralGaze(ServerPlayer player) {
    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.no_mask_equipped"), true);
      return;
    }

    ICuriosItemHandler inventory = curiosHandler.get();
    Optional<SlotResult> slot = inventory.findFirstCurio(ModItems.ETERNAL_GUARDIAN_MASK.get());

    if (slot.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.no_mask_equipped"), true);
      return;
    }

    ItemStack stack = slot.get().stack();
    if (stack.getItem() instanceof EternalGuardianMaskItem) {
      ItemDataHelper.enabledToggle(stack);

      if (ItemDataHelper.isEnabled(stack)) {
        player.displayClientMessage(Component.translatable("message.crawlingmysteries.spectral_gaze_enabled"), true);
      } else {
        player.displayClientMessage(Component.translatable("message.crawlingmysteries.spectral_gaze_disabled"), true);
      }
    }
  }
}

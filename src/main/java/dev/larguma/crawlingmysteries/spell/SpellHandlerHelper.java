package dev.larguma.crawlingmysteries.spell;

import java.util.Optional;

import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import dev.larguma.crawlingmysteries.item.helper.ItemDataHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SpellHandlerHelper {

  public static ItemStack getCurioEquipped(ServerPlayer player, Item item) {
    Optional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosInventory(player);
    if (curiosHandler.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.spell.no_equipped"), true);
      return ItemStack.EMPTY;
    }

    ICuriosItemHandler inventory = curiosHandler.get();
    Optional<SlotResult> slot = inventory.findFirstCurio(item);

    if (slot.isEmpty()) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.spell.no_equipped"), true);
      return ItemStack.EMPTY;
    }

    return slot.get().stack();
  }

  public static boolean handleSpectralGaze(ServerPlayer player) {
    ItemStack stack = getCurioEquipped(player, ModItems.ETERNAL_GUARDIAN_MASK.get());
    if (stack.isEmpty()) {
      return false;
    }

    ItemDataHelper.enabledToggle(stack);

    if (ItemDataHelper.isEnabled(stack)) {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.spell.spectral_gaze_enabled"),
          true);
    } else {
      player.displayClientMessage(Component.translatable("message.crawlingmysteries.spell.spectral_gaze_disabled"),
          true);
    }

    return true;
  }

  public static boolean handleFeedTotem(ServerPlayer player) {
    ItemStack stack = getCurioEquipped(player, ModItems.CRYPTIC_EYE.get());
    if (stack.isEmpty()) {
      return false;
    }

    if (stack.getItem() instanceof CrypticEyeItem item) {
      return item.consumeTotem(player, stack);
    }

    return false;
  }
}

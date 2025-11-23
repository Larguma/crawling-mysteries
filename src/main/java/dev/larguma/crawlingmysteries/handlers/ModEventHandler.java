package dev.larguma.crawlingmysteries.handlers;

import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ModEventHandler {

  @SubscribeEvent
  public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!player.getData(ModDataAttachments.STARTER_RECEIVED)) {
      player.getInventory().add(new ItemStack(ModItems.CRYPTIC_EYE.get()));
      player.setData(ModDataAttachments.STARTER_RECEIVED, true);
    }
  }

}

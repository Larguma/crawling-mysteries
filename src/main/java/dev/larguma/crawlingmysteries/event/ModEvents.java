package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModEvents {
  @SubscribeEvent
  public static void playerLogin(PlayerLoggedInEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!player.getData(ModDataAttachments.STARTER_RECEIVED)) {
      player.getInventory().add(new ItemStack(ModItems.CRYPTIC_EYE.get()));
      player.setData(ModDataAttachments.STARTER_RECEIVED, true);
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerRespawn(PlayerRespawnEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    if (!event.isEndConquered()) {
      SpellCooldownManager.clearAllCooldowns((ServerPlayer) player);
    }
    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

  @SubscribeEvent
  public static void playerChangedDimension(PlayerChangedDimensionEvent event) {
    Player player = event.getEntity();
    if (player.level().isClientSide()) {
      return;
    }

    SpellCooldownManager.syncAllCooldownsToClient((ServerPlayer) player);
  }

}

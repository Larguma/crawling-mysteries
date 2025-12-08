package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.command.spell.ClearAllCooldowns;
import dev.larguma.crawlingmysteries.command.spell.ClearCooldowns;
import dev.larguma.crawlingmysteries.command.spell.GetCooldowns;
import dev.larguma.crawlingmysteries.command.spell.GetTrinketData;
import dev.larguma.crawlingmysteries.command.spell.SetTrinketData;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModEvents {

  // #region Player
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

  // #endregion Player

  // #region Command
  @SubscribeEvent
  public static void onCommandsRegister(RegisterCommandsEvent event) {
    // Spell
    new ClearAllCooldowns(event.getDispatcher());
    new ClearCooldowns(event.getDispatcher());
    new GetCooldowns(event.getDispatcher());
    new SetTrinketData(event.getDispatcher());
    new GetTrinketData(event.getDispatcher());

    ConfigCommand.register(event.getDispatcher());
  }
  // #endregion Command

}

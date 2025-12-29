package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.command.spell.ClearAllCooldowns;
import dev.larguma.crawlingmysteries.command.spell.ClearCooldowns;
import dev.larguma.crawlingmysteries.command.spell.GetCooldowns;
import dev.larguma.crawlingmysteries.command.trinket.GetTrinketData;
import dev.larguma.crawlingmysteries.command.trinket.SetTrinketData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModCommandEvents {

  @SubscribeEvent
  public static void onCommandsRegister(RegisterCommandsEvent event) {
    new ClearAllCooldowns(event.getDispatcher());
    new ClearCooldowns(event.getDispatcher());
    new GetCooldowns(event.getDispatcher());
    new SetTrinketData(event.getDispatcher());
    new GetTrinketData(event.getDispatcher());

    ConfigCommand.register(event.getDispatcher());
  }
}

package dev.larguma.crawlingmysteries.client.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.codex.CodexUnlockManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class CodexClientEvents {

  @SubscribeEvent
  public static void onPlayerLogout(LoggingOut event) {
    CodexUnlockManager.clearCache();
  }
}

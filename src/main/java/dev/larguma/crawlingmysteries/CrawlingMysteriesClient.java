package dev.larguma.crawlingmysteries;

import dev.larguma.crawlingmysteries.client.CurioRenderers;
import dev.larguma.crawlingmysteries.client.ModBlockRenderers;
import dev.larguma.crawlingmysteries.client.ModEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CrawlingMysteries.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class CrawlingMysteriesClient {
  public CrawlingMysteriesClient(ModContainer container) {
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }

  @SubscribeEvent
  public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    ModBlockRenderers.register(event);
    ModEntityRenderers.register(event);
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    CurioRenderers.register();
  }
}

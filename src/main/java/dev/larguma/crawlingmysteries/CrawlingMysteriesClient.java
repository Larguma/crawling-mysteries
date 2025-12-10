package dev.larguma.crawlingmysteries;

import dev.larguma.crawlingmysteries.client.CurioRenderers;
import dev.larguma.crawlingmysteries.client.ModBlockRenderers;
import dev.larguma.crawlingmysteries.client.ModEntityRenderers;
import dev.larguma.crawlingmysteries.client.gui.BetterToastOverlay;
import dev.larguma.crawlingmysteries.client.gui.PassiveSpellHudOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

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
  public static void registerGuiLayers(RegisterGuiLayersEvent event) {
    event.registerAbove(VanillaGuiLayers.HOTBAR, PassiveSpellHudOverlay.ID, new PassiveSpellHudOverlay());
    event.registerAbove(VanillaGuiLayers.TITLE, BetterToastOverlay.ID, new BetterToastOverlay());
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    CurioRenderers.register();
  }
}

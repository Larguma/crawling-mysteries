package dev.larguma.crawlingmysteries.item.client.curio;

import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.item.client.curio.renderer.ObserverRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CurioRenderers {

  public static void register() {
    CuriosRendererRegistry.register(ModItems.CRYPTIC_EYE.get(), () -> new ObserverRenderer());
  }
}

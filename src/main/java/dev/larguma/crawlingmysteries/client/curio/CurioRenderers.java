package dev.larguma.crawlingmysteries.client.curio;

import dev.larguma.crawlingmysteries.client.curio.renderer.ObserverRenderer;
import dev.larguma.crawlingmysteries.item.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CurioRenderers {

  public static void register() {
    CuriosRendererRegistry.register(ModItems.CRYPTIC_EYE.get(), () -> new ObserverRenderer());
  }
}

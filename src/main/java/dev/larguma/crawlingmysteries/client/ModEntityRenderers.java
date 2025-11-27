package dev.larguma.crawlingmysteries.client;

import dev.larguma.crawlingmysteries.client.entity.EternalGuardianEntityRenderer;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class ModEntityRenderers {

  public static void register(RegisterRenderers event) {
    event.registerEntityRenderer(ModEntities.ETERNAL_GUARDIAN.get(), EternalGuardianEntityRenderer::new);
  }

}

package dev.larguma.crawlingmysteries.block.entity.client;

import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.block.entity.client.renderer.TombstoneBlockEntityRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class ModBlockRenderers {

  public static void register(RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.TOMBSTONE_BE.get(), TombstoneBlockEntityRenderer::new);
  }

}

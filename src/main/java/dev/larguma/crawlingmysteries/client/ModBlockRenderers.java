package dev.larguma.crawlingmysteries.client;

import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.client.block.BeerKegBlockEntityRenderer;
import dev.larguma.crawlingmysteries.client.block.BeerMugBlockEntityRenderer;
import dev.larguma.crawlingmysteries.client.block.TombstoneBlockEntityRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

public class ModBlockRenderers {

  public static void register(RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.TOMBSTONE_BE.get(), TombstoneBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.BEER_MUG_BE.get(), BeerMugBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(ModBlockEntities.BEER_KEG_BE.get(), BeerKegBlockEntityRenderer::new);
  }

}

package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.BeerMugBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BeerMugBlockEntityRenderer extends GeoBlockRenderer<BeerMugBlockEntity> {
  public BeerMugBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new BeerMugBlockEntityModel());
  }
}
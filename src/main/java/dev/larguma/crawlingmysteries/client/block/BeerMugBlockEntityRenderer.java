package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.BeerMugBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import dev.larguma.crawlingmysteries.client.item.GooglyEyesLayer;

public class BeerMugBlockEntityRenderer extends GeoBlockRenderer<BeerMugBlockEntity> {
  public BeerMugBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new BeerMugBlockEntityModel());
    addRenderLayer(new GooglyEyesLayer<BeerMugBlockEntity>(this, 0.13d, 0.35d, 0d, 270f));
  }
}
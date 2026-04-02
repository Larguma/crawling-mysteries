package dev.larguma.crawlingmysteries.client.block;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import dev.larguma.crawlingmysteries.block.entity.custom.BeerMugBlockEntity;
import dev.larguma.crawlingmysteries.client.item.GooglyEyesLayer;

public class BeerMugBlockEntityRenderer extends GeoBlockRenderer<BeerMugBlockEntity> {
  public BeerMugBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new BeerMugBlockEntityModel());
    addRenderLayer(new GooglyEyesLayer<>(this, 0.13D, 0.35D, 0D, 270F));
  }
}
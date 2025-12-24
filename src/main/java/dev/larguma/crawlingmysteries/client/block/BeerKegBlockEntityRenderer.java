package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.BeerKegBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BeerKegBlockEntityRenderer extends GeoBlockRenderer<BeerKegBlockEntity> {
  public BeerKegBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new BeerKegBlockEntityModel());
  }
}
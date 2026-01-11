package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.CookingAltarTier1BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CookingAltarTier1BlockEntityRenderer extends GeoBlockRenderer<CookingAltarTier1BlockEntity> {

  public CookingAltarTier1BlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new CookingAltarTier1BlockEntityModel());

    addRenderLayer(new AutoGlowingGeoLayer<>(this));
  }
}

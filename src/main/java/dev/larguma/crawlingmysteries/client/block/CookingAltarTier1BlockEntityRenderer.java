package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier1BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CookingAltarTier1BlockEntityRenderer extends GeoBlockRenderer<CookingAltarTier1BlockEntity> {

  public CookingAltarTier1BlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new CookingAltarTier1BlockEntityModel());

    addRenderLayer(new AutoGlowingGeoLayer<>(this));
  }

  @Override
  public AABB getRenderBoundingBox(CookingAltarTier1BlockEntity blockEntity) {
    BlockPos pos = blockEntity.getBlockPos();
    return AABB.encapsulatingFullBlocks(pos, pos.offset(1, 1, 1));
  }
}

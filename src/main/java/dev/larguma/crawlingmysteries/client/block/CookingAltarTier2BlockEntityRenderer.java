package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CookingAltarTier2BlockEntityRenderer extends GeoBlockRenderer<CookingAltarTier2BlockEntity> {

  public CookingAltarTier2BlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    super(new CookingAltarTier2BlockEntityModel());
  }

  @Override
  public AABB getRenderBoundingBox(CookingAltarTier2BlockEntity blockEntity) {
    BlockPos pos = blockEntity.getBlockPos();
    return AABB.encapsulatingFullBlocks(pos, pos.offset(1, 1, 1));
  }
}

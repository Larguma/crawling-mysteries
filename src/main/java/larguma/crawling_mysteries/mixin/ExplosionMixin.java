package larguma.crawling_mysteries.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;

@Mixin(Explosion.class)
public class ExplosionMixin {
  @Shadow
  @Final
  private World world;
  private BlockPos lastPos;

  @ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
  private BlockPos modifyAffectedBlocks(BlockPos pos) {
    lastPos = pos;
    return pos;
  }

  @ModifyVariable(method = "affectWorld", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
  private BlockState modifyAffectedBlocks(BlockState state) {
    if (state.getBlock() == ModBlocks.TOMBSTONE) {
      BlockEntity blockEntity = world.getBlockEntity(lastPos);

      if (blockEntity instanceof TombstoneBlockEntity tombstoneBlockEntity) {
        if (tombstoneBlockEntity.getTombOwner() != null)
          return Blocks.AIR.getDefaultState();
      }
    }

    return state;
  }
}
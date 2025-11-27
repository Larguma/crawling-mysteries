package dev.larguma.crawlingmysteries.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Explosion.class)
public class ExplosionMixin {
  @Shadow
  @Final
  private Level level;
  private BlockPos lastPos;

  @ModifyVariable(method = "finalizeExplosion", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
  private BlockPos modifyAffectedBlocks(BlockPos pos) {
    lastPos = pos;
    return pos;
  }

  @ModifyVariable(method = "finalizeExplosion", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
  private BlockState modifyAffectedBlocks(BlockState state) {
    if (state.is(ModBlocks.TOMBSTONE)) {
      BlockEntity blockEntity = level.getBlockEntity(lastPos);

      if (blockEntity instanceof TombstoneBlockEntity tombstoneBlockEntity) {
        if (tombstoneBlockEntity.getTombstoneOwner() != null)
          return Blocks.AIR.defaultBlockState();
      }
    }

    return state;
  }
}

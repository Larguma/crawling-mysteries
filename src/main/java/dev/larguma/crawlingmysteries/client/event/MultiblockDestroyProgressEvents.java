package dev.larguma.crawlingmysteries.client.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.custom.CookingAltarTier1Block;
import dev.larguma.crawlingmysteries.block.custom.CookingAltarTier2Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class MultiblockDestroyProgressEvents {

  private static final int SECONDARY_TRACKER_ID_OFFSET = -999_000;

  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Post event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.level == null || mc.player == null || mc.gameMode == null)
      return;

    MultiPlayerGameMode gameMode = mc.gameMode;
    int secondaryId = mc.player.getId() + SECONDARY_TRACKER_ID_OFFSET;

    if (!gameMode.isDestroying()) {
      mc.level.destroyBlockProgress(secondaryId, BlockPos.ZERO, -1);
      return;
    }

    if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK)
      return;

    BlockPos miningPos = ((BlockHitResult) mc.hitResult).getBlockPos();
    BlockState state = mc.level.getBlockState(miningPos);
    BlockPos masterPos = getMasterPos(miningPos, state);

    if (masterPos == null) {
      mc.level.destroyBlockProgress(secondaryId, BlockPos.ZERO, -1);
      return;
    }

    mc.level.destroyBlockProgress(secondaryId, masterPos, gameMode.getDestroyStage());
  }

  /** Returns the master block position if {@code pos} is a non-master part of a multiblock, else null. */
  private static BlockPos getMasterPos(BlockPos pos, BlockState state) {
    if (state.is(ModBlocks.COOKING_ALTAR_TIER_1.get())
        && state.getValue(CookingAltarTier1Block.HALF) == DoubleBlockHalf.UPPER) {
      return pos.below();
    }
    if (state.is(ModBlocks.COOKING_ALTAR_TIER_2.get())
        && state.getValue(CookingAltarTier2Block.PART) != 0) {
      return ((CookingAltarTier2Block) ModBlocks.COOKING_ALTAR_TIER_2.get()).getMasterPos(pos, state);
    }
    return null;
  }
}

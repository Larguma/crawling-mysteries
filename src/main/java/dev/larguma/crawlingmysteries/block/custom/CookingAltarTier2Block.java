package dev.larguma.crawlingmysteries.block.custom;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CookingAltarTier2Block extends BaseEntityBlock {
  public static final MapCodec<CookingAltarTier2Block> CODEC = simpleCodec(CookingAltarTier2Block::new);
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final IntegerProperty PART = IntegerProperty.create("part", 0, 7);
  private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

  public CookingAltarTier2Block() {
    super(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .noOcclusion()
        .strength(4.0f, 6.0f)
        .pushReaction(PushReaction.BLOCK)
        .lightLevel(state -> state.getValue(PART) == 0 ? 12 : 0));
    this.registerDefaultState(this.stateDefinition.any()
        .setValue(PART, 0)
        .setValue(FACING, Direction.NORTH));
  }

  protected CookingAltarTier2Block(Properties properties) {
    this();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(PART, FACING);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return state.getValue(PART) == 0 ? new CookingAltarTier2BlockEntity(pos, state) : null;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return state.getValue(PART) == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
  }

  @Override
  protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    if (state.getValue(PART) != 0) {
      return List.of();
    }
    return super.getDrops(state, builder);
  }

  // #region Multiblock Placement Logic

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();
    Direction facing = context.getHorizontalDirection().getOpposite();

    if (!canPlaceMultiblock(level, pos, facing, context)) {
      return null;
    }

    return this.defaultBlockState()
        .setValue(PART, 0)
        .setValue(FACING, facing);
  }

  private boolean canPlaceMultiblock(Level level, BlockPos masterPos, Direction facing, BlockPlaceContext context) {
    for (int i = 0; i < 8; i++) {
      BlockPos partPos = getPartPosition(masterPos, i, facing);
      if (partPos.getY() >= level.getMaxBuildHeight()) {
        return false;
      }
      if (!level.getBlockState(partPos).canBeReplaced(context)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    if (state.getValue(PART) != 0) {
      return;
    }

    Direction facing = state.getValue(FACING);

    for (int i = 1; i < 8; i++) {
      BlockPos partPos = getPartPosition(pos, i, facing);
      level.setBlock(partPos, state.setValue(PART, i), Block.UPDATE_ALL);
    }

    super.setPlacedBy(level, pos, state, placer, stack);
  }

  private BlockPos getPartPosition(BlockPos masterPos, int partIndex, Direction facing) {
    int layer = partIndex / 4; // 0 = bottom, 1 = top
    int quadrant = partIndex % 4; // 0-3 position in layer

    int localX = (quadrant == 1 || quadrant == 3) ? 1 : 0;
    int localZ = (quadrant == 2 || quadrant == 3) ? 1 : 0;

    return masterPos.offset(
        rotateOffset(localX, localZ, facing),
        layer,
        rotateOffsetZ(localX, localZ, facing));
  }

  private int rotateOffset(int x, int z, Direction facing) {
    return switch (facing) {
      case NORTH -> x;
      case SOUTH -> -x;
      case WEST -> z;
      case EAST -> -z;
      default -> x;
    };
  }

  private int rotateOffsetZ(int x, int z, Direction facing) {
    return switch (facing) {
      case NORTH -> z;
      case SOUTH -> -z;
      case WEST -> -x;
      case EAST -> x;
      default -> z;
    };
  }

  private BlockPos getMasterPos(BlockPos partPos, BlockState state) {
    int part = state.getValue(PART);
    if (part == 0) {
      return partPos;
    }

    Direction facing = state.getValue(FACING);
    int layer = part / 4;
    int quadrant = part % 4;

    int localX = (quadrant == 1 || quadrant == 3) ? 1 : 0;
    int localZ = (quadrant == 2 || quadrant == 3) ? 1 : 0;

    return partPos.offset(
        -rotateOffset(localX, localZ, facing),
        -layer,
        -rotateOffsetZ(localX, localZ, facing));
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    if (state.getValue(PART) == 0) {
      return true;
    }

    BlockPos masterPos = getMasterPos(pos, state);
    BlockState masterState = level.getBlockState(masterPos);
    return masterState.is(this) && masterState.getValue(PART) == 0;
  }

  @Override
  protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
      LevelAccessor level, BlockPos pos, BlockPos facingPos) {

    int part = state.getValue(PART);

    if (part != 0) {
      BlockPos masterPos = getMasterPos(pos, state);
      BlockState masterState = level.getBlockState(masterPos);
      if (!masterState.is(this) || masterState.getValue(PART) != 0) {
        return Blocks.AIR.defaultBlockState();
      }
    }

    return super.updateShape(state, facing, facingState, level, pos, facingPos);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      destroyMultiblock(level, pos, state, player);
    }
    return super.playerWillDestroy(level, pos, state, player);
  }

  private void destroyMultiblock(Level level, BlockPos destroyedPos, BlockState state, Player player) {
    BlockPos masterPos = getMasterPos(destroyedPos, state);
    Direction facing = state.getValue(FACING);

    for (int i = 0; i < 8; i++) {
      BlockPos partPos = getPartPosition(masterPos, i, facing);
      if (!partPos.equals(destroyedPos)) {
        BlockState partState = level.getBlockState(partPos);
        if (partState.is(this)) {
          level.setBlock(partPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
          level.levelEvent(player, 2001, partPos, Block.getId(partState));
        }
      }
    }
  }

  // #endregion Multiblock Placement Logic

  // #region Interaction

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hitResult) {

    BlockPos masterPos = getMasterPos(pos, state);

    if (level.getBlockEntity(masterPos) instanceof CookingAltarTier2BlockEntity ) {
      if (!level.isClientSide && player instanceof ServerPlayer) {
          level.playSound(null, masterPos, SoundEvents.COPPER_BREAK, SoundSource.BLOCKS, 0.5f, 1.2f);
        }
      
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  // #endregion Interaction
}

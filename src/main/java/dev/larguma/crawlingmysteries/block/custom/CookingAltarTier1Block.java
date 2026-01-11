package dev.larguma.crawlingmysteries.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.block.entity.CookingAltarTier1BlockEntity;
import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

public class CookingAltarTier1Block extends BaseEntityBlock {
  public static final MapCodec<CookingAltarTier1Block> CODEC = simpleCodec(CookingAltarTier1Block::new);
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

  public CookingAltarTier1Block() {
    super(BlockBehaviour.Properties.of()
        .mapColor(MapColor.STONE)
        .noOcclusion()
        .strength(3.0f, 6.0f)
        .pushReaction(PushReaction.BLOCK)
        .lightLevel(state -> state.getValue(HALF) == DoubleBlockHalf.LOWER ? 10 : 0));
    this.registerDefaultState(this.stateDefinition.any()
        .setValue(HALF, DoubleBlockHalf.LOWER)
        .setValue(FACING, Direction.NORTH));
  }

  protected CookingAltarTier1Block(Properties properties) {
    this();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(HALF, FACING);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new CookingAltarTier1BlockEntity(pos, state) : null;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return state.getValue(HALF) == DoubleBlockHalf.LOWER ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
  }

  // #region Double Block Placement Logic
  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();

    if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
      Direction facing = context.getHorizontalDirection().getOpposite();
      return this.defaultBlockState()
          .setValue(HALF, DoubleBlockHalf.LOWER)
          .setValue(FACING, facing);
    }
    return null;
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
      ItemStack stack) {
    if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
      level.setBlock(pos.above(), state
          .setValue(HALF, DoubleBlockHalf.UPPER)
          .setValue(FACING, state.getValue(FACING)), 3);
    }
    super.setPlacedBy(level, pos, state, placer, stack);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
      return true;
    }
    BlockState belowState = level.getBlockState(pos.below());
    return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
  }

  @Override
  protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
      BlockPos pos, BlockPos facingPos) {
    DoubleBlockHalf half = state.getValue(HALF);

    if (facing.getAxis() == Direction.Axis.Y) {
      if (half == DoubleBlockHalf.LOWER && facing == Direction.UP) {
        if (!facingState.is(this) || facingState.getValue(HALF) != DoubleBlockHalf.UPPER) {
          return Blocks.AIR.defaultBlockState();
        }
      } else if (half == DoubleBlockHalf.UPPER && facing == Direction.DOWN) {
        if (!facingState.is(this) || facingState.getValue(HALF) != DoubleBlockHalf.LOWER) {
          return Blocks.AIR.defaultBlockState();
        }
      }
    }

    return super.updateShape(state, facing, facingState, level, pos, facingPos);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide) {
      DoubleBlockHalf half = state.getValue(HALF);
      BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
      BlockState otherState = level.getBlockState(otherPos);

      if (otherState.is(this) && otherState.getValue(HALF) != half) {
        level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
      }
    }
    return super.playerWillDestroy(level, pos, state, player);
  }
  // #endregion Double Block Placement Logic

  private BlockPos getBlockEntityPos(BlockState state, BlockPos pos) {
    return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
      Player player, InteractionHand hand, BlockHitResult hitResult) {

    BlockPos bePos = getBlockEntityPos(state, pos);
    if (level.getBlockEntity(bePos) instanceof CookingAltarTier1BlockEntity blockEntity) {
      CookingAltarTier1BlockEntity.CookingState cookingState = blockEntity.getCookingState();

      if (cookingState == CookingAltarTier1BlockEntity.CookingState.IDLE) {
        if (isValidIngredient(stack)) {
          if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
              stack.shrink(1);
            }
            blockEntity.startCooking();
            level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
          }
          return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
      }

      if (cookingState == CookingAltarTier1BlockEntity.CookingState.DONE) {
        if (stack.is(Items.BOWL)) {
          if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) {
              stack.shrink(1);
            }
            // TODO: Replace with SoulStew
            ItemStack stewStack = new ItemStack(Items.SUSPICIOUS_STEW);
            if (!player.getInventory().add(stewStack)) {
              player.drop(stewStack, false);
            }
            blockEntity.collectOutput();
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
          }
          return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
      }
    }

    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hitResult) {
    BlockPos bePos = getBlockEntityPos(state, pos);
    if (level.getBlockEntity(bePos) instanceof CookingAltarTier1BlockEntity blockEntity) {
      if (!level.isClientSide) {
        CookingAltarTier1BlockEntity.CookingState cookingState = blockEntity.getCookingState();
        String statusKey = switch (cookingState) {
          case IDLE -> "block.crawlingmysteries.cooking_altar_tier_1.idle";
          case BOILING -> "block.crawlingmysteries.cooking_altar_tier_1.boiling";
          case DONE -> "block.crawlingmysteries.cooking_altar_tier_1.done";
        };
        player.displayClientMessage(Component.translatable(statusKey), true);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return super.useWithoutItem(state, level, pos, player, hitResult);
  }

  private boolean isValidIngredient(ItemStack stack) {
    return stack.is(Tags.Items.FOODS_RAW_MEAT);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
      BlockEntityType<T> blockEntityType) {
    if (blockEntityType == ModBlockEntities.COOKING_ALTAR_TIER_1_BE.get()) {
      if (level.isClientSide) {
        return createTickerHelper(blockEntityType, ModBlockEntities.COOKING_ALTAR_TIER_1_BE.get(),
            CookingAltarTier1BlockEntity::clientTick);
      } else {
        return createTickerHelper(blockEntityType, ModBlockEntities.COOKING_ALTAR_TIER_1_BE.get(),
            CookingAltarTier1BlockEntity::serverTick);
      }
    }
    return null;
  }
}

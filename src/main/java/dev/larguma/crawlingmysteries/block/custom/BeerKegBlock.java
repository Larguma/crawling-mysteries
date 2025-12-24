package dev.larguma.crawlingmysteries.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.block.entity.BeerKegBlockEntity;
import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import dev.larguma.crawlingmysteries.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeerKegBlock extends BaseEntityBlock {
  public static final MapCodec<BeerKegBlock> CODEC = simpleCodec(BeerKegBlock::new);
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final VoxelShape SHAPE_N = Block.box(1, 0, 2, 15, 16, 16);
  public static final VoxelShape SHAPE_S = Block.box(1, 0, 0, 15, 16, 14);
  public static final VoxelShape SHAPE_E = Block.box(0, 0, 1, 14, 16, 15);
  public static final VoxelShape SHAPE_W = Block.box(2, 0, 1, 16, 16, 15);
  public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
  public static final BooleanProperty IS_POURING = BooleanProperty.create("is_pouring");

  public BeerKegBlock() {
    super(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .sound(SoundType.WOOD)
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY));
    this.registerDefaultState(
        this.stateDefinition.any().setValue(IS_POURING, false).setValue(FACING, Direction.NORTH));
  }

  protected BeerKegBlock(Properties properties) {
    this();
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
      InteractionHand hand, BlockHitResult hitResult) {
    if (stack.getItem() instanceof BeerMugItem item) {
      if (!state.getValue(IS_POURING)) {
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
      if (item.getBeerLevel(stack) >= 4) {
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
      item.setBeerLevel(stack, 4);
      level.playSound(null, pos, ModSounds.BEER_POUR.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
      return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hitResult) {
    BlockState newState = state.cycle(IS_POURING);
    level.setBlock(pos, newState, 3);

    if (level.getBlockEntity(pos) instanceof BeerKegBlockEntity be) {
      be.setIsPouring(newState.getValue(IS_POURING));
    }

    return InteractionResult.sidedSuccess(level.isClientSide());
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BeerKegBlockEntity(pos, state);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Direction facing = state.getValue(FACING);
    return switch (facing) {
      case NORTH -> SHAPE_N;
      case SOUTH -> SHAPE_S;
      case EAST -> SHAPE_E;
      case WEST -> SHAPE_W;
      default -> SHAPE;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(IS_POURING, FACING);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
      BlockEntityType<T> type) {
    return level.isClientSide
        ? createTickerHelper(type, ModBlockEntities.BEER_KEG_BE.get(), BeerKegBlockEntity::clientTick)
        : null;
  }
}

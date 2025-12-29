package dev.larguma.crawlingmysteries.block.custom;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.block.entity.BeerMugBlockEntity;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeerMugBlock extends BushBlock implements EntityBlock {
  public static final MapCodec<BeerMugBlock> CODEC = simpleCodec(BeerMugBlock::new);
  public static final int MAX_MUGS = 4;
  public static final IntegerProperty MUGS = IntegerProperty.create("mugs", 1, MAX_MUGS);
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  protected static final VoxelShape ONE_MUG = Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0);
  protected static final VoxelShape TWO_MUGS = Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0);
  protected static final VoxelShape THREE_MUGS = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);
  protected static final VoxelShape FOUR_MUGS = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

  public BeerMugBlock() {
    super(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .sound(SoundType.WOOD)
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY));
    this.registerDefaultState(
        this.stateDefinition.any().setValue(MUGS, Integer.valueOf(1)).setValue(FACING, Direction.NORTH));
  }

  protected BeerMugBlock(Properties properties) {
    this();
  }

  @Override
  protected MapCodec<? extends BushBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BeerMugBlockEntity(pos, state);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return switch (state.getValue(MUGS)) {
      case 1 -> ONE_MUG;
      case 2 -> TWO_MUGS;
      case 3 -> THREE_MUGS;
      default -> FOUR_MUGS;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
    if (blockstate.is(this)) {
      boolean itemHasEyes = context.getItemInHand().has(ModDataComponents.GOOGLY_EYES.get());
      boolean blockHasEyes = false;
      if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof BeerMugBlockEntity be) {
        blockHasEyes = be.hasGooglyEyes();
      }

      if (!itemHasEyes && !blockHasEyes) {
        return blockstate.setValue(MUGS, Math.min(4, blockstate.getValue(MUGS) + 1));
      }
    }
    return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    if (level.getBlockEntity(pos) instanceof BeerMugBlockEntity be) {
      if (stack.has(ModDataComponents.GOOGLY_EYES.get())) {
        be.setHasGooglyEyes(stack.get(ModDataComponents.GOOGLY_EYES.get()));
      }
    }
    super.setPlacedBy(level, pos, state, placer, stack);
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    List<ItemStack> drops = new ArrayList<>();
    ItemStack stack = new ItemStack(this);
    stack.setCount(state.getValue(MUGS));

    if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BeerMugBlockEntity be) {
      if (stack.getItem() instanceof BeerMugItem item) {
        item.setBeerLevel(stack, be.getBeerLevel());
      }
      if (be.hasGooglyEyes()) {
        stack.set(ModDataComponents.GOOGLY_EYES.get(), true);
      }
    }
    drops.add(stack);
    return drops;
  }

  @Override
  protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
      BlockPos currentPos, BlockPos facingPos) {
    if (!state.canSurvive(level, currentPos)) {
      return Blocks.AIR.defaultBlockState();
    }

    return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
  }

  @Override
  protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
    return !state.getCollisionShape(level, pos).getFaceShape(Direction.UP).isEmpty()
        || state.isFaceSturdy(level, pos, Direction.UP);
  }

  @Override
  public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
    boolean itemHasEyes = context.getItemInHand().has(ModDataComponents.GOOGLY_EYES.get());
    boolean blockHasEyes = false;
    if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof BeerMugBlockEntity be) {
      blockHasEyes = be.hasGooglyEyes();
    }

    if (itemHasEyes || blockHasEyes) {
      return false;
    }

    return !context.isSecondaryUseActive()
        && context.getItemInHand().getItem() == this.asItem()
        && state.getValue(MUGS) < 4
        || super.canBeReplaced(state, context);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(MUGS, FACING);
  }

  @Override
  protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
    return false;
  }
}

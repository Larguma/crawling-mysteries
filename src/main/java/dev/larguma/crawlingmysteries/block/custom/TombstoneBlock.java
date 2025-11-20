package dev.larguma.crawlingmysteries.block.custom;

import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import dev.larguma.crawlingmysteries.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TombstoneBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
  public static final MapCodec<TombstoneBlock> CODEC = simpleCodec(TombstoneBlock::new);
  public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 14, 12);
  private final SimpleParticleType particle;
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public TombstoneBlock() {
    super(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE)
        .strength(-1.0f, 3600000.0f).noOcclusion().noLootTable());
    this.particle = ParticleTypes.SOUL_FIRE_FLAME;
    this.registerDefaultState(
        this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
  }

  protected TombstoneBlock(Properties properties) {
    super(properties);
    this.particle = null;
  }

  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TombstoneBlockEntity(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED,
        Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
  }

  @Override
  protected FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, WATERLOGGED);
  }

  @Override
  protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    double x = (double) pos.getX() + random.nextDouble();
    double y = (double) pos.getY() + random.nextDouble() + random.nextDouble();
    double z = (double) pos.getZ() + random.nextDouble();
    level.addParticle(this.particle, x, y, z, 0.0, 0.0, 0.0);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hitResult) {
    if (level.getBlockEntity(pos) instanceof TombstoneBlockEntity tombstoneBlockEntity) {
      if (tombstoneBlockEntity.getTombstoneOwner() == player.getGameProfile()) {
        player.giveExperiencePoints(tombstoneBlockEntity.getXp());
        playerWillDestroy(level, pos, state, player);
        level.removeBlock(pos, false);

        if (tombstoneBlockEntity.getGuardianUUID() != null) {
          // getGuardianEntity(level, player,
          // tombstoneBlockEntity).remove(RemovalCause.EXPIRED);
        }
      } else if (tombstoneBlockEntity.getGuardianUUID() == null) {
        playerWillDestroy(level, pos, state, player);
        level.removeBlock(pos, false);
      }  
    }
    return InteractionResult.SUCCESS;
  }

  // private EternalGuardianEntity getGuardianEntity(World world, PlayerEntity player,
  //     TombstoneBlockEntity tombstoneBlockEntity) {
  //   final EternalGuardianEntity[] eternalGuardian = { null };
  //   List<EternalGuardianEntity> eternalGuardians = world
  //       .getEntitiesByClass(EternalGuardianEntity.class, new Box(tombstoneBlockEntity.getPos()).expand(5),
  //           EntityPredicates.EXCEPT_SPECTATOR);
  //   for (EternalGuardianEntity guardian : eternalGuardians) {
  //     if (guardian.getUuid().equals(tombstoneBlockEntity.getGuardianUUID())) {
  //       eternalGuardian[0] = guardian;
  //       break;
  //     }
  //   }
  //   return eternalGuardian[0];
  // }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    dropAllGrave(level, pos);
    return super.playerWillDestroy(level, pos, state, player);
  }

  public void dropAllGrave(Level level, BlockPos pos) {
    if (level.isClientSide())
      return;

    BlockEntity be = level.getBlockEntity(pos);

    if (!(be instanceof TombstoneBlockEntity))
      return;
    TombstoneBlockEntity blockEntity = (TombstoneBlockEntity) be;

    blockEntity.setChanged();

    if (blockEntity.getItems() == null)
      return;

    Helper.scatterItems(level, pos, blockEntity.getItems());

    blockEntity.setItems(NonNullList.of(ItemStack.EMPTY));
  }

  public static void placeTombstone(Player player, List<ItemStack> trinketStacks) {
    Level level = player.getCommandSenderWorld();
    if (level.isClientSide || player.isFakePlayer()) {
      return;
    }

    BlockPos blockPos = player.blockPosition().below(1);
    if (blockPos.getY() <= level.getMinBuildHeight()) {
      blockPos.atY(level.getMinBuildHeight());
    }

    BlockState blockState = level.getBlockState(blockPos);
    Block block = blockState.getBlock();

    NonNullList<ItemStack> combinedInventory = NonNullList.create();

    combinedInventory.addAll(player.getInventory().items);
    combinedInventory.addAll(player.getInventory().armor);
    combinedInventory.addAll(player.getInventory().offhand);
    combinedInventory.addAll(trinketStacks);

    boolean placed = false;

    for (BlockPos pos : BlockPos.withinManhattan(blockPos.above(1), 5, 5, 5)) {
      if (canPlaceTombstone(level, pos)) {
        BlockState state = ModBlocks.TOMBSTONE.get().defaultBlockState().setValue(FACING, player.getDirection());
        // EternalGuardianEntity guardian = spawnEternalGuardian(level, pos,
        // player.getGameProfile());
        placed = level.setBlockAndUpdate(pos, state);
        TombstoneBlockEntity tombstoneBlockEntity = new TombstoneBlockEntity(pos, state);
        tombstoneBlockEntity.setItems(combinedInventory);
        tombstoneBlockEntity.setTombstoneOwner(player.getGameProfile());
        tombstoneBlockEntity.setXp(player.totalExperience);
        // tombstoneBlockEntity.setGuardianUUID(guardian.UUID);
        level.setBlockEntity(tombstoneBlockEntity);

        tombstoneBlockEntity.setChanged();
        block.playerWillDestroy(level, blockPos, blockState, player);

        CrawlingMysteries.LOGGER.info("Tombstone for player: " + player.getName().getString()
            + " spawned at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());

        player.sendSystemMessage(
            Component.translatable("block.crawlingmysteries.tombstone.death", pos.getX(), pos.getY(), pos.getZ()));
        break;
      }
    }

    player.totalExperience = 0;
    player.experienceProgress = 0;
    player.experienceLevel = 0;

    if (!placed) {
      player.getInventory().dropAll();
    }
  }

  private static boolean canPlaceTombstone(Level level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos.above(1));

    if (blockEntity != null)
      return false;

    return !(pos.getY() < level.getMinBuildHeight()
        || pos.getY() > level.getHeight() - level.getMinBuildHeight());
  }
}

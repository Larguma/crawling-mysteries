package larguma.crawling_mysteries.block.custom;

import java.util.List;
import java.util.UUID;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TombstoneBlock extends BlockWithEntity implements Waterloggable {
  public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
  public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
  private final ParticleEffect particle;
  private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 14, 12);

  public TombstoneBlock(Settings settings, ParticleEffect particle) {
    super(settings);
    this.particle = particle;
    setDefaultState(getDefaultState()
        .with(WATERLOGGED, false)
        .with(FACING, Direction.NORTH));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(Properties.HORIZONTAL_FACING, WATERLOGGED);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    BlockPos blockPos = ctx.getBlockPos();
    FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
    BlockState blockState = this.getDefaultState()
        .with(FACING, ctx.getHorizontalPlayerFacing())
        .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    return blockState;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
      WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (state.get(WATERLOGGED)) {
      world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }

    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new TombstoneBlockEntity(pos, state);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    TombstoneBlockEntity tombstoneBlockEntity = (TombstoneBlockEntity) world.getBlockEntity(pos);
    if (tombstoneBlockEntity.getTombstoneOwner() == player.getGameProfile()) {
      player.addExperience(tombstoneBlockEntity.getXp());
      onBreak(world, pos, state, player);
      world.removeBlock(pos, false);

      if (tombstoneBlockEntity.getGuardianUUID() != null) {
        getGuardianEntity(world, player, tombstoneBlockEntity).remove(RemovalReason.DISCARDED);
      }
    } else if (tombstoneBlockEntity.getGuardianUUID() == null) {
      onBreak(world, pos, state, player);
      world.removeBlock(pos, false);
    }
    return ActionResult.SUCCESS;
  }

  private EternalGuardianEntity getGuardianEntity(World world, PlayerEntity player,
      TombstoneBlockEntity tombstoneBlockEntity) {
    final EternalGuardianEntity[] eternalGuardian = { null };
    List<EternalGuardianEntity> eternalGuardians = world
        .getEntitiesByClass(EternalGuardianEntity.class, new Box(tombstoneBlockEntity.getPos()).expand(5),
            EntityPredicates.EXCEPT_SPECTATOR);
    for (EternalGuardianEntity guardian : eternalGuardians) {
      if (guardian.getUuid().equals(tombstoneBlockEntity.getGuardianUUID())) {
        eternalGuardian[0] = guardian;
        break;
      }
    }
    return eternalGuardian[0];
  }

  @Override
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    dropAllGrave(world, pos);
    super.onBreak(world, pos, state, player);
  }

  public void dropAllGrave(World world, BlockPos pos) {
    if (world.isClient)
      return;

    BlockEntity be = world.getBlockEntity(pos);

    if (!(be instanceof TombstoneBlockEntity))
      return;
    TombstoneBlockEntity blockEntity = (TombstoneBlockEntity) be;

    blockEntity.markDirty();

    if (blockEntity.getItems() == null)
      return;

    ItemScatterer.spawn(world, pos, blockEntity.getItems());

    blockEntity.setItems(DefaultedList.copyOf(ItemStack.EMPTY));
  }

  public static void placeTombstone(PlayerEntity player, List<ItemStack> trinketStacks) {
    World world = player.getWorld();
    if (world.isClient || !player.isPlayer())
      return;

    BlockPos blockPos = player.getBlockPos().offset(Direction.DOWN, 1);
    if (blockPos.getY() <= world.getDimension().minY())
      blockPos.withY(world.getDimension().minY());

    BlockState blockState = world.getBlockState(blockPos);
    Block block = blockState.getBlock();

    DefaultedList<ItemStack> combinedInventory = DefaultedList.of();

    combinedInventory.addAll(player.getInventory().main);
    combinedInventory.addAll(player.getInventory().armor);
    combinedInventory.addAll(player.getInventory().offHand);
    combinedInventory.addAll(trinketStacks);

    boolean placed = false;

    for (BlockPos pos : BlockPos.iterateOutwards(blockPos.add(new Vec3i(0, 1, 0)), 5, 5, 5)) {
      if (canPlaceTombstone(world, block, pos)) {
        BlockState state = ModBlocks.TOMBSTONE.getDefaultState()
            .with(FACING, player.getHorizontalFacing());
        EternalGuardianEntity guardian = spawnEternalGuardian(world, pos, player.getGameProfile().getId());
        placed = world.setBlockState(pos, state);
        TombstoneBlockEntity tombstoneBlockEntity = new TombstoneBlockEntity(pos, state);
        tombstoneBlockEntity.setItems(combinedInventory);
        tombstoneBlockEntity.setTombstoneOwner(player.getGameProfile());
        tombstoneBlockEntity.setXp(player.totalExperience);
        tombstoneBlockEntity.setGuardianUUID(guardian.getUuid());
        world.addBlockEntity(tombstoneBlockEntity);

        tombstoneBlockEntity.markDirty();
        block.onBreak(world, blockPos, blockState, player);

        CrawlingMysteries.LOGGER
            .info("Tombstone for player: " + player.getName()
                + " spawned at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());

        player.sendMessage(
            Text.translatable("block.crawling-mysteries.tombstone.death",
                pos.getX(), pos.getY(), pos.getZ()));

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

  private static boolean canPlaceTombstone(World world, Block block, BlockPos blockPos) {
    BlockEntity blockEntity = world.getBlockEntity(blockPos);

    if (blockEntity != null)
      return false;

    return !(blockPos.getY() < world.getDimension().minY()
        || blockPos.getY() > world.getDimension().height() - world.getDimension().minY());
  }

  private static EternalGuardianEntity spawnEternalGuardian(World world, BlockPos pos, UUID owner) {
    EternalGuardianEntity eternalGuardianEntity = new EternalGuardianEntity(ModEntities.ETERNAL_GUARDIAN, world);
    eternalGuardianEntity.setTombstonePos(pos);
    eternalGuardianEntity.setTombstoneOwner(owner);
    eternalGuardianEntity.refreshPositionAndAngles((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5,
        0.0f, 0.0f);
    world.spawnEntity(eternalGuardianEntity);
    eternalGuardianEntity.playSpawnEffects();
    return eternalGuardianEntity;
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    double x = (double) pos.getX() + random.nextDouble();
    double y = (double) pos.getY() + random.nextDouble() + random.nextDouble();
    double z = (double) pos.getZ() + random.nextDouble();
    world.addParticle(this.particle, x, y, z, 0.0, 0.0, 0.0);
  }
}

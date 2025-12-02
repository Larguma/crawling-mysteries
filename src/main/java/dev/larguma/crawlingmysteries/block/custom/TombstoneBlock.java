package dev.larguma.crawlingmysteries.block.custom;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import dev.larguma.crawlingmysteries.item.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.phys.AABB;
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
        .strength(-1.0f, 3600000.0f).noOcclusion().noLootTable().randomTicks());
    this.particle = ParticleTypes.SOUL_FIRE_FLAME;
    this.registerDefaultState(
        this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
  }

  protected TombstoneBlock(Properties properties) {
    super(properties);
    this.particle = ParticleTypes.SOUL_FIRE_FLAME;
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
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    super.animateTick(state, level, pos, random);
    double x = (double) pos.getX() + random.nextDouble();
    double y = (double) pos.getY() + random.nextDouble() + random.nextDouble();
    double z = (double) pos.getZ() + random.nextDouble();
    level.addParticle(this.particle, x, y, z, 0, 0, 0);

    double d0 = (double) pos.getX() + 0.5;
    double d1 = (double) pos.getY();
    double d2 = (double) pos.getZ() + 0.5;
    if (random.nextDouble() < 0.1) {
      level.playLocalSound(d0, d1, d2, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
    }
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hitResult) {
    if (level.getBlockEntity(pos) instanceof TombstoneBlockEntity tombstoneBlockEntity) {
      if (tombstoneBlockEntity.getTombstoneOwner().equals(player.getGameProfile())) {
        player.giveExperiencePoints(tombstoneBlockEntity.getXp());
        playerWillDestroy(level, pos, state, player);
        level.removeBlock(pos, false);

        if (tombstoneBlockEntity.getGuardianUUID() != null) {
          EternalGuardianEntity guardian = getGuardianEntity(level, player, tombstoneBlockEntity);
          if (guardian != null)
            guardian.remove(RemovalReason.DISCARDED);
        }
      } else if (tombstoneBlockEntity.getGuardianUUID() == null) {
        playerWillDestroy(level, pos, state, player);
        level.removeBlock(pos, false);
      }
    }
    return InteractionResult.SUCCESS;
  }

  private EternalGuardianEntity getGuardianEntity(Level level, Player player,
      TombstoneBlockEntity tombstoneBlockEntity) {
    final EternalGuardianEntity[] eternalGuardian = { null };
    List<EternalGuardianEntity> eternalGuardians = level.getEntitiesOfClass(EternalGuardianEntity.class,
        new AABB(tombstoneBlockEntity.getBlockPos()).inflate(128));
    for (EternalGuardianEntity guardian : eternalGuardians) {
      if (guardian.getUUID().equals(tombstoneBlockEntity.getGuardianUUID())) {
        eternalGuardian[0] = guardian;
        break;
      }
    }
    return eternalGuardian[0];
  }

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

    ItemHelper.scatterItems(level, pos, blockEntity.getItems());

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
        UUID guardianUuid = spawnEternalGuardian(level, pos, player.getGameProfile());
        placed = level.setBlockAndUpdate(pos, state);
        TombstoneBlockEntity tombstoneBlockEntity = new TombstoneBlockEntity(pos, state);
        tombstoneBlockEntity.setItems(combinedInventory);
        tombstoneBlockEntity.setTombstoneOwner(player.getGameProfile());
        tombstoneBlockEntity.setXp(player.totalExperience);
        tombstoneBlockEntity.setGuardianUUID(guardianUuid);
        level.setBlockEntity(tombstoneBlockEntity);

        if (level.getBlockState(blockPos).isAir())
          level.setBlockAndUpdate(blockPos, Blocks.GRASS_BLOCK.defaultBlockState());

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

  private static UUID spawnEternalGuardian(Level level, BlockPos pos, GameProfile gameProfile) {
    EternalGuardianEntity eternalGuardianEntity = ModEntities.ETERNAL_GUARDIAN.get().spawn((ServerLevel) level, pos,
        MobSpawnType.EVENT);
    eternalGuardianEntity.setTombstonePos(pos);
    eternalGuardianEntity.setTombstoneOwner(gameProfile.getId());
    eternalGuardianEntity.setTombstoneOwnerName(gameProfile.getName());
    eternalGuardianEntity.moveTo((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, 0.0f, 0.0f);
    eternalGuardianEntity.spawnAnim();
    return eternalGuardianEntity.getUUID();
  }

  private static boolean canPlaceTombstone(Level level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos);

    if (blockEntity != null)
      return false;

    return !level.getBlockState(pos).is(Blocks.BEDROCK)
        && !(pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getHeight() - level.getMinBuildHeight());
  }
}

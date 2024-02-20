package larguma.crawling_mysteries.block.custom;

import javax.swing.text.html.BlockView;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class TombstoneBlock extends BlockWithEntity {

  private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 5, 14, 16, 11);

  public TombstoneBlock(Settings settings) {
    super(settings);
  }

  public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

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
    dropAllGrave(world, pos);
    world.removeBlock(pos, false);
    return ActionResult.SUCCESS;
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

  public static void placeTombstone(World world, BlockPos blockPos, PlayerEntity player) {
    if (world.isClient)
      return;

    if (blockPos.getY() <= world.getDimension().minY()) {
      blockPos = new BlockPos(blockPos.getX(), world.getDimension().minY(), blockPos.getZ());
    }

    BlockState blockState = world.getBlockState(blockPos);
    Block block = blockState.getBlock();

    DefaultedList<ItemStack> combinedInventory = DefaultedList.of();

    combinedInventory.addAll(player.getInventory().main);
    combinedInventory.addAll(player.getInventory().armor);
    combinedInventory.addAll(player.getInventory().offHand);

    boolean placed = false;
    BlockRotation blockRotation = BlockRotation.NONE;

    for (BlockPos pos : BlockPos.iterateOutwards(blockPos.add(new Vec3i(0, 1, 0)), 5, 5, 5)) {
      if (canPlaceTombstone(world, block, pos)) {
        if (pos.getX() % 2 == 0) blockRotation = BlockRotation.CLOCKWISE_90;
        BlockState state = ModBlocks.TOMBSTONE.getDefaultState().rotate(blockRotation);

        placed = world.setBlockState(pos, state);
        TombstoneBlockEntity TombstoneBlockEntity = new TombstoneBlockEntity(pos, state);
        TombstoneBlockEntity.setItems(combinedInventory);
        TombstoneBlockEntity.setGraveOwner(player.getGameProfile());
        TombstoneBlockEntity.setXp(player.totalExperience);
        world.addBlockEntity(TombstoneBlockEntity);

        TombstoneBlockEntity.markDirty();
        block.onBreak(world, blockPos, blockState, player);

        CrawlingMysteries.LOGGER
            .info("Tombstone spawned at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());

        break;
      }
    }

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

}

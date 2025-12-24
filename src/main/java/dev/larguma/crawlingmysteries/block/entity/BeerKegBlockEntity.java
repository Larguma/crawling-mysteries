package dev.larguma.crawlingmysteries.block.entity;

import dev.larguma.crawlingmysteries.block.custom.BeerKegBlock;
import dev.larguma.crawlingmysteries.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BeerKegBlockEntity extends BlockEntity implements GeoBlockEntity {
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  private boolean isPouring = false;
  public static final String IS_POURING_KEY = "is_pouring";

  public BeerKegBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.BEER_KEG_BE.get(), pos, state);
  }

  // #region NBT
  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    if (tag.contains(IS_POURING_KEY)) {
      this.isPouring = tag.getBoolean(IS_POURING_KEY);
    }

    super.loadAdditional(tag, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
    tag.putBoolean(IS_POURING_KEY, this.isPouring);

    super.saveAdditional(tag, registries);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public CompoundTag getUpdateTag(Provider registries) {
    return saveWithoutMetadata(registries);
  }

  public void setIsPouring(boolean isPouring) {
    this.isPouring = isPouring;
    this.setChanged();

    // Sync to clients
    if (this.level != null && !this.level.isClientSide) {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
  }

  public boolean getIsPouring() {
    return this.isPouring;
  }
  // #endregion NBT

  // #region Gecko
  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "base_controller", 0, this::kegAnimController));
  }

  protected <E extends BeerKegBlockEntity> PlayState kegAnimController(final AnimationState<E> state) {
    if (this.isPouring) {
      return state.setAndContinue(RawAnimation.begin().thenPlay("open").thenLoop("flow"));
    } else {
      return state.setAndContinue(RawAnimation.begin().thenPlay("close"));
    }
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  public static void clientTick(Level level, BlockPos pos, BlockState state, BeerKegBlockEntity blockEntity) {
    if (blockEntity.isPouring && level.isClientSide) {
      Direction facing = state.getValue(BeerKegBlock.FACING);

      double baseY = pos.getY() + (1.0 / 16.0);

      // The locator is -7 pixels forward from center in Blockbench
      double offsetForward = -7.0 / 16.0;
      double x, z;

      switch (facing) {
        case NORTH: // -Z
          x = pos.getX() + 0.5;
          z = pos.getZ() + 0.5 + offsetForward;
          break;
        case SOUTH: // +Z
          x = pos.getX() + 0.5;
          z = pos.getZ() + 0.5 - offsetForward;
          break;
        case WEST: // -X
          x = pos.getX() + 0.5 + offsetForward;
          z = pos.getZ() + 0.5;
          break;
        case EAST: // +X
          x = pos.getX() + 0.5 - offsetForward;
          z = pos.getZ() + 0.5;
          break;
        default:
          x = pos.getX() + 0.5;
          z = pos.getZ() + 0.5 + offsetForward;
          break;
      }

      for (int i = 0; i < 4; i++) {
        level.addParticle(ModParticles.BEER_FLOW.get(),
            x + (level.random.nextDouble() - 0.5) * 0.1,
            baseY,
            z + (level.random.nextDouble() - 0.5) * 0.1,
            0,
            -0.1,
            0);
      }
    }
  }
  // #endregion Gecko
}
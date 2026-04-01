package dev.larguma.crawlingmysteries.block.entity.custom;

import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

public class CookingAltarTier2BlockEntity extends BlockEntity implements GeoBlockEntity {

  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  protected static final RawAnimation DISTILLING_ANIM = RawAnimation.begin().thenLoop("distilling");

  public enum DistillingState {
    IDLE,
    DISTILLING,
    SUCCESS,
    FAILED
  }

  private DistillingState distillingState = DistillingState.IDLE;

  public CookingAltarTier2BlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COOKING_ALTAR_TIER_2_BE.get(), pos, state);
  }

  // #region NBT

  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    super.loadAdditional(tag, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
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

  // #endregion NBT

  // #region Getters and Setters

  public DistillingState getDistillingState() {
    return this.distillingState;
  }

  public void setDistillingState(DistillingState state) {
    this.distillingState = state;
    this.setChanged();
    syncToClients();
  }

  private void syncToClients() {
    if (this.level != null && !this.level.isClientSide) {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
  }

  // #endregion Getters and Setters

  // #region GeckoLib Animation

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "distilling_controller", 10, this::distillingAnimController));
  }

  protected <E extends CookingAltarTier2BlockEntity> PlayState distillingAnimController(final AnimationState<E> state) {
    return switch (this.distillingState) {
      case DISTILLING -> state.setAndContinue(DISTILLING_ANIM);
      default -> state.setAndContinue(IDLE_ANIM);
    };
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }

  // #endregion GeckoLib Animation
}

package dev.larguma.crawlingmysteries.block.entity;

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

public class BeerMugBlockEntity extends BlockEntity implements GeoBlockEntity {
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  private static final int MAX_BEER_LEVEL = 4;
  private int beerLevel = MAX_BEER_LEVEL;
  public static final String BEER_LEVEL_KEY = "beer_level";

  public BeerMugBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.BEER_MUG_BE.get(), pos, state);
  }

  // #region NBT
  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    if (tag.contains(BEER_LEVEL_KEY)) {
      this.beerLevel = tag.getInt(BEER_LEVEL_KEY);
    }

    super.loadAdditional(tag, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
    tag.putInt(BEER_LEVEL_KEY, this.beerLevel);

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

  public void setBeerLevel(int level) {
    this.beerLevel = Math.min(level, MAX_BEER_LEVEL);
    this.setChanged();
  }

  public int getBeerLevel() {
    return this.beerLevel;
  }
  // #endregion NBT

  // #region Gecko
  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, this::idleAnimController));
  }

  protected <E extends BeerMugBlockEntity> PlayState idleAnimController(final AnimationState<E> state) {
    return state.setAndContinue(IDLE_ANIM);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }
  // #endregion Gecko
}
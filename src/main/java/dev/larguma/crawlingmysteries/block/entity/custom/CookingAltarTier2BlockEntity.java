package dev.larguma.crawlingmysteries.block.entity.custom;

import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.screen.custom.AlchemicalDistilleryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CookingAltarTier2BlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  protected static final RawAnimation DISTILLING_ANIM = RawAnimation.begin().thenLoop("distilling");

  private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
    @Override
    protected void onContentsChanged(int slot) {
      setChanged();
      syncToClients();
    }

    @Override
    public int getSlotLimit(int slot) {
      return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      for (int i = 0; i < getSlots(); i++) {
        if (getStackInSlot(i).isEmpty()) {
          return stack.is(Tags.Items.FOODS_RAW_MEAT);
        }
      }

      return super.isItemValid(slot, stack);
    }
  };

  private int needlePosition = 0;
  private int needleVelocity = 2;
  private int cookingProgress = 0;
  private int cookingTime = 0;
  private int stabilityScore = 0;
  private int ticksInRedZone = 0;
  private boolean isCooking = false;

  private static final int GREEN_ZONE_MIN = -20;
  private static final int GREEN_ZONE_MAX = 20;
  private static final int MAX_RED_ZONE_TICKS = 60;
  private static final int TOTAL_COOKING_TICKS = 200;

  public enum DistillingState {
    IDLE,
    DISTILLING,
    SUCCESS,
    FAILED
  }

  private DistillingState distillingState = DistillingState.IDLE;

  private final ContainerData containerData = new ContainerData() {
    @Override
    public int get(int index) {
      return switch (index) {
        case AlchemicalDistilleryMenu.DATA_NEEDLE_POSITION -> needlePosition;
        case AlchemicalDistilleryMenu.DATA_COOKING_PROGRESS -> cookingProgress;
        case AlchemicalDistilleryMenu.DATA_COOKING_TIME -> cookingTime;
        case AlchemicalDistilleryMenu.DATA_STABILITY_SCORE -> stabilityScore;
        case AlchemicalDistilleryMenu.DATA_IS_COOKING -> isCooking ? 1 : 0;
        default -> 0;
      };
    }

    @Override
    public void set(int index, int value) {
      switch (index) {
        case AlchemicalDistilleryMenu.DATA_NEEDLE_POSITION -> needlePosition = value;
        case AlchemicalDistilleryMenu.DATA_COOKING_PROGRESS -> cookingProgress = value;
        case AlchemicalDistilleryMenu.DATA_COOKING_TIME -> cookingTime = value;
        case AlchemicalDistilleryMenu.DATA_STABILITY_SCORE -> stabilityScore = value;
        case AlchemicalDistilleryMenu.DATA_IS_COOKING -> isCooking = value == 1;
      }
    }

    @Override
    public int getCount() {
      return AlchemicalDistilleryMenu.DATA_COUNT;
    }
  };

  public CookingAltarTier2BlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COOKING_ALTAR_TIER_2_BE.get(), pos, state);
  }

  // #region NBT

  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    super.loadAdditional(tag, registries);
    if (tag.contains("inventory")) {
      itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }
    needlePosition = tag.getInt("needlePosition");
    cookingProgress = tag.getInt("cookingProgress");
    cookingTime = tag.getInt("cookingTime");
    stabilityScore = tag.getInt("stabilityScore");
    ticksInRedZone = tag.getInt("ticksInRedZone");
    isCooking = tag.getBoolean("isCooking");
    distillingState = DistillingState.values()[tag.getInt("distillingState")];
  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
    super.saveAdditional(tag, registries);
    tag.put("inventory", itemHandler.serializeNBT(registries));
    tag.putInt("needlePosition", needlePosition);
    tag.putInt("cookingProgress", cookingProgress);
    tag.putInt("cookingTime", cookingTime);
    tag.putInt("stabilityScore", stabilityScore);
    tag.putInt("ticksInRedZone", ticksInRedZone);
    tag.putBoolean("isCooking", isCooking);
    tag.putInt("distillingState", distillingState.ordinal());
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

  public ItemStackHandler getItemHandler() {
    return this.itemHandler;
  }

  public ContainerData getContainerData() {
    return this.containerData;
  }

  public DistillingState getDistillingState() {
    return this.distillingState;
  }

  public void setDistillingState(DistillingState state) {
    this.distillingState = state;
    this.setChanged();
    syncToClients();
  }

  public boolean isCooking() {
    return this.isCooking;
  }

  public int getNeedlePosition() {
    return this.needlePosition;
  }

  // #endregion Getters and Setters

  // #region MenuProvider

  @Override
  public Component getDisplayName() {
    return Component.translatable("screen.crawlingmysteries.alchemical_distillery");
  }

  @Override
  public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    return new AlchemicalDistilleryMenu(containerId, playerInventory, this, this.containerData);
  }

  // #endregion MenuProvider

  // #region Minigame Logic
  public static void serverTick(Level level, BlockPos pos, BlockState state, CookingAltarTier2BlockEntity blockEntity) {
    if (blockEntity.isCooking) {
      blockEntity.tickMinigame();

      if (level.random.nextFloat() < 0.05f) {
        level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.3f, 1.0f);
      }
    }
  }

  public static void clientTick(Level level, BlockPos pos, BlockState state, CookingAltarTier2BlockEntity blockEntity) {
    if (blockEntity.isCooking) {
      if (level.random.nextFloat() < 0.15f) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
        double y = pos.getY() + 1.5;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.05, 0);
      }

      if (level.random.nextFloat() < 0.1f) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
        double y = pos.getY() + 1.2;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
        level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0, 0.08, 0);
      }
    }
  }

  private void tickMinigame() {
    needlePosition += needleVelocity;

    if (needlePosition >= 100 || needlePosition <= -100) {
      needleVelocity = -needleVelocity;
      needlePosition = Math.clamp(needlePosition, -100, 100);
    }

    if (level != null && level.random.nextFloat() < 0.05f) {
      needleVelocity += level.random.nextInt(3) - 1; // -1, 0, or 1
      needleVelocity = Math.clamp(needleVelocity, -5, 5);
    }

    boolean inGreenZone = needlePosition >= GREEN_ZONE_MIN && needlePosition <= GREEN_ZONE_MAX;

    if (inGreenZone) {
      stabilityScore += 2;
      ticksInRedZone = Math.max(0, ticksInRedZone - 1);
    } else {
      ticksInRedZone++;
    }

    cookingTime++;
    cookingProgress = (cookingTime * 100) / TOTAL_COOKING_TICKS;

    if (ticksInRedZone >= MAX_RED_ZONE_TICKS) {
      finishCooking(false);
      return;
    }

    if (cookingTime >= TOTAL_COOKING_TICKS) {
      boolean success = stabilityScore >= (TOTAL_COOKING_TICKS * 0.8);
      finishCooking(success);
    }

    setChanged();
    syncToClients();
  }

  public void startCooking() {
    if (isCooking)
      return;

    isCooking = true;
    needlePosition = 0;
    needleVelocity = 2;
    cookingProgress = 0;
    cookingTime = 0;
    stabilityScore = 0;
    ticksInRedZone = 0;
    setDistillingState(DistillingState.DISTILLING);
  }

  public void stabilizeNeedle() {
    if (needlePosition > 0) {
      needlePosition -= 15;
      needleVelocity = Math.max(needleVelocity - 1, -3);
    } else if (needlePosition < 0) {
      needlePosition += 15;
      needleVelocity = Math.min(needleVelocity + 1, 3);
    }
    needlePosition = Math.clamp(needlePosition, -100, 100);
  }

  private void finishCooking(boolean success) {
    isCooking = false;
    cookingProgress = 100;

    for (int i = 0; i < itemHandler.getSlots(); i++) {
      itemHandler.setStackInSlot(i, ItemStack.EMPTY);
    }

    ItemStack outputItem;
    if (success) {
      setDistillingState(DistillingState.SUCCESS);
      outputItem = new ItemStack(Items.SUSPICIOUS_STEW);
      // Play success sound
      if (level != null && !level.isClientSide) {
        level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.7f, 1.2f);
      }
    } else {
      setDistillingState(DistillingState.FAILED);
      outputItem = new ItemStack(Items.ROTTEN_FLESH);
      // Play failure sound
      if (level != null && !level.isClientSide) {
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 0.8f);
      }
    }

    itemHandler.setStackInSlot(0, outputItem);

    cookingTime = 0;
    cookingProgress = 0;
    stabilityScore = 0;
    ticksInRedZone = 0;

    setChanged();
    syncToClients();
  }

  // #endregion Minigame Logic

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

  // #region Sync

  private void syncToClients() {
    if (this.level != null && !this.level.isClientSide) {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
  }

  // #endregion Sync
}

package dev.larguma.crawlingmysteries.block.entity.custom;

import java.util.Optional;

import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.recipe.CookingAltarTier1Recipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
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

public class CookingAltarTier1BlockEntity extends BlockEntity implements GeoBlockEntity {
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  protected static final RawAnimation BOILING_ANIM = RawAnimation.begin().thenPlay("boiling");
  protected static final RawAnimation DONE_ANIM = RawAnimation.begin().thenLoop("done");
  public static final String COOKING_STATE_KEY = "cooking_state";
  public static final String COOKING_PROGRESS_KEY = "cooking_progress";
  public static final String COOKING_OUTPUT_KEY = "cooking_output";
  public static final String ACTIVE_RECIPE_ID_KEY = "active_recipe_id";
  public static final String MAX_COOKING_TIME_KEY = "max_cooking_time";
  public static final int DEFAULT_COOKING_TIME = 100;

  public enum CookingState {
    IDLE,
    BOILING,
    DONE
  }

  private CookingState cookingState = CookingState.IDLE;
  private int cookingProgress = 0;
  private int maxCookingTime = DEFAULT_COOKING_TIME;
  private boolean hasIngredients = false;
  private ItemStack cookingOutput = ItemStack.EMPTY;
  private ResourceLocation activeRecipeId = null;

  public CookingAltarTier1BlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COOKING_ALTAR_TIER_1_BE.get(), pos, state);
  }

  // #region NBT
  @Override
  protected void loadAdditional(CompoundTag tag, Provider registries) {
    if (tag.contains(COOKING_STATE_KEY)) {
      this.cookingState = CookingState.values()[tag.getInt(COOKING_STATE_KEY)];
    }
    if (tag.contains(COOKING_PROGRESS_KEY)) {
      this.cookingProgress = tag.getInt(COOKING_PROGRESS_KEY);
    }
    if (tag.contains(MAX_COOKING_TIME_KEY)) {
      this.maxCookingTime = tag.getInt(MAX_COOKING_TIME_KEY);
    }
    if (tag.contains(ACTIVE_RECIPE_ID_KEY)) {
      this.activeRecipeId = ResourceLocation.tryParse(tag.getString(ACTIVE_RECIPE_ID_KEY));
    }
    if (tag.contains(COOKING_OUTPUT_KEY)) {
      this.cookingOutput = ItemStack.parse(registries, tag.getCompound(COOKING_OUTPUT_KEY)).orElse(ItemStack.EMPTY);
    }
    super.loadAdditional(tag, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, Provider registries) {
    tag.putInt(COOKING_STATE_KEY, this.cookingState.ordinal());
    tag.putInt(COOKING_PROGRESS_KEY, this.cookingProgress);
    tag.putInt(MAX_COOKING_TIME_KEY, this.maxCookingTime);
    if (this.activeRecipeId != null) {
      tag.putString(ACTIVE_RECIPE_ID_KEY, this.activeRecipeId.toString());
    }
    if (!this.cookingOutput.isEmpty()) {
      tag.put(COOKING_OUTPUT_KEY, this.cookingOutput.save(registries));
    }
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
  public CookingState getCookingState() {
    return this.cookingState;
  }

  public void setCookingState(CookingState state) {
    this.cookingState = state;
    this.setChanged();
    syncToClients();
  }

  public int getCookingProgress() {
    return this.cookingProgress;
  }

  public void setCookingProgress(int progress) {
    this.cookingProgress = progress;
    this.setChanged();
  }

  public boolean hasIngredients() {
    return this.hasIngredients;
  }

  public void setHasIngredients(boolean hasIngredients) {
    this.hasIngredients = hasIngredients;
    this.setChanged();
  }

  private void syncToClients() {
    if (this.level != null && !this.level.isClientSide) {
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }
  }
  // #endregion Getters and Setters

  // #region Cooking Logic
  public void startCooking(ResourceLocation recipeId, CookingAltarTier1Recipe recipe) {
    if (this.cookingState == CookingState.IDLE) {
      this.cookingState = CookingState.BOILING;
      this.cookingProgress = 0;
      this.hasIngredients = true;
      this.activeRecipeId = recipeId;
      this.cookingOutput = recipe.getResult().copy();
      this.maxCookingTime = recipe.getCookingTime();
      this.setChanged();
      syncToClients();
    }
  }

  public boolean canCollectWith(ItemStack stack, Level level) {
    if (this.cookingState != CookingState.DONE) {
      return false;
    }
    if (this.activeRecipeId != null && level != null) {
      Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(this.activeRecipeId);
      if (holder.isPresent() && holder.get().value() instanceof CookingAltarTier1Recipe recipe) {
        return recipe.getContainer().test(stack);
      }
    }
    return stack.is(Items.BOWL);
  }

  public ItemStack collectOutput(Level level) {
    ItemStack output = ItemStack.EMPTY;
    if (this.cookingState == CookingState.DONE) {
      if (this.activeRecipeId != null && level != null) {
        Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(this.activeRecipeId);
        if (holder.isPresent() && holder.get().value() instanceof CookingAltarTier1Recipe recipe) {
          output = recipe.getResult().copy();
        }
      }
      if (output.isEmpty() && !this.cookingOutput.isEmpty()) {
        output = this.cookingOutput.copy();
      }
      if (output.isEmpty()) {
        output = new ItemStack(ModItems.SOUL_STEW.get());
      }

      this.cookingState = CookingState.IDLE;
      this.cookingProgress = 0;
      this.hasIngredients = false;
      this.activeRecipeId = null;
      this.cookingOutput = ItemStack.EMPTY;
      this.maxCookingTime = DEFAULT_COOKING_TIME;
      this.setChanged();
      syncToClients();
    }
    return output;
  }

  public static void serverTick(Level level, BlockPos pos, BlockState state, CookingAltarTier1BlockEntity blockEntity) {
    if (blockEntity.cookingState == CookingState.BOILING) {
      blockEntity.cookingProgress++;

      if (blockEntity.cookingProgress >= blockEntity.maxCookingTime) {
        blockEntity.cookingState = CookingState.DONE;
        blockEntity.cookingProgress = blockEntity.maxCookingTime;
        blockEntity.setChanged();
        blockEntity.syncToClients();
      }
    }
  }

  public static void clientTick(Level level, BlockPos pos, BlockState state, CookingAltarTier1BlockEntity blockEntity) {
    if (blockEntity.cookingState == CookingState.BOILING || blockEntity.cookingState == CookingState.DONE) {
      if (level.random.nextFloat() < 0.1F) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.06, 0);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0.06, 0);
      }

      if (blockEntity.cookingState == CookingState.BOILING && level.random.nextFloat() < 0.2F) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
        level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0, 0.1, 0);
      }

    }

    if (blockEntity.cookingState == CookingState.DONE && level.random.nextFloat() < 0.05F) {
      double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
      double y = pos.getY() + 1.2;
      double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3;
      level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.02, 0);
    }

  }
  // #endregion Cooking Logic

  // #region GeckoLib Animation
  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "cooking_controller", 10, this::cookingAnimController));
  }

  protected <E extends CookingAltarTier1BlockEntity> PlayState cookingAnimController(final AnimationState<E> state) {
    return switch (this.cookingState) {
      case BOILING -> state.setAndContinue(BOILING_ANIM);
      case DONE -> state.setAndContinue(DONE_ANIM);
      default -> state.setAndContinue(IDLE_ANIM);
    };
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }
  // #endregion GeckoLib Animation
}

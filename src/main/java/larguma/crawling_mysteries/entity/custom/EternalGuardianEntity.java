package larguma.crawling_mysteries.entity.custom;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.TombstoneBlockEntity;
import larguma.crawling_mysteries.entity.ai.GoToTombstoneGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class EternalGuardianEntity extends HostileEntity implements GeoEntity {

  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  public BlockPos tombstonePos;
  private UUID tombstoneOwner;
  public static final String TOMBSTONE_POS_KEY = "TombstonePos";
  public static final String TOMBSTONE_OWNER_KEY = "TombstoneOwner";

  public EternalGuardianEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.tombstonePos = new BlockPos(0, 0, 0);
    this.tombstoneOwner = null;
  }

  @Override
  @Nullable
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
      @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  public static DefaultAttributeContainer.Builder setAttributes() {
    return HostileEntity.createMobAttributes()
        .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, 4)
        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8)
        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000)
        .add(EntityAttributes.GENERIC_ARMOR, 20)
        .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 15);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(1, new MeleeAttackGoal(this, 1, false));
    this.goalSelector.add(2, new GoToTombstoneGoal(this));
    this.goalSelector.add(3, new SwimGoal(this));
    this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.add(5, new LookAroundGoal(this));

    this.targetSelector.add(1, new RevengeGoal(this));
    this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    Entity entity;

    entity = source.getAttacker();
    if (entity == null || !(entity instanceof PlayerEntity)) {
      return false;
    }
    return super.damage(source, amount);
  }

  @Override
  public void onDeath(DamageSource source) {
    if (this.hasTombstone()) {
      BlockPos pos = this.getTombstonePos();
      TombstoneBlockEntity tombstoneBlockEntity = getTombstone(pos);
      if (tombstoneBlockEntity != null) {
        tombstoneBlockEntity.setGuardianUUID(null);
      }
    }
    super.onDeath(source);
  }

  @Override
  public void checkDespawn() {
    if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
      this.discard();
      return;
    }
    this.despawnCounter = 0;
  }

  @Override
  public boolean canBreatheInWater() {
    return true;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);

    if (this.hasTombstone()) {
      nbt.put(TOMBSTONE_POS_KEY, NbtHelper.fromBlockPos(this.getTombstonePos()));
    }
    if (this.hasTombstoneOwner()) {
      nbt.put(TOMBSTONE_OWNER_KEY, NbtHelper.fromUuid(this.getTombstoneOwner()));
    }
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    this.tombstonePos = null;
    if (nbt.contains(TOMBSTONE_POS_KEY)) {
      this.tombstonePos = NbtHelper.toBlockPos(nbt.getCompound(TOMBSTONE_POS_KEY));
    }
    if (nbt.contains(TOMBSTONE_OWNER_KEY)) {
      this.tombstoneOwner = NbtHelper.toUuid(nbt.get(TOMBSTONE_OWNER_KEY));
    }
    super.readCustomDataFromNbt(nbt);
  }

  @Nullable
  public BlockPos getTombstonePos() {
    return this.tombstonePos;
  }

  public boolean hasTombstone() {
    return this.tombstonePos != null;
  }

  public void setTombstonePos(BlockPos tombstonePos) {
    this.tombstonePos = tombstonePos;
  }

  public void setTombstoneOwner(UUID owner) {
    this.tombstoneOwner = owner;
  }

  public UUID getTombstoneOwner() {
    return this.tombstoneOwner != null ? this.tombstoneOwner
        : CrawlingMysteries.ELDRICTH_WEAVER_UUID;
  }

  public boolean hasTombstoneOwner() {
    return this.tombstoneOwner != null;
  }

  @Override
  public void registerControllers(ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
  }

  private <T extends GeoAnimatable> PlayState predicate(AnimationState<EternalGuardianEntity> tAnimationState) {
    if (tAnimationState.isMoving()) {
      // tAnimationState.getController()
      // .setAnimation(RawAnimation.begin().then("animation.eternal_guardian.walk",
      // Animation.LoopType.LOOP));
    } else {
      tAnimationState.getController()
          .setAnimation(RawAnimation.begin().then("animation.eternal_guardian.idle", Animation.LoopType.LOOP));
    }
    return PlayState.CONTINUE;
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return cache;
  }

  public boolean isTombstone(BlockPos pos) {
    if (pos == null) {
      return false;
    }
    return this.getWorld().getBlockState(pos).isOf(ModBlocks.TOMBSTONE);
  }

  public TombstoneBlockEntity getTombstone(BlockPos pos) {
    if (!isTombstone(pos)) {
      return null;
    }
    return (TombstoneBlockEntity) this.getWorld().getBlockEntity(pos);
  }

  public boolean isWithinDistance(BlockPos pos, int distance) {
    if (pos == null) {
      return false;
    }
    return pos.isWithinDistance(this.getBlockPos(), (double) distance);
  }

  public void startMovingTo(BlockPos pos) {
    Vec3d vec3d2;
    Vec3d vec3d = Vec3d.ofBottomCenter(pos);
    int i = 0;
    BlockPos blockPos = this.getBlockPos();
    int j = (int) vec3d.y - blockPos.getY();
    if (j > 2) {
      i = 4;
    } else if (j < -2) {
      i = -4;
    }
    int k = 6;
    int l = 8;
    int m = blockPos.getManhattanDistance(pos);
    if (m < 15) {
      k = m / 2;
      l = m / 2;
    }
    if ((vec3d2 = NoWaterTargeting.find(this, k, l, i, vec3d, 0.3141592741012573)) == null) {
      return;
    }
    this.navigation.setRangeMultiplier(0.5f);
    this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
  }
}
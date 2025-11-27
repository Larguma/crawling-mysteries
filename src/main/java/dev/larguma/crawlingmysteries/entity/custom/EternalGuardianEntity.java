package dev.larguma.crawlingmysteries.entity.custom;

import java.util.Optional;
import java.util.UUID;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import dev.larguma.crawlingmysteries.entity.ai.GoToTombstoneGoal;
import dev.larguma.crawlingmysteries.entity.ai.ProtectTombstoneGoal;
import dev.larguma.crawlingmysteries.util.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EternalGuardianEntity extends Monster implements GeoEntity {
  private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
  protected static final RawAnimation MELEE_ANIM = RawAnimation.begin().thenPlay("melee");
  public static final EntityDataAccessor<Optional<BlockPos>> TOMBSTONE_POS = SynchedEntityData.defineId(
      EternalGuardianEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
  public static final EntityDataAccessor<Optional<UUID>> TOMBSTONE_OWNER = SynchedEntityData.defineId(
      EternalGuardianEntity.class, EntityDataSerializers.OPTIONAL_UUID);
  public static final EntityDataAccessor<String> TOMBSTONE_OWNER_NAME = SynchedEntityData.defineId(
      EternalGuardianEntity.class, EntityDataSerializers.STRING);
  public final float speed = 1f;

  public EternalGuardianEntity(EntityType<? extends Monster> entityType, Level level) {
    super(entityType, level);
    this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
    this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    this.xpReward = Enemy.XP_REWARD_LARGE;
  }

  public EternalGuardianEntity(EntityType<? extends EternalGuardianEntity> type, Level level, double x, double y,
      double z) {
    this(type, level);
    this.setPos(x, y, z);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 60)
        .add(Attributes.ATTACK_DAMAGE, 5)
        .add(Attributes.ATTACK_SPEED, 2)
        .add(Attributes.MOVEMENT_SPEED, 0.25f)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1000)
        .add(Attributes.ARMOR, 20)
        .add(Attributes.ARMOR_TOUGHNESS, 15);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new ProtectTombstoneGoal(this, speed, false));
    this.goalSelector.addGoal(2, new GoToTombstoneGoal(this));
    this.goalSelector.addGoal(3, new FloatGoal(this));
    this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8f));
    this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    this.goalSelector.addGoal(6, new RandomStrollGoal(this, speed));

    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false) {

    });
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    Entity entity = source.getEntity();
    if (entity == null || !(entity instanceof Player) || source.is(DamageTypeTags.IS_PROJECTILE)) {
      return false;
    }
    return super.hurt(source, amount);
  }

  @Override
  public void die(DamageSource damageSource) {
    if (this.hasTombstone()) {
      TombstoneBlockEntity tombstoneBlockEntity = getTombstone(this.getTombstonePos().get());
      if (tombstoneBlockEntity != null)
        tombstoneBlockEntity.setGuardianUUID(null);
    }
    super.die(damageSource);
  }

  @Override
  protected boolean shouldDespawnInPeaceful() {
    return false;
  }

  @Override
  public boolean isPersistenceRequired() {
    return true;
  }

  // #region NBT
  @Override
  public void readAdditionalSaveData(CompoundTag compound) {
    if (compound.contains("tombstone_pose"))
      this.entityData.set(TOMBSTONE_POS, Optional.of(NbtHelper.toBlockPos(compound.getCompound("tombstone_pose"))));
    if (compound.contains("tombstone_owner"))
      this.entityData.set(TOMBSTONE_OWNER, Optional.of(compound.getUUID("tombstone_owner")));
    if (compound.contains("tombstone_owner_name"))
      this.entityData.set(TOMBSTONE_OWNER_NAME, compound.getString("tombstone_owner_name"));
    super.readAdditionalSaveData(compound);
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compound) {
    if (this.getTombstonePos().isPresent())
      compound.put("tombstone_pose", NbtHelper.fromBlockPos(this.getTombstonePos().get()));
    if (this.getTombstoneOwner().isPresent())
      compound.putUUID("tombstone_owner", this.getTombstoneOwner().get());
    compound.putString("tombstone_owner_name", this.getTombstoneOwnerName());
    super.addAdditionalSaveData(compound);
  }

  @Override
  protected void defineSynchedData(Builder builder) {
    builder.define(TOMBSTONE_POS, Optional.empty());
    builder.define(TOMBSTONE_OWNER, Optional.of(CrawlingMysteries.ELDRICTH_WEAVER_UUID));
    builder.define(TOMBSTONE_OWNER_NAME, CrawlingMysteries.ELDRICTH_WEAVER_NAME);
    super.defineSynchedData(builder);
  }
  // #endregion NBT

  // #region Gecko
  @Override
  public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "Idle", 5, this::idleAnimController));
    controllers.add(new AnimationController<>(this, "Attack", 5, this::attackAnimController));
  }

  protected <E extends EternalGuardianEntity> PlayState idleAnimController(final AnimationState<E> event) {
    return event.setAndContinue(IDLE_ANIM);
  }

  protected <E extends EternalGuardianEntity> PlayState attackAnimController(final AnimationState<E> event) {
    if (this.swinging && event.getController().getAnimationState().equals(State.STOPPED)) {
      event.getController().forceAnimationReset();
      event.getController().setAnimation(MELEE_ANIM);
      this.swinging = false;
    }
    return PlayState.CONTINUE;
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
  }
  // #endregion Gecko

  // #region Tombstone
  public Optional<BlockPos> getTombstonePos() {
    return this.entityData.get(TOMBSTONE_POS);
  }

  public boolean hasTombstone() {
    return this.entityData.get(TOMBSTONE_POS).isPresent();
  }

  public void setTombstonePos(BlockPos tombstonePos) {
    this.entityData.set(TOMBSTONE_POS, Optional.of(tombstonePos));
  }

  public void setTombstoneOwner(UUID owner) {
    this.entityData.set(TOMBSTONE_OWNER, Optional.of(owner));
  }

  public Optional<UUID> getTombstoneOwner() {
    return this.entityData.get(TOMBSTONE_OWNER);
  }

  public boolean hasTombstoneOwner() {
    return this.entityData.get(TOMBSTONE_OWNER).isPresent();
  }

  public void setTombstoneOwnerName(String name) {
    this.setCustomName(Component.translatable("entity.crawlingmysteries.eternal_guardian.custom_name", name));
    this.entityData.set(TOMBSTONE_OWNER_NAME, name);
  }

  public String getTombstoneOwnerName() {
    return this.entityData.get(TOMBSTONE_OWNER_NAME);
  }

  public boolean hasTombstoneOwnerName() {
    return !this.entityData.get(TOMBSTONE_OWNER_NAME).isEmpty();
  }

  public boolean isTombstone(BlockPos pos) {
    if (pos == null) {
      return false;
    }
    return this.level().getBlockState(pos).is(ModBlocks.TOMBSTONE);
  }

  public TombstoneBlockEntity getTombstone(BlockPos pos) {
    if (!isTombstone(pos)) {
      return null;
    }
    return (TombstoneBlockEntity) this.level().getBlockEntity(pos);
  }

  public boolean isWithinDistance(BlockPos pos, int distance) {
    if (pos == null) {
      return false;
    }
    return pos.closerThan(this.blockPosition(), (double) distance);
  }
  // #endregion Tombstone
}

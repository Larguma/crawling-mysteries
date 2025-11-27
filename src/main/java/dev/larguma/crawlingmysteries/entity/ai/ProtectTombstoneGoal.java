package dev.larguma.crawlingmysteries.entity.ai;

import java.util.UUID;

import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ProtectTombstoneGoal extends MeleeAttackGoal {

  protected final EternalGuardianEntity mob;

  public ProtectTombstoneGoal(EternalGuardianEntity mob, double speed, boolean pauseWhenMobIdle) {
    super(mob, speed, pauseWhenMobIdle);
    this.mob = mob;
  }

  @Override
  public boolean canUse() {
    LivingEntity livingEntity = this.mob.getTarget();
    UUID tombstoneOwner = this.mob.getTombstoneOwner().get();
    if (livingEntity != null && livingEntity.getUUID().equals(tombstoneOwner)) {
      return false;
    }
    return super.canUse();
  }
}
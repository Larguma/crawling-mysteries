package dev.larguma.crawlingmysteries.entity.ai;

import java.util.UUID;

import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ProtectTombstoneGoal extends MeleeAttackGoal {

  protected final EternalGuardianEntity entity;

  public ProtectTombstoneGoal(EternalGuardianEntity mob, double speed, boolean pauseWhenMobIdle) {
    super(mob, speed, pauseWhenMobIdle);
    this.entity = mob;
  }

  @Override
  public boolean canUse() {
    LivingEntity livingEntity = this.entity.getTarget();
    if (this.entity.getTombstoneOwner().isPresent()) {
      UUID tombstoneOwner = this.entity.getTombstoneOwner().get();
      if (livingEntity != null && livingEntity.getUUID().equals(tombstoneOwner)) {
        return false;
      }
    }
    return super.canUse();
  }
}
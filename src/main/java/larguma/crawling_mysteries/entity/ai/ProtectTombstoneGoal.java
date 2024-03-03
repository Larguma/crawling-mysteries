package larguma.crawling_mysteries.entity.ai;

import java.util.UUID;

import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class ProtectTombstoneGoal extends MeleeAttackGoal {
  protected final EternalGuardianEntity mob;

  public ProtectTombstoneGoal(EternalGuardianEntity mob, double speed, boolean pauseWhenMobIdle) {
    super(mob, speed, pauseWhenMobIdle);
    this.mob = mob;
  }

  @Override
  public boolean canStart() {
    LivingEntity livingEntity = this.mob.getTarget();
    UUID tombstoneOwner = this.mob.getTombstoneOwner();
    if (livingEntity != null && livingEntity.getUuid().equals(tombstoneOwner)) {
      return false;
    }
    return super.canStart();
  }
}

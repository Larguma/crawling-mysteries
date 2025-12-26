package dev.larguma.crawlingmysteries.entity.ai;

import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class DrunkAggroGoal extends NearestAttackableTargetGoal<LivingEntity> {

  public DrunkAggroGoal(Mob mob) {
    super(mob, LivingEntity.class, 10, true, false, null);
  }

  @Override
  public boolean canUse() {
    if (!this.mob.hasEffect(ModMobEffects.DRUNK)) {
      return false;
    }
    return super.canUse();
  }

  @Override
  protected void findTarget() {
    this.target = this.mob.level().getNearestEntity(Monster.class, this.targetConditions, this.mob, this.mob.getX(),
        this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().inflate(10.0D));

    if (this.target == this.mob) {
      this.target = null;
    }
  }

  @Override
  public void start() {
    if (this.mob.getTarget() instanceof Player) {
      this.mob.setTarget(null);
    }
    super.start();
  }

}

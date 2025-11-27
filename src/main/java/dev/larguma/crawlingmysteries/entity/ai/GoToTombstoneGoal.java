package dev.larguma.crawlingmysteries.entity.ai;

import java.util.EnumSet;

import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoToTombstoneGoal extends Goal {

  private final EternalGuardianEntity eternalGuardian;

  public GoToTombstoneGoal(EternalGuardianEntity eternalGuardian) {
    this.eternalGuardian = eternalGuardian;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    return this.eternalGuardian.getTombstonePos().isPresent()
        && !this.eternalGuardian.isWithinDistance(this.eternalGuardian.getTombstonePos().get(), 2);
  }

  @Override
  public void stop() {
    this.eternalGuardian.getNavigation().stop();
  }

  @Override
  public void tick() {
    if (this.eternalGuardian.getTombstonePos().isPresent()) {
      BlockPos pos = this.eternalGuardian.getTombstonePos().get();
      if (pos == null || this.eternalGuardian.getNavigation().isInProgress()) {
        return;
      }
      this.eternalGuardian.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 5, this.eternalGuardian.speed);
    }
  }

}

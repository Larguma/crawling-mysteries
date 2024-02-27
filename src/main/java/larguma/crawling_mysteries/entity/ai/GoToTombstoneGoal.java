package larguma.crawling_mysteries.entity.ai;

import java.util.EnumSet;

import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class GoToTombstoneGoal extends Goal {

  private final EternalGuardianEntity eternalGuardian;

  public GoToTombstoneGoal(EternalGuardianEntity eternalGuardian) {
    this.eternalGuardian = eternalGuardian;
    this.setControls(EnumSet.of(Goal.Control.MOVE));
  }

  @Override
  public boolean canStart() {
    return this.eternalGuardian.getTombstonePos() != null
        && !this.eternalGuardian.isWithinDistance(this.eternalGuardian.getTombstonePos(), 2);
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  public void stop() {
    this.eternalGuardian.getNavigation().stop();
  }

  @Override
  public void tick() {
    BlockPos pos = this.eternalGuardian.getTombstonePos();
    if (pos == null || this.eternalGuardian.getNavigation().isFollowingPath()) {
      return;
    }
    this.eternalGuardian.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), this.eternalGuardian.speed);
  }
}

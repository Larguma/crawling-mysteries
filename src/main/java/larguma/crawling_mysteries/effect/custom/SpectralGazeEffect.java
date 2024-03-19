package larguma.crawling_mysteries.effect.custom;

import java.util.List;

import larguma.crawling_mysteries.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SpectralGazeEffect extends StatusEffect {

  private static final int EFFECT_RADIUS = 40;

  public SpectralGazeEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0x7b33d7);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    World world = entity.getWorld();
    if (!world.isClient) {
      int duration = entity.getStatusEffect(this).getDuration();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();

      Scoreboard scoreboard = entity.getCommandSource().getServer().getScoreboard();
      Team teamNeutral = ModUtils.getOrCreateTeam(scoreboard, "team_neutral", Formatting.YELLOW);
      Team teamPassive = ModUtils.getOrCreateTeam(scoreboard, "team_passive", Formatting.GREEN);
      Team teamHostile = ModUtils.getOrCreateTeam(scoreboard, "team_hostile", Formatting.RED);
      Team teamWater = ModUtils.getOrCreateTeam(scoreboard, "team_water", Formatting.BLUE);

      List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
          new Box(x - EFFECT_RADIUS, y - EFFECT_RADIUS, z - EFFECT_RADIUS, x + EFFECT_RADIUS, y + EFFECT_RADIUS,
              z + EFFECT_RADIUS),
          (livingEntity) -> livingEntity != entity);

      StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, 5, 0, true, false,
          false);
      for (LivingEntity livingEntity : entities) {
        if (duration > 1) {
          if (livingEntity instanceof Angerable) {
            scoreboard.addScoreHolderToTeam(livingEntity.getNameForScoreboard(), teamNeutral);
          } else if (livingEntity instanceof AnimalEntity) {
            scoreboard.addScoreHolderToTeam(livingEntity.getNameForScoreboard(), teamPassive);
          } else if (livingEntity instanceof Monster) {
            scoreboard.addScoreHolderToTeam(livingEntity.getNameForScoreboard(), teamHostile);
          } else if (livingEntity instanceof WaterCreatureEntity) {
            scoreboard.addScoreHolderToTeam(livingEntity.getNameForScoreboard(), teamWater);
          }
        } else if (livingEntity.getScoreboardTeam() != null) {
          if (livingEntity instanceof Angerable) {
            scoreboard.removeScoreHolderFromTeam(livingEntity.getNameForScoreboard(), teamNeutral);
          } else if (livingEntity instanceof AnimalEntity) {
            scoreboard.removeScoreHolderFromTeam(livingEntity.getNameForScoreboard(), teamPassive);
          } else if (livingEntity instanceof Monster) {
            scoreboard.removeScoreHolderFromTeam(livingEntity.getNameForScoreboard(), teamHostile);
          } else if (livingEntity instanceof WaterCreatureEntity) {
            scoreboard.removeScoreHolderFromTeam(livingEntity.getNameForScoreboard(), teamWater);
          }
        }
        livingEntity.addStatusEffect(statusEffectInstance);
      }
    }
    super.applyUpdateEffect(entity, amplifier);

  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

}

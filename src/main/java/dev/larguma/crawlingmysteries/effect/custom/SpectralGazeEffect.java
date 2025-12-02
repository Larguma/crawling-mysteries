package dev.larguma.crawlingmysteries.effect.custom;

import java.util.List;

import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import dev.larguma.crawlingmysteries.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class SpectralGazeEffect extends MobEffect {

  private static final int EFFECT_RADIUS = 16;
  private static final String TEAM_NEUTRAL = "crawlingmysteries.spectral_gaze.neutral";
  private static final String TEAM_PASSIVE = "crawlingmysteries.spectral_gaze.passive";
  private static final String TEAM_HOSTILE = "crawlingmysteries.spectral_gaze.hostile";
  private static final String TEAM_WATER = "crawlingmysteries.spectral_gaze.water";

  public SpectralGazeEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x7b33d7);
  }

  @Override
  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    // amplifier 0 = only white glow and self
    // amplifier 1 = no more self
    // amplifier 3 = adds coloring
    // amplifier 4+ = only increases radius
    if (entity.level().isClientSide) {
      return super.applyEffectTick(entity, amplifier);
    }

    MobEffectInstance effectInstance = entity.getEffect(ModMobEffects.SPECTRAL_GAZE);

    if (effectInstance == null) {
      return super.applyEffectTick(entity, amplifier);
    }

    Scoreboard scoreboard = entity.getCommandSenderWorld().getServer().getScoreboard();
    int duration = effectInstance.getDuration();

    if (duration <= 1) {
      removeAllTeams(scoreboard);
      return super.applyEffectTick(entity, amplifier);
    }

    PlayerTeam teamNeutral = ModUtils.getOrCreateTeam(scoreboard, TEAM_NEUTRAL, ChatFormatting.YELLOW);
    PlayerTeam teamPassive = ModUtils.getOrCreateTeam(scoreboard, TEAM_PASSIVE, ChatFormatting.GREEN);
    PlayerTeam teamHostile = ModUtils.getOrCreateTeam(scoreboard, TEAM_HOSTILE, ChatFormatting.RED);
    PlayerTeam teamWater = ModUtils.getOrCreateTeam(scoreboard, TEAM_WATER, ChatFormatting.AQUA);
    MobEffectInstance glowingEffect = new MobEffectInstance(MobEffects.GLOWING, 3, 0, true, false, true);

    List<LivingEntity> entities = entity.level().getEntitiesOfClass(
        LivingEntity.class,
        entity.getBoundingBox().inflate(EFFECT_RADIUS * (1 + amplifier)),
        livingEntity -> amplifier == 0 || livingEntity != entity);

    for (LivingEntity livingEntity : entities) {
      if (amplifier >= 3) {
        PlayerTeam team = getTeamForEntity(livingEntity, teamWater, teamHostile, teamNeutral, teamPassive);
        if (team != null) {
          scoreboard.addPlayerToTeam(livingEntity.getScoreboardName(), team);
        }
      }
      livingEntity.addEffect(glowingEffect);
    }

    return super.applyEffectTick(entity, amplifier);
  }

  private PlayerTeam getTeamForEntity(LivingEntity entity, PlayerTeam teamWater, PlayerTeam teamHostile,
      PlayerTeam teamNeutral, PlayerTeam teamPassive) {
    if (entity instanceof WaterAnimal) {
      return teamWater;
    }
    if (entity instanceof Monster || entity instanceof Enemy) {
      return teamHostile;
    }
    if (entity instanceof NeutralMob) {
      return teamNeutral;
    }
    if (entity instanceof Animal) {
      return teamPassive;
    }
    return null;
  }

  private void removeAllTeams(Scoreboard scoreboard) {
    ModUtils.removeTeam(scoreboard, TEAM_NEUTRAL);
    ModUtils.removeTeam(scoreboard, TEAM_PASSIVE);
    ModUtils.removeTeam(scoreboard, TEAM_HOSTILE);
    ModUtils.removeTeam(scoreboard, TEAM_WATER);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
    return true;
  }

}

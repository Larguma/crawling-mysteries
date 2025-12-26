package dev.larguma.crawlingmysteries.effect.custom;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DrunkEffect extends MobEffect {

  public DrunkEffect() {
    super(MobEffectCategory.HARMFUL, 0xdaa520);
  }

  @Override
  public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
    if (livingEntity.getRandom().nextFloat() < 0.2f) {
      float strength = (amplifier + 1) * 0.1f;
      double pushX = (livingEntity.getRandom().nextDouble()) * strength;
      double pushZ = (livingEntity.getRandom().nextDouble()) * strength;

      livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(pushX, 0, pushZ));
      livingEntity.hasImpulse = true;
    }

    float time = (livingEntity.tickCount + livingEntity.getId()) * 0.03F;
    float sway = (Mth.sin(time) * 1.2F + Mth.sin(time * 1.7F) * 0.6F) * (amplifier + 1) * 0.3F;

    livingEntity.setYRot(livingEntity.getYRot() + sway);
    livingEntity.setYHeadRot(livingEntity.getYHeadRot() + sway);

    return true;
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return true;
  }
}

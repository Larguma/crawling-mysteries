package dev.larguma.crawlingmysteries.effect.custom;

import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class VoidWalkerEffect extends MobEffect {

  public VoidWalkerEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x4B0082);
  }

  @Override
  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    if (entity.level().isClientSide || !(entity instanceof Player)) {
      return super.applyEffectTick(entity, amplifier);
    }

    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 2, true, false, false));
    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2, true, false, false));
    entity.addEffect(new MobEffectInstance(ModMobEffects.AETHER_SIGHT, 400, 0, true, false, false));

    return super.applyEffectTick(entity, amplifier);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return duration % 80 == 0;
  }

  @Override
  public void onEffectStarted(LivingEntity entity, int amplifier) {
    super.onEffectStarted(entity, amplifier);

    if (!entity.level().isClientSide && entity instanceof Player) {
      entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 2, true, false, false));
      entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2, true, false, false));
      entity.addEffect(new MobEffectInstance(ModMobEffects.AETHER_SIGHT, 400, 0, true, false, false));
    }
  }

}

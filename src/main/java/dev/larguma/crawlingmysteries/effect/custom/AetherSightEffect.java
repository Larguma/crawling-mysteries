package dev.larguma.crawlingmysteries.effect.custom;

import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AetherSightEffect extends MobEffect {

  public AetherSightEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x44AAFF);
  }

  @Override
  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    if (entity.level().isClientSide || !(entity instanceof Player)) {
      return super.applyEffectTick(entity, amplifier);
    }

    entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false, false));
    entity.addEffect(new MobEffectInstance(ModMobEffects.IRON_STOMACH, 400, 0, true, false, false));

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
      entity.addEffect(new MobEffectInstance(ModMobEffects.IRON_STOMACH, 400, 0, true, false, false));
      entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false, false));
    }
  }
}

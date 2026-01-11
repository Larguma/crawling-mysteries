package dev.larguma.crawlingmysteries.effect.custom;

import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IronStomachEffect extends MobEffect {

  public IronStomachEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x8B4513);
  }

  @Override
  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    if (entity.level().isClientSide || !(entity instanceof Player player)) {
      return super.applyEffectTick(entity, amplifier);
    }

    MobEffectInstance effectInstance = entity.getEffect(ModMobEffects.IRON_STOMACH);
    if (effectInstance == null) {
      return super.applyEffectTick(entity, amplifier);
    }

    int duration = effectInstance.getDuration();
    if (duration <= 1) {
      return super.applyEffectTick(entity, amplifier);
    }

    player.getFoodData().setFoodLevel(20);
    player.getFoodData().setSaturation(20.0f);

    return super.applyEffectTick(entity, amplifier);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    return duration % 80 == 0;
  }

}

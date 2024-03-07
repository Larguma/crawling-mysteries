package larguma.crawling_mysteries.effect;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SpectralGazeEffect extends StatusEffect {

  private static final int EFFECT_RADIUS = 40;

  protected SpectralGazeEffect(StatusEffectCategory category, int color) {
    super(category, color);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    World world = entity.getWorld();
    if (!world.isClient) {
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();

      List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
          new Box(x - EFFECT_RADIUS, y - EFFECT_RADIUS, z - EFFECT_RADIUS, x + EFFECT_RADIUS, y + EFFECT_RADIUS,
              z + EFFECT_RADIUS),
          (livingEntity) -> livingEntity != entity);

      StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, 5, 0, true, false,
          false);
      for (LivingEntity livingEntity : entities) {
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

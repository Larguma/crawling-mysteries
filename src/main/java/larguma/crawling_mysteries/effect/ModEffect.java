package larguma.crawling_mysteries.effect;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import larguma.crawling_mysteries.effect.custom.SpectralGazeEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEffect implements AutoRegistryContainer<StatusEffect> {
  public static final StatusEffect SPECTRAL_GAZE = new SpectralGazeEffect(StatusEffectCategory.BENEFICIAL, 0x7b33d7);

  @Override
  public Registry<StatusEffect> getRegistry() {
    return Registries.STATUS_EFFECT;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<StatusEffect> getTargetFieldType() {
    return (Class<StatusEffect>) (Object) StatusEffect.class;
  }
}

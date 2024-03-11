package larguma.crawling_mysteries.effect;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import larguma.crawling_mysteries.effect.custom.SpectralGazeEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEffect implements AutoRegistryContainer<StatusEffect> {
  public static final StatusEffect SPECTRAL_GAZE = new SpectralGazeEffect();

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

package larguma.crawling_mysteries.effect;

import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffect {
  public static StatusEffect SPECTRAL_GAZE;

  public static StatusEffect registerStatusEffect(String name) {
    return Registry.register(Registries.STATUS_EFFECT, new Identifier(CrawlingMysteries.MOD_ID, name),
        new SpectralGazeEffect(StatusEffectCategory.BENEFICIAL, 0x7b33d7));
  }

  public static void registerModEffects() {
    SPECTRAL_GAZE = registerStatusEffect("spectral_gaze");
    CrawlingMysteries.LOGGER.debug("Registering ModEffects");
  }
}

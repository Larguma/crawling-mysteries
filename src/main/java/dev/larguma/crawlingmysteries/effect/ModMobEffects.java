package dev.larguma.crawlingmysteries.effect;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.effect.custom.DrunkEffect;
import dev.larguma.crawlingmysteries.effect.custom.SpectralGazeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMobEffects {
  public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister
      .create(BuiltInRegistries.MOB_EFFECT, CrawlingMysteries.MOD_ID);

  public static final Holder<MobEffect> SPECTRAL_GAZE = MOB_EFFECTS.register("spectral_gaze", SpectralGazeEffect::new);
  public static final Holder<MobEffect> DRUNK = MOB_EFFECTS.register("drunk", DrunkEffect::new);

  public static void register(IEventBus eventBus) {
    MOB_EFFECTS.register(eventBus);
  }
}

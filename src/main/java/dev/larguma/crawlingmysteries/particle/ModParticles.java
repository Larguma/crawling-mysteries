package dev.larguma.crawlingmysteries.particle;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
  public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
      .create(BuiltInRegistries.PARTICLE_TYPE, CrawlingMysteries.MOD_ID);

  public static final Supplier<SimpleParticleType> SOUL_SUCKLE = PARTICLE_TYPES.register("soul_suckle",
      () -> new SimpleParticleType(true));

  public static void register(IEventBus eventBus) {
    PARTICLE_TYPES.register(eventBus);
  }
}

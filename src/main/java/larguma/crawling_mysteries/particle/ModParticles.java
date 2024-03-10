package larguma.crawling_mysteries.particle;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModParticles implements AutoRegistryContainer<ParticleType<?>> {
  public static final DefaultParticleType ETERNAL_FIRE_PARTICLE = FabricParticleTypes.simple();

  @Override
  public Registry<ParticleType<?>> getRegistry() {
    return Registries.PARTICLE_TYPE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<ParticleType<?>> getTargetFieldType() {
    return (Class<ParticleType<?>>) (Object) ParticleType.class;
  }
}

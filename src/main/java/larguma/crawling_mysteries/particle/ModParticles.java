package larguma.crawling_mysteries.particle;

import larguma.crawling_mysteries.CrawlingMysteries;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
  public static final DefaultParticleType ETERNAL_FIRE_PARTICLE = FabricParticleTypes.simple();

  public static void registerParticles() {
    CrawlingMysteries.LOGGER.debug("Registering ModParticles");
    Registry.register(Registries.PARTICLE_TYPE, new Identifier(CrawlingMysteries.MOD_ID, "eternal_fire_particle"), ETERNAL_FIRE_PARTICLE);
  }
}

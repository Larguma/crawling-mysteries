package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.particle.ModParticles;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class ModParticleDescriptionProvider extends ParticleDescriptionProvider {

  public ModParticleDescriptionProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, existingFileHelper);
  }

  @Override
  protected void addDescriptions() {

    spriteSet(ModParticles.SOUL_SUCKLE.get(),
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "soul_suckle"), 11, false);
    spriteSet(ModParticles.BEER_FLOW.get(), ResourceLocation.withDefaultNamespace("drip_fall"));
  }

}

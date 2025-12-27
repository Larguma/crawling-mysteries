package dev.larguma.crawlingmysteries.worldgen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {

  public static final ResourceKey<BiomeModifier> ADD_MYSTERIOUS_STONE_ORE = registerKey("add_mysterious_stone_ore");

  public static void bootstrap(BootstrapContext<BiomeModifier> context) {
    var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
    var biomes = context.lookup(Registries.BIOME);

    context.register(ADD_MYSTERIOUS_STONE_ORE,
        new BiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.MYSTERIOUS_STONE_ORE_PLACED_KEY)),
            GenerationStep.Decoration.UNDERGROUND_ORES));
  }

  private static ResourceKey<BiomeModifier> registerKey(String name) {
    return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name));
  }

}

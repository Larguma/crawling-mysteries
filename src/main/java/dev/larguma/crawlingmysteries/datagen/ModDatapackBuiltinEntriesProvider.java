package dev.larguma.crawlingmysteries.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.sound.ModJukeboxSongs;
import dev.larguma.crawlingmysteries.worldgen.ModBiomeModifiers;
import dev.larguma.crawlingmysteries.worldgen.ModConfiguredFeatures;
import dev.larguma.crawlingmysteries.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider {
  public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
      .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
      .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
      .add(Registries.JUKEBOX_SONG, ModJukeboxSongs::bootstrap);

  public ModDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(CrawlingMysteries.MOD_ID));
  }
}
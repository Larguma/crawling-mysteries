package dev.larguma.crawlingmysteries.datagen;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class ModDataMapProvider extends DataMapProvider {

  protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
    super(packOutput, lookupProvider);
  }

  @Override
  protected void gather(Provider provider) {
    this.builder(NeoForgeDataMaps.FURNACE_FUELS)
        .add(ModItems.CRYPTIC_EYE.getId(), new FurnaceFuel(12000), false);
    //TODO: make the eye respawn in inv and insult you
  }
}

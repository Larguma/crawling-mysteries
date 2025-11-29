package dev.larguma.crawlingmysteries.datagen;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class DataGenerators {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    DataGenerator generator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    PackOutput packOutput = generator.getPackOutput();
    BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);

    generator.addProvider(event.includeServer(), blockTagsProvider);

    generator.addProvider(event.includeServer(),
        new CuriosItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(),
            existingFileHelper));

    generator.addProvider(event.includeServer(),
        new CuriosProvider(packOutput, existingFileHelper, lookupProvider));

    generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
        List.of(
            new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new,
                LootContextParamSets.BLOCK),
            new LootTableProvider.SubProviderEntry(ModEntityLootTableProvider::new,
                LootContextParamSets.ENTITY)),
        lookupProvider));

    generator.addProvider(event.includeServer(),
        new ModAdvancementProvider(packOutput, lookupProvider, existingFileHelper));

    generator.addProvider(event.includeServer(), new ModDataMapProvider(packOutput, lookupProvider));

    generator.addProvider(event.includeServer(),
        new ModEntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper));

    generator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput, lookupProvider));

    generator.addProvider(event.includeServer(),
        new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(),
            existingFileHelper));

    generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));

    generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

    generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

  }
}

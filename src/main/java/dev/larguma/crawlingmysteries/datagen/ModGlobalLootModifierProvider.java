package dev.larguma.crawlingmysteries.datagen;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.loot.custom.AddItemModifier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

  public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CrawlingMysteries.MOD_ID);
  }

  @Override
  protected void start() {
    add("eternal_guardians_band_from_ancient_city",
        new AddItemModifier(new LootItemCondition[] {
            new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/ancient_city")).build()
        }, ModItems.ETERNAL_GUARDIANS_BAND.get(), 0.1f));
  }
}

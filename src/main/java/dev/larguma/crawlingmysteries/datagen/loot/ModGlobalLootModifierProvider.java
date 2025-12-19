package dev.larguma.crawlingmysteries.datagen.loot;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.loot.custom.AddItemModifier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
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

    addLuckyHorseshoeToChest("lucky_horseshoe_from_dungeon", "chests/simple_dungeon", 0.15f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_mineshaft", "chests/abandoned_mineshaft", 0.12f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_desert_pyramid", "chests/desert_pyramid", 0.18f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_jungle_temple", "chests/jungle_temple", 0.18f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_stronghold_corridor", "chests/stronghold_corridor", 0.12f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_stronghold_crossing", "chests/stronghold_crossing", 0.12f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_stronghold_library", "chests/stronghold_library", 0.15f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_village_weaponsmith", "chests/village/village_weaponsmith", 0.20f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_village_toolsmith", "chests/village/village_toolsmith", 0.20f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_woodland_mansion", "chests/woodland_mansion", 0.10f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_pillager_outpost", "chests/pillager_outpost", 0.15f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_ruined_portal", "chests/ruined_portal", 0.12f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_shipwreck_treasure", "chests/shipwreck_treasure", 0.10f);
    addLuckyHorseshoeToChest("lucky_horseshoe_from_buried_treasure", "chests/buried_treasure", 0.25f);
  }

  private void addLuckyHorseshoeToChest(String modifierName, String lootTablePath, float chance) {
    DataComponentPatch components = DataComponentPatch.builder()
        .set(ModDataComponents.HORSESHOE_TIER.get(), new HorseshoeDataComponent(1)).build();

    add(modifierName,
        new AddItemModifier(
            new LootItemCondition[] {
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace(lootTablePath)).build() },
            ModItems.LUCKY_HORSESHOE.get(), chance, components));
  }
}
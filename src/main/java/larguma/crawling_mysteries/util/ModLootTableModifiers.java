package larguma.crawling_mysteries.util;

import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTableModifiers {

  private static final Identifier ANCIENT_CITY_ID = new Identifier("minecraft", "chests/ancient_city");

  public static void modifyLootTables() {
    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {

      // ETERNAL_GUARDIANS_BAND
      if(ANCIENT_CITY_ID.equals(id)) {
        LootPool.Builder poolBuilder = LootPool.builder()
          .rolls(ConstantLootNumberProvider.create(1))
          .conditionally(RandomChanceLootCondition.builder(0.1f)) // Drops 10% of time
          .with(ItemEntry.builder(ModItems.ETERNAL_GUARDIANS_BAND))
          .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

          tableBuilder.pool(poolBuilder.build());
      }
    });
  }
}

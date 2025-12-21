package dev.larguma.crawlingmysteries.datagen.loot;

import java.util.stream.Stream;

import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntityLootTableProvider extends EntityLootSubProvider {

  public ModEntityLootTableProvider(Provider registries) {
    super(FeatureFlags.REGISTRY.allFlags(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  public void generate() {
    add(ModEntities.ETERNAL_GUARDIAN.get(),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem
            .lootTableItem(ModItems.ETERNAL_GUARDIAN_HEAD).when(LootItemKilledByPlayerCondition.killedByPlayer()))));
  }

  @Override
  protected Stream<EntityType<?>> getKnownEntityTypes() {
    return ModEntities.ENTITY_TYPES.getEntries().stream().map(DeferredHolder::value);
  }

}

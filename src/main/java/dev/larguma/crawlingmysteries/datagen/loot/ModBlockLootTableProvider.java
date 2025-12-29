package dev.larguma.crawlingmysteries.datagen.loot;

import java.util.List;
import java.util.Set;

import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.custom.BeerMugBlock;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

  public ModBlockLootTableProvider(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  protected void generate() {
    add(ModBlocks.BEER_MUG.get(), createBeerMugDrops(ModBlocks.BEER_MUG.get()));
    add(ModBlocks.MYSTERIOUS_STONE.get(),
        block -> createOreDrop(ModBlocks.MYSTERIOUS_STONE.get(), ModItems.PETRIFIED_EYE.get()));
    dropSelf(ModBlocks.BEER_KEG.get());
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
  }

  protected LootTable.Builder createBeerMugDrops(Block block) {
    return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
        .add((LootPoolEntryContainer.Builder<?>) this.applyExplosionDecay(block, LootItem.lootTableItem(block)
            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                .include(DataComponents.BLOCK_ENTITY_DATA))
            .apply(
                List.of(2, 3, 4),
                count -> SetItemCountFunction.setCount(ConstantValue.exactly((float) count.intValue()))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
                        StatePropertiesPredicate.Builder.properties().hasProperty(BeerMugBlock.MUGS,
                            count.intValue())))))));
  }
}

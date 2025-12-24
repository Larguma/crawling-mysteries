package dev.larguma.crawlingmysteries.datagen.loot;

import java.util.function.BiConsumer;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record ModChestLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

  @Override
  public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
    writer.accept(
        ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "chests/tavern_secret")),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 2.0F))
            .add(LootItem.lootTableItem(ModItems.LUCKY_HORSESHOE.get()).setWeight(3))
            .add(LootItem.lootTableItem(ModItems.ETERNAL_GUARDIANS_BAND.get()).setWeight(1))));

    writer.accept(
        ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "chests/tavern_jukebox")),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 5.0F))
            .add(LootItem.lootTableItem(ModItems.MUSIC_DISC_OST_01.get()))));

    writer.accept(
        ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "chests/tavern_bar")),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 10.0F))
            .add(LootItem.lootTableItem(ModBlocks.BEER_MUG.get()))
            .add(LootItem.lootTableItem(Items.BREAD))
            .add(LootItem.lootTableItem(Items.COOKED_BEEF))
            .add(LootItem.lootTableItem(Items.APPLE))));

    writer.accept(
        ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "chests/tavern_storage")),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(3.0F, 10.0F))
            .add(LootItem.lootTableItem(ModItems.ETERNAL_GUARDIAN_HEAD.get()).setWeight(1))
            .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5))
            .add(LootItem.lootTableItem(Items.EMERALD).setWeight(10))));

    writer.accept(
        ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "chests/tavern_room")),
        LootTable.lootTable().withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 5.0F))
            .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
            .add(LootItem.lootTableItem(Items.IRON_SWORD).setWeight(3))
            .add(LootItem.lootTableItem(Items.SHIELD).setWeight(2))
            .add(LootItem.lootTableItem(Items.CROSSBOW).setWeight(1))));
  }
}
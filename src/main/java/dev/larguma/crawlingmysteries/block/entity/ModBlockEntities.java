package dev.larguma.crawlingmysteries.block.entity;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.custom.BeerKegBlockEntity;
import dev.larguma.crawlingmysteries.block.entity.custom.BeerMugBlockEntity;
import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier1BlockEntity;
import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import dev.larguma.crawlingmysteries.block.entity.custom.TombstoneBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    private ModBlockEntities() {
        /* This utility class should not be instantiated */
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CrawlingMysteries.MOD_ID);

    public static final Supplier<BlockEntityType<TombstoneBlockEntity>> TOMBSTONE_BE = BLOCK_ENTITIES
            .register("tombstone_be", () -> BlockEntityType.Builder.of(
                    TombstoneBlockEntity::new, ModBlocks.TOMBSTONE.get()).build(null));
    public static final Supplier<BlockEntityType<BeerMugBlockEntity>> BEER_MUG_BE = BLOCK_ENTITIES
            .register("beer_mug_be", () -> BlockEntityType.Builder.of(
                    BeerMugBlockEntity::new, ModBlocks.BEER_MUG.get()).build(null));
    public static final Supplier<BlockEntityType<BeerKegBlockEntity>> BEER_KEG_BE = BLOCK_ENTITIES
            .register("beer_keg_be", () -> BlockEntityType.Builder.of(
                    BeerKegBlockEntity::new, ModBlocks.BEER_KEG.get()).build(null));
    public static final Supplier<BlockEntityType<CookingAltarTier1BlockEntity>> COOKING_ALTAR_TIER_1_BE = BLOCK_ENTITIES
            .register("cooking_altar_tier_1_be", () -> BlockEntityType.Builder.of(
                    CookingAltarTier1BlockEntity::new, ModBlocks.COOKING_ALTAR_TIER_1.get()).build(null));
    public static final Supplier<BlockEntityType<CookingAltarTier2BlockEntity>> COOKING_ALTAR_TIER_2_BE = BLOCK_ENTITIES
            .register("cooking_altar_tier_2_be", () -> BlockEntityType.Builder.of(
                    CookingAltarTier2BlockEntity::new, ModBlocks.COOKING_ALTAR_TIER_2.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

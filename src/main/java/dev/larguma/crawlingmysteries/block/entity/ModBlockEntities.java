package dev.larguma.crawlingmysteries.block.entity;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
      .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CrawlingMysteries.MODID);

  public static final Supplier<BlockEntityType<TombstoneBlockEntity>> TOMBSTONE_BE = BLOCK_ENTITIES
      .register("tombstone_be", () -> BlockEntityType.Builder.of(
          TombstoneBlockEntity::new, ModBlocks.TOMBSTONE.get()).build(null));

  public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
  }
}

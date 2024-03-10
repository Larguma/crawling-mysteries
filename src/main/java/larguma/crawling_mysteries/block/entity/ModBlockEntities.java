package larguma.crawling_mysteries.block.entity;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import larguma.crawling_mysteries.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities implements AutoRegistryContainer<BlockEntityType<?>> {
  public static final BlockEntityType<TombstoneBlockEntity> TOMBSTONE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
      .create(TombstoneBlockEntity::new, ModBlocks.TOMBSTONE).build();

  @Override
  public Registry<BlockEntityType<?>> getRegistry() {
    return Registries.BLOCK_ENTITY_TYPE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<BlockEntityType<?>> getTargetFieldType() {
    return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
  }
}

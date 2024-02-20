package larguma.crawling_mysteries.block.entity;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
  public static final BlockEntityType<TombstoneBlockEntity> TOMBSTONE_BLOCK_ENTITY = Registry.register(
      Registries.BLOCK_ENTITY_TYPE, new Identifier(CrawlingMysteries.MOD_ID, "tombstone_be"),
      FabricBlockEntityTypeBuilder.create(TombstoneBlockEntity::new, ModBlocks.TOMBSTONE).build());

  public static void registerBlockEntities() {
    CrawlingMysteries.LOGGER.info("Registring Block Entities");
  }
}

package larguma.crawling_mysteries.block;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.custom.TombstoneBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
  public static final Block TOMBSTONE = registerBlock("tombstone",
      new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE).nonOpaque(), ParticleTypes.SOUL));

  private static Block registerBlock(String name, Block block) {
    registerBlockItem(name, block);
    return Registry.register(Registries.BLOCK, new Identifier(CrawlingMysteries.MOD_ID, name), block);
  }

  private static Item registerBlockItem(String name, Block block) {
    return Registry.register(Registries.ITEM, new Identifier(CrawlingMysteries.MOD_ID, name),
        new BlockItem(block, new FabricItemSettings()));
  }

  public static void registerModBlocks() {
    CrawlingMysteries.LOGGER.debug("Registering ModBlocks");
  }
}

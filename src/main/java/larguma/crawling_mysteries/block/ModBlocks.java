package larguma.crawling_mysteries.block;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.custom.TombstoneBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.particle.ParticleTypes;

public class ModBlocks implements BlockRegistryContainer {
  public static final Block TOMBSTONE = new TombstoneBlock(
      FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE).strength(-1.0f, 3600000.0f).nonOpaque(),
      ParticleTypes.SOUL_FIRE_FLAME);

  @Override
  public BlockItem createBlockItem(Block block, String identifier) {
    return new BlockItem(block, new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(1));
  }
}

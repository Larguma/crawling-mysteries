package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

  public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CrawlingMysteries.MOD_ID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    blockWithItem(ModBlocks.MYSTERIOUS_STONE);
  }

  private void blockWithItem(DeferredBlock<?> deferredBlock) {
    simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
  }

}

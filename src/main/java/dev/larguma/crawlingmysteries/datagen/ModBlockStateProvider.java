package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

  public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CrawlingMysteries.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
  }

  private void blockWithItem(DeferredBlock<?> block) {
    simpleBlockWithItem(block.get(), cubeAll(block.get()));
  }

}

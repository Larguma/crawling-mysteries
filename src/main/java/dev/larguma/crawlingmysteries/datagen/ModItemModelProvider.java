package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

  public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CrawlingMysteries.MODID, exFileHelper);
  }

  @Override
  protected void registerModels() {
  }

}

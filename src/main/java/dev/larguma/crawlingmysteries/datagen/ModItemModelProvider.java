package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

  public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CrawlingMysteries.MOD_ID, exFileHelper);
  }

  @Override
  protected void registerModels() {
    withExistingParent(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
  }

}

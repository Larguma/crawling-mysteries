package dev.larguma.crawlingmysteries.datagen;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosProvider extends CuriosDataProvider {

  public CuriosProvider(String modId, PackOutput output, ExistingFileHelper fileHelper,
      CompletableFuture<Provider> registries) {
    super(modId, output, fileHelper, registries);
  }

  @Override
  public void generate(Provider registries, ExistingFileHelper fileHelper) {
    this.createSlot("observer").size(1).dropRule(ICurio.DropRule.ALWAYS_KEEP).icon(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MODID, "slot/observer"));


    this.createEntities("entities").addPlayer().addSlots("observer");
  }

}

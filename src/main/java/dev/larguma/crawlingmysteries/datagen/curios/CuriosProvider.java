package dev.larguma.crawlingmysteries.datagen.curios;

import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

public class CuriosProvider extends CuriosDataProvider {

  public CuriosProvider(PackOutput output, ExistingFileHelper fileHelper,
      CompletableFuture<Provider> registries) {
    super(CrawlingMysteries.MOD_ID, output, fileHelper, registries);
  }

  @Override
  public void generate(Provider registries, ExistingFileHelper fileHelper) {
    this.createSlot("observer").size(1).dropRule(DropRule.ALWAYS_KEEP)
        .icon(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "slot/observer"));
    this.createSlot("mask").size(1)
        .icon(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "slot/mask"));
    this.createSlot("feet").size(1)
        .icon(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "slot/feet"));

    this.createEntities("entities").addPlayer()
        .addSlots("observer", "mask", "ring", "feet");
  }

}

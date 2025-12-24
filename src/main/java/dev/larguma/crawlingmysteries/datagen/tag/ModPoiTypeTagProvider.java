package dev.larguma.crawlingmysteries.datagen.tag;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.villager.ModVillager;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModPoiTypeTagProvider extends PoiTypeTagsProvider {

  public ModPoiTypeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, CrawlingMysteries.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(PoiTypeTags.ACQUIRABLE_JOB_SITE)
        .addOptional(ResourceLocation.parse(ModVillager.BEER_KEG_POI.getRegisteredName()));
  }

}

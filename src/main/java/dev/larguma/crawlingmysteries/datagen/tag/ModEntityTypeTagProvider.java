package dev.larguma.crawlingmysteries.datagen.tag;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModEntityTypeTagProvider extends EntityTypeTagsProvider {

  public ModEntityTypeTagProvider(PackOutput output, CompletableFuture<Provider> provider,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(output, provider, CrawlingMysteries.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
        .add(ModEntities.ETERNAL_GUARDIAN.get());
  }

}

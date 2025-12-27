package dev.larguma.crawlingmysteries.datagen.tag;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {

  public ModBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, CrawlingMysteries.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .add(ModBlocks.MYSTERIOUS_STONE.get());

    tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .add(ModBlocks.MYSTERIOUS_STONE.get());
  }

}

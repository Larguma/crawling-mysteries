package dev.larguma.crawlingmysteries.datagen.tag;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemTagProvider extends ItemTagsProvider {

  public ModItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTags, CrawlingMysteries.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
        .add(ModItems.MUSIC_DISC_OST_01.get());

    tag(Tags.Items.MUSIC_DISCS)
        .add(ModItems.MUSIC_DISC_OST_01.get());
  }

}

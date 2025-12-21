package dev.larguma.crawlingmysteries.datagen.curios;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosTags;

public class CuriosItemTagProvider extends ItemTagsProvider {

  public CuriosItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTags, "curios", existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    tag(CuriosTags.createItemTag("observer"))
        .add(ModItems.CRYPTIC_EYE.get());
    tag(CuriosTags.createItemTag("mask"))
        .add(ModItems.ETERNAL_GUARDIAN_MASK.get());
    tag(CuriosTags.createItemTag("feet"))
        .add(ModItems.LUCKY_HORSESHOE.get());

    tag(CuriosTags.RING)
        .add(ModItems.ETERNAL_GUARDIANS_BAND.get());
  }

}

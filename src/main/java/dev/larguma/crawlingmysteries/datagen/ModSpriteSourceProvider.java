package dev.larguma.crawlingmysteries.datagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

public class ModSpriteSourceProvider extends SpriteSourceProvider {

  public ModSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
      ExistingFileHelper fileHelper) {
    super(output, lookupProvider, CrawlingMysteries.MOD_ID, fileHelper);
  }

  @Override
  protected void gather() {
    String[] gui = { "spell_slot", "spell_slot_selected" };
    for (String guiId : gui) {
      atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(
          ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "gui/" + guiId),
          Optional.empty()));
    }

    String[] spellIcons = { "spectral_gaze", "feed_totem", "be_totem" };
    for (String iconId : spellIcons) {
      atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(
          ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "spell/" + iconId),
          Optional.empty()));
    }
  }
}

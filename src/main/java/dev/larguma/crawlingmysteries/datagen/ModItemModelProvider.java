package dev.larguma.crawlingmysteries.datagen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

  public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CrawlingMysteries.MOD_ID, exFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(ModItems.MUSIC_DISC_OST_01.get());


    this.withExistingParent(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG.getId().toString(), mcLoc("item/template_spawn_egg"));
    this.withExistingParent(ModBlocks.TOMBSTONE.getId().toString(), modLoc("block/tombstone"));

    this.withExistingParent(ModItems.CRYPTIC_EYE.getId().toString() + "_2d", mcLoc("item/generated"))
        .texture("layer0", "item/cryptic_eye_2d");
    this.getBuilder(ModItems.CRYPTIC_EYE.getId().toString())
        .parent(getExistingFile(mcLoc("item/handheld")))
        .customLoader(SeparateTransformsModelBuilder::begin)
        .base(nested().parent(getExistingFile(modLoc("item/cryptic_eye_3d"))))
        .perspective(ItemDisplayContext.GUI,
            nested().parent(getExistingFile(modLoc("item/cryptic_eye_2d"))))
        .perspective(ItemDisplayContext.FIXED,
            nested().parent(getExistingFile(modLoc("item/cryptic_eye_2d"))))
        .end();

    this.withExistingParent(ModItems.ETERNAL_GUARDIANS_BAND.getId().toString() + "_2d", mcLoc("item/generated"))
        .texture("layer0", "item/eternal_guardians_band_2d");
    this.getBuilder(ModItems.ETERNAL_GUARDIANS_BAND.getId().toString())
        .parent(getExistingFile(mcLoc("item/handheld")))
        .customLoader(SeparateTransformsModelBuilder::begin)
        .base(nested().parent(getExistingFile(modLoc("item/eternal_guardians_band_3d"))))
        .perspective(ItemDisplayContext.GUI,
            nested().parent(getExistingFile(modLoc("item/eternal_guardians_band_2d"))))
        .perspective(ItemDisplayContext.FIXED,
            nested().parent(getExistingFile(modLoc("item/eternal_guardians_band_2d"))))
        .end();

    this.withExistingParent(ModItems.ETERNAL_GUARDIAN_HEAD.getId().toString() + "_2d", mcLoc("item/generated"))
        .texture("layer0", "item/eternal_guardian_head_2d");
    this.getBuilder(ModItems.ETERNAL_GUARDIAN_HEAD.getId().toString())
        .parent(getExistingFile(mcLoc("item/handheld")))
        .customLoader(SeparateTransformsModelBuilder::begin)
        .base(nested().parent(getExistingFile(modLoc("item/eternal_guardian_head_3d"))))
        .perspective(ItemDisplayContext.GUI,
            nested().parent(getExistingFile(modLoc("item/eternal_guardian_head_2d"))))
        .perspective(ItemDisplayContext.FIXED,
            nested().parent(getExistingFile(modLoc("item/eternal_guardian_head_2d"))))
        .end();

    this.withExistingParent(ModItems.ETERNAL_GUARDIAN_MASK.getId().toString() + "_2d", mcLoc("item/generated"))
        .texture("layer0", "item/eternal_guardian_mask_2d");
    this.getBuilder(ModItems.ETERNAL_GUARDIAN_MASK.getId().toString())
        .parent(getExistingFile(mcLoc("item/handheld")))
        .customLoader(SeparateTransformsModelBuilder::begin)
        .base(nested().parent(getExistingFile(modLoc("item/eternal_guardian_mask_3d"))))
        .perspective(ItemDisplayContext.GUI,
            nested().parent(getExistingFile(modLoc("item/eternal_guardian_mask_2d"))))
        .perspective(ItemDisplayContext.FIXED,
            nested().parent(getExistingFile(modLoc("item/eternal_guardian_mask_2d"))))
        .end();

    this.withExistingParent(ModItems.LUCKY_HORSESHOE.getId().toString() + "_tier_1", mcLoc("item/generated"))
        .texture("layer0", "item/lucky_horseshoe_tier_1");
    this.withExistingParent(ModItems.LUCKY_HORSESHOE.getId().toString() + "_tier_2", mcLoc("item/generated"))
        .texture("layer0", "item/lucky_horseshoe_tier_2");
    this.withExistingParent(ModItems.LUCKY_HORSESHOE.getId().toString() + "_tier_3", mcLoc("item/generated"))
        .texture("layer0", "item/lucky_horseshoe_tier_3");
    this.withExistingParent(ModItems.LUCKY_HORSESHOE.getId().toString() + "_tier_4", mcLoc("item/generated"))
        .texture("layer0", "item/lucky_horseshoe_tier_4");
    luckyHorseshoeWithTiers();
  }

  private void luckyHorseshoeWithTiers() {
    // Create base model with Tier 1 texture
    ItemModelBuilder builder = withExistingParent("lucky_horseshoe", mcLoc("item/generated"))
        .texture("layer0", modLoc("item/lucky_horseshoe_tier_1"));

    // Add overrides for each tier
    builder.override()
        .predicate(modLoc("tier"), 2)
        .model(getBuilder("lucky_horseshoe_tier_2")
            .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
            .texture("layer0", modLoc("item/lucky_horseshoe_tier_2")))
        .end();

    builder.override()
        .predicate(modLoc("tier"), 3)
        .model(getBuilder("lucky_horseshoe_tier_3")
            .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
            .texture("layer0", modLoc("item/lucky_horseshoe_tier_3")))
        .end();

    builder.override()
        .predicate(modLoc("tier"), 4)
        .model(getBuilder("lucky_horseshoe_tier_4")
            .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
            .texture("layer0", modLoc("item/lucky_horseshoe_tier_4")))
        .end();
  }
}

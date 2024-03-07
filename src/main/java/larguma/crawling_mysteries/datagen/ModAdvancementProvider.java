package larguma.crawling_mysteries.datagen;

import java.util.function.Consumer;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModAdvancementProvider extends FabricAdvancementProvider {

  public ModAdvancementProvider(FabricDataOutput output) {
    super(output);
  }

  @SuppressWarnings("unused")
  @Override
  public void generateAdvancement(Consumer<Advancement> consumer) {
    Advancement rootAdvancement = Advancement.Builder.create()
        .display(
            ModItems.CRYPTIC_EYE, // The display icon
            Text.translatable("general.crawling-mysteries.mod_name"), // The title
            Text.translatable("advancement.crawling-mysteries.desc"), // The description
            new Identifier("textures/block/end_stone_bricks.png"), // Background image used
            AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
            true, // Show toast top right
            false, // Announce to chat
            false // Hidden in the advancement tab
        )
        // The first string used in criterion is the name referenced by other
        // advancements when they want to have 'requirements'
        .criterion("got_cryptic_eye", InventoryChangedCriterion.Conditions.items(ModItems.CRYPTIC_EYE))
        .build(consumer, CrawlingMysteries.MOD_ID + "/root");

    Advancement gotEternalGuardiansBand = Advancement.Builder.create().parent(rootAdvancement)
        .display(
            ModItems.ETERNAL_GUARDIANS_BAND,
            Text.translatable("advancement.crawling-mysteries.eternal_guardians_band"),
            Text.translatable("advancement.crawling-mysteries.eternal_guardians_band.desc"), 
            null, // children to parent advancements don't need a background set
            AdvancementFrame.TASK,
            true,
            true,
            false)
        .rewards(AdvancementRewards.Builder.experience(1000))
        .criterion("got_eternal_guardians_band",
            InventoryChangedCriterion.Conditions.items(ModItems.ETERNAL_GUARDIANS_BAND))
        .build(consumer, CrawlingMysteries.MOD_ID + "/got_eternal_guardians_band");

    Advancement gotEternalGuardianMask = Advancement.Builder.create().parent(gotEternalGuardiansBand)
        .display(
            ModItems.ETERNAL_GUARDIAN_MASK,
            Text.translatable("advancement.crawling-mysteries.eternal_guardian_mask"),
            Text.translatable("advancement.crawling-mysteries.eternal_guardian_mask.desc"),
            null, 
            AdvancementFrame.TASK,
            true,
            true,
            true)
        .rewards(AdvancementRewards.Builder.experience(1000))
        .criterion("got_eternal_guardian_mask",
            InventoryChangedCriterion.Conditions.items(ModItems.ETERNAL_GUARDIAN_MASK))
        .build(consumer, CrawlingMysteries.MOD_ID + "/got_eternal_guardian_mask");
  }

}

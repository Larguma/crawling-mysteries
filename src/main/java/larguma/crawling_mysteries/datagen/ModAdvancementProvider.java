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
import net.minecraft.item.Items;
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
            ModItems.MYSTERIOUS_AMULET, // The display icon
            Text.translatable("general.crawling_mysteries.mod_name"), // The title
            Text.translatable("advancement.crawling_mysteries.desc"), // The description
            new Identifier("textures/block/end_stone_bricks.png"), // Background image used
            AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
            false, // Show toast top right
            false, // Announce to chat
            false // Hidden in the advancement tab
        )
        // The first string used in criterion is the name referenced by other
        // advancements when they want to have 'requirements'
        .criterion("got_mysterious_amulet", InventoryChangedCriterion.Conditions.items(ModItems.MYSTERIOUS_AMULET))
        .build(consumer, CrawlingMysteries.MOD_ID + "/root");

    Advancement gotOakAdvancement = Advancement.Builder.create().parent(rootAdvancement)
        .display(
            Items.OAK_LOG,
            Text.literal("Your First Log"),
            Text.literal("Bare fisted"),
            null, // children to parent advancements don't need a background set
            AdvancementFrame.TASK,
            true,
            true,
            false)
        .rewards(AdvancementRewards.Builder.experience(1000))
        .criterion("got_wood", InventoryChangedCriterion.Conditions.items(Items.OAK_LOG))
        .build(consumer, CrawlingMysteries.MOD_ID + "/got_wood");
  }

}

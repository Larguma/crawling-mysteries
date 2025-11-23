package dev.larguma.crawlingmysteries.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModAdvancementProvider extends AdvancementProvider {

  public ModAdvancementProvider(PackOutput output, CompletableFuture<Provider> registries,
      ExistingFileHelper existingFileHelper) {
    super(output, registries, existingFileHelper, List.of(new ModAdvancementGenerator()));
  }

  private static final class ModAdvancementGenerator implements AdvancementGenerator {

    @SuppressWarnings("unused")
    @Override
    public void generate(Provider registries, Consumer<AdvancementHolder> saver,
        ExistingFileHelper existingFileHelper) {

      AdvancementHolder root = Advancement.Builder.advancement()
          .display(
              new ItemStack(ModItems.CRYPTIC_EYE.get()),
              Component.translatable("general.crawlingmysteries.mod_name"),
              Component.translatable("advancement.crawlingmysteries.desc"),
              ResourceLocation.withDefaultNamespace("textures/block/end_stone_bricks.png"),
              // The frame type. Valid values are AdvancementType.TASK, CHALLENGE, or GOAL.
              AdvancementType.GOAL,
              // Whether to show the advancement toast or not.
              true,
              // Whether to announce the advancement into chat or not.
              true,
              // Whether the advancement should be hidden or not.
              false)
          .rewards(AdvancementRewards.Builder.experience(100))
          .addCriterion("got_cryptic_eye", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CRYPTIC_EYE))
          .requirements(AdvancementRequirements.allOf(List.of("got_cryptic_eye")))
          .save(saver, ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "root"), existingFileHelper);

      AdvancementHolder gotEternalGuardiansBand = Advancement.Builder.advancement()
          .display(
              ModItems.ETERNAL_GUARDIANS_BAND,
              Component.translatable("advancement.crawlingmysteries.eternal_guardians_band"),
              Component.translatable("advancement.crawlingmysteries.eternal_guardians_band.desc"),
              null, // children to parent advancements don't need a background set
              AdvancementType.TASK,
              true,
              true,
              false)
          .parent(root)
          .rewards(AdvancementRewards.Builder.experience(1000))
          .addCriterion("got_eternal_guardians_band",
              InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ETERNAL_GUARDIANS_BAND))
          .requirements(AdvancementRequirements.allOf(List.of("got_eternal_guardians_band")))
          .save(saver, ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "got_eternal_guardians_band"),
              existingFileHelper);
    }

  }

}

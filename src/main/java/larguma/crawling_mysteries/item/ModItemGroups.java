package larguma.crawling_mysteries.item;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

  public static final ItemGroup CRAWLING_MYSTERIES_GROUP = Registry.register(Registries.ITEM_GROUP,
      new Identifier(CrawlingMysteries.MOD_ID, "crawling_mysteries_group"),
      FabricItemGroup.builder().displayName(Text.translatable("general.crawling-mysteries.mod_name"))
          .icon(() -> new ItemStack(ModItems.CRYPTIC_EYE)).entries((displayContext, entries) -> {
            // Add items to the group
            entries.add(ModItems.CRYPTIC_EYE);
            entries.add(ModItems.ETERNAL_GUARDIANS_BAND);

            entries.add(ModBlocks.TOMBSTONE);

            entries.add(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG);

          }).build());

  public static void registerItemGroups() {
    CrawlingMysteries.LOGGER.debug("Registering item groups");
  }
}

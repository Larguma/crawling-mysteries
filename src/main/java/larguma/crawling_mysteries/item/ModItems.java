package larguma.crawling_mysteries.item;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.item.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

  public static final Item CRYPTIC_EYE = registerItem("cryptic_eye",
      new CrypticEyeItem(new FabricItemSettings().maxCount(1)));
  public static final Item ETERNAL_GUARDIANS_BAND = registerItem("eternal_guardians_band",
      new EternalGuardiansBandItem(new FabricItemSettings().maxCount(1)));
  public static final Item ETERNAL_GUARDIAN_HEAD = registerItem("eternal_guardian_head",
      new EternalGuardianHeadItem(new FabricItemSettings().maxCount(1)));
  public static final Item ETERNAL_GUARDIAN_MASK = registerItem("eternal_guardian_mask",
      new EternalGuardianMaskItem(new FabricItemSettings().maxCount(1)));

  public static final Item ETERNAL_GUARDIAN_SPAWN_EGG = registerItem("eternal_guardian_spawn_egg",
      new SpawnEggItem(ModEntities.ETERNAL_GUARDIAN, 0x7b33d7, 0x7d90fd, new FabricItemSettings()));

  private static Item registerItem(String name, Item item) {
    return Registry.register(Registries.ITEM, new Identifier(CrawlingMysteries.MOD_ID, name), item);
  }

  public static void registerModItems() {
    CrawlingMysteries.LOGGER.debug("Registering mod items");
  }
}

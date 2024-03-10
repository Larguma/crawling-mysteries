package larguma.crawling_mysteries.item;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.item.custom.*;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;

public class ModItems implements ItemRegistryContainer {
  public static final Item CRYPTIC_EYE = new CrypticEyeItem();
  public static final Item ETERNAL_GUARDIANS_BAND = new EternalGuardiansBandItem();
  public static final Item ETERNAL_GUARDIAN_HEAD = new EternalGuardianHeadItem();
  public static final Item ETERNAL_GUARDIAN_MASK = new EternalGuardianMaskItem();

  public static final Item ETERNAL_GUARDIAN_SPAWN_EGG = new SpawnEggItem(ModEntities.ETERNAL_GUARDIAN, 0x7b33d7,
      0x7d90fd, new OwoItemSettings().group(CrawlingMysteries.CRAWLING_MYSTERIES_GROUP).tab(1));
}

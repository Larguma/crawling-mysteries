package larguma.crawling_mysteries.item;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.custom.MysteriousAmuletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

  public static final Item MYSTERIOUS_AMULET = registerItem("mysterious_amulet", 
    new MysteriousAmuletItem(new FabricItemSettings().maxCount(1)));

  private static Item registerItem(String name, Item item) {
    return Registry.register(Registries.ITEM, new Identifier(CrawlingMysteries.MOD_ID, name), item);
  }

  public static void registerModItems() {
    CrawlingMysteries.LOGGER.info("Registering mod items");
  }
}

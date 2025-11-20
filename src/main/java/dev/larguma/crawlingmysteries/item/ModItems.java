package dev.larguma.crawlingmysteries.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrawlingMysteries.MODID);

  public static final DeferredItem<Item> CRYPTIC_EYE = ITEMS.register("cryptic_eye", () -> new CrypticEyeItem());

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}

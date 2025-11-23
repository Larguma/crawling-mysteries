package dev.larguma.crawlingmysteries.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardiansBandItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrawlingMysteries.MOD_ID);

  public static final DeferredItem<Item> CRYPTIC_EYE = ITEMS.register("cryptic_eye", () -> new CrypticEyeItem());
  public static final DeferredItem<Item> ETERNAL_GUARDIANS_BAND = ITEMS.register("eternal_guardians_band", () -> new EternalGuardiansBandItem());

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}

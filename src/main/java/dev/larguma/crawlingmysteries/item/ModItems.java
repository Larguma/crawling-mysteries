package dev.larguma.crawlingmysteries.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianHeadItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardiansBandItem;
import dev.larguma.crawlingmysteries.item.custom.LuckyHorseshoe;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrawlingMysteries.MOD_ID);

  // #region Custom
  public static final DeferredItem<Item> CRYPTIC_EYE = ITEMS.register("cryptic_eye", CrypticEyeItem::new);
  public static final DeferredItem<Item> ETERNAL_GUARDIANS_BAND = ITEMS.register("eternal_guardians_band",
      EternalGuardiansBandItem::new);
  public static final DeferredItem<Item> ETERNAL_GUARDIAN_HEAD = ITEMS.register("eternal_guardian_head",
      EternalGuardianHeadItem::new);
  public static final DeferredItem<Item> ETERNAL_GUARDIAN_MASK = ITEMS.register("eternal_guardian_mask",
      EternalGuardianMaskItem::new);
  public static final DeferredItem<Item> LUCKY_HORSESHOE = ITEMS.register("lucky_horseshoe",
      LuckyHorseshoe::new);
  // #endregion Custom

  // #region Egg
  public static final DeferredItem<Item> ETERNAL_GUARDIAN_SPAWN_EGG = ITEMS.register(
      "eternal_guardian_spawn_egg",
      () -> new DeferredSpawnEggItem(ModEntities.ETERNAL_GUARDIAN, 0x7b33d7, 0x7d90fd, new Item.Properties()));
  // #endregion Egg

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}

package dev.larguma.crawlingmysteries.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianHeadItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardiansBandItem;
import dev.larguma.crawlingmysteries.item.custom.LuckyHorseshoe;
import dev.larguma.crawlingmysteries.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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
  public static final DeferredItem<Item> LUCKY_HORSESHOE = ITEMS.register("lucky_horseshoe", LuckyHorseshoe::new);

  // #endregion Custom

  // #region Simple

  public static final DeferredItem<Item> BEER_BARREL = ITEMS.register("beer_barrel",
      () -> new Item(new Item.Properties().stacksTo(16)));

  // #endregion Simple

  // #region Egg

  public static final DeferredItem<Item> ETERNAL_GUARDIAN_SPAWN_EGG = ITEMS.register("eternal_guardian_spawn_egg",
      () -> new DeferredSpawnEggItem(ModEntities.ETERNAL_GUARDIAN, 0x7b33d7, 0x7d90fd, new Item.Properties()));

  // #endregion Egg

  // #region Music Discs

  public static final DeferredItem<Item> MUSIC_DISC_OST_01 = ITEMS.register("music_disc_ost_01",
      () -> new Item(new Item.Properties().jukeboxPlayable(ModSounds.OST_01_KEY).stacksTo(1).rarity(Rarity.RARE)));
  public static final DeferredItem<Item> MUSIC_DISC_OST_02 = ITEMS.register("music_disc_ost_02",
      () -> new Item(new Item.Properties().jukeboxPlayable(ModSounds.OST_02_KEY).stacksTo(1).rarity(Rarity.RARE)));

  // #endregion Music Discs

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}

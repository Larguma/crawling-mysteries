package dev.larguma.crawlingmysteries.item;

import java.util.function.Supplier;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
      .create(Registries.CREATIVE_MODE_TAB, CrawlingMysteries.MOD_ID);

  public static final Supplier<CreativeModeTab> TAB_ALL = CREATIVE_MODE_TABS.register("tab_all",
      () -> CreativeModeTab.builder()
          .icon(() -> ModItems.CRYPTIC_EYE.get().getDefaultInstance())
          .title(Component.translatable("general.crawlingmysteries.mod_name"))
          .displayItems((itemDisplayParameters, output) -> {
            output.accept(ModItems.CRYPTIC_EYE.get());
            output.accept(ModItems.ETERNAL_GUARDIANS_BAND.get());
            output.accept(ModItems.ETERNAL_GUARDIAN_HEAD.get());
            output.accept(ModItems.ETERNAL_GUARDIAN_MASK.get());
            output.accept(ModItems.LUCKY_HORSESHOE.get());
            output.accept(ModItems.PETRIFIED_EYE.get());
            output.accept(ModItems.AWAKENED_EYE.get());

            output.accept(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG.get());

            output.accept(ModItems.MUSIC_DISC_OST_01.get());
            output.accept(ModItems.MUSIC_DISC_OST_02.get());

            output.accept(ModBlocks.TOMBSTONE.get());
            output.accept(ModBlocks.BEER_MUG.get());
            output.accept(ModItems.BEER_BARREL.get());
            output.accept(ModBlocks.BEER_KEG.get());
            output.accept(ModBlocks.MYSTERIOUS_STONE.get());
          })
          .build());

  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}

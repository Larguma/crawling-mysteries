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
      .create(Registries.CREATIVE_MODE_TAB, CrawlingMysteries.MODID);

  public static final Supplier<CreativeModeTab> TAB_ALL = CREATIVE_MODE_TABS.register("tab_all",
      () -> CreativeModeTab.builder()
          .icon(() -> ModItems.CRYPTIC_EYE.get().getDefaultInstance())
          .title(Component.translatable("general.crawlingmysteries.mod_name"))
          .displayItems((itemDisplayParameters, output) -> {
            output.accept(ModItems.CRYPTIC_EYE.get());
            output.accept(ModBlocks.TOMBSTONE.get());
          })
          .build());

  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}

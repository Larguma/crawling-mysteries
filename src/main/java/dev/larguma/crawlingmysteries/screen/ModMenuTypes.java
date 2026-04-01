package dev.larguma.crawlingmysteries.screen;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.screen.custom.AlchemicalDistilleryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
  public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,
      CrawlingMysteries.MOD_ID);

  public static final DeferredHolder<MenuType<?>, MenuType<AlchemicalDistilleryMenu>> ALCHEMICAL_DISTILLERY_MENU = MENUS
      .register("alchemical_distillery_menu", () -> IMenuTypeExtension.create(AlchemicalDistilleryMenu::new));

  public static void register(IEventBus eventBus) {
    MENUS.register(eventBus);
  }
}

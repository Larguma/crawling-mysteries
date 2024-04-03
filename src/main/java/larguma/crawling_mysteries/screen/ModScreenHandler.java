package larguma.crawling_mysteries.screen;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import larguma.crawling_mysteries.screen.custom.SpellSelectMenuScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandler implements AutoRegistryContainer<ScreenHandlerType<?>> {

  public static final ScreenHandlerType<SpellSelectMenuScreenHandler> SPELL_SELECT_MENU_HANDLER_TYPE = new ScreenHandlerType<>(
      SpellSelectMenuScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

  @Override
  public Registry<ScreenHandlerType<?>> getRegistry() {
    return Registries.SCREEN_HANDLER;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<ScreenHandlerType<?>> getTargetFieldType() {
    return (Class<ScreenHandlerType<?>>) (Object) ScreenHandlerType.class;
  }
}

package larguma.crawling_mysteries.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.GridLayout;
import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.util.Identifier;

public class SpellSelectMenuScreen extends BaseUIModelScreen<GridLayout> {

  public SpellSelectMenuScreen() {
    super(GridLayout.class, DataSource.asset(new Identifier(CrawlingMysteries.MOD_ID, "spell_menu_screen")));
  }

  @Override
  protected void build(GridLayout rootComponent) {
    rootComponent.childById(ButtonComponent.class, "favorite-spell-one").onPress(button -> {
      CrawlingMysteries.LOGGER.info("Button favorite spell one pressed!");
    });
    rootComponent.childById(ButtonComponent.class, "favorite-spell-two").onPress(button -> {
      CrawlingMysteries.LOGGER.info("Button favorite spell two pressed!");
    });
    rootComponent.childById(ButtonComponent.class, "favorite-spell-three").onPress(button -> {
      CrawlingMysteries.LOGGER.info("Button favorite spell three pressed!");
    });
    rootComponent.childById(ButtonComponent.class, "favorite-spell-four").onPress(button -> {
      CrawlingMysteries.LOGGER.info("Button favorite spell four pressed!");
    });
  }
}

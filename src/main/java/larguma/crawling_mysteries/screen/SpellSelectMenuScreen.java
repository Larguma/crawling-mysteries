package larguma.crawling_mysteries.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import larguma.crawling_mysteries.CrawlingMysteries;
import net.minecraft.util.Identifier;

public class SpellSelectMenuScreen extends BaseUIModelScreen<FlowLayout> {

  public SpellSelectMenuScreen() {
    super(FlowLayout.class, DataSource.asset(new Identifier(CrawlingMysteries.MOD_ID, "spell_menu_screen")));
  }

  @Override
  protected void build(FlowLayout rootComponent) {
  }
}

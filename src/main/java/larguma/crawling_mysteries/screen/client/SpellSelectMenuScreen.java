package larguma.crawling_mysteries.screen.client;

import io.wispforest.owo.ui.base.BaseUIModelHandledScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen.DataSource;
import io.wispforest.owo.ui.container.FlowLayout;
import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.screen.custom.SpellSelectMenuScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SpellSelectMenuScreen extends BaseUIModelHandledScreen<FlowLayout, SpellSelectMenuScreenHandler> {

  public SpellSelectMenuScreen(SpellSelectMenuScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title, FlowLayout.class, DataSource.asset(new Identifier(CrawlingMysteries.MOD_ID, "spell_menu_screen")));
  }

  @Override
  protected void build(FlowLayout layout) {
    this.titleY = 1000;
    this.playerInventoryTitleY = 1000;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
  }
}
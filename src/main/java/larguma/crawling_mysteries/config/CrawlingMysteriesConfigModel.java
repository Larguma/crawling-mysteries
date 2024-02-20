package larguma.crawling_mysteries.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import larguma.crawling_mysteries.CrawlingMysteries;

@Modmenu(modId = CrawlingMysteries.MOD_ID)
@Config(name = CrawlingMysteries.MOD_ID, wrapperName = "CrawlingMysteriesConfig")
public class CrawlingMysteriesConfigModel {

  // https://docs.wispforest.io/owo/config/getting-started/
  
  @SectionHeader("someSection")
  public String someLaterOption = "42";
  public int anIntOption = 16;
  public boolean aBooleanToggle = false;

  public Choices anEnumOption = Choices.ANOTHER_CHOICE;

  public enum Choices {
    A_CHOICE, ANOTHER_CHOICE;
  }
}

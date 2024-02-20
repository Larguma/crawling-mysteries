package larguma.crawling_mysteries.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import larguma.crawling_mysteries.CrawlingMysteries;

@Modmenu(modId = CrawlingMysteries.MOD_ID)
@Config(name = CrawlingMysteries.MOD_ID, wrapperName = "CrawlingMysteriesConfig")
public class CrawlingMysteriesConfigModel {

  // https://docs.wispforest.io/owo/config/getting-started/
  
  @SectionHeader("trinkets")
  public boolean enableCrypticEyeRender = true;
  public boolean enableTombstone = true;

}
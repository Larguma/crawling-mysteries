package larguma.crawling_mysteries.config;

import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.config.Option;
import larguma.crawling_mysteries.CrawlingMysteries;

@Modmenu(modId = CrawlingMysteries.MOD_ID)
@Config(name = CrawlingMysteries.MOD_ID, wrapperName = "CrawlingMysteriesConfig")
public class CrawlingMysteriesConfigModel {

  // https://docs.wispforest.io/owo/config/getting-started/
  
  @SectionHeader("trinkets")
  public boolean enableCrypticEyeRender = true;

  @Sync(Option.SyncMode.OVERRIDE_CLIENT)
  public boolean enableTombstone = true;

}
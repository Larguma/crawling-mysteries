package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.item.custom.CrypticEyeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrypticEyeItemRenderer extends GeoItemRenderer<CrypticEyeItem> {

  public CrypticEyeItemRenderer() {
    super(new CrypticEyeItemModel());
  }
}

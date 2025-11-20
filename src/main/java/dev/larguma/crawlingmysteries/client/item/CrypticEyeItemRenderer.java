package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrypticEyeItemRenderer extends GeoItemRenderer<CrypticEyeItem> {
  public CrypticEyeItemRenderer() {
    super(new CrypticEyeItemModel());
  }
}

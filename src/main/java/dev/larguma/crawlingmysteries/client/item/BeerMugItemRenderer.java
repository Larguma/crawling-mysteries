package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BeerMugItemRenderer extends GeoItemRenderer<BeerMugItem> {
  public BeerMugItemRenderer() {
    super(new BeerMugItemModel());
  }
}

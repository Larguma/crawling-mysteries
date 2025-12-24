package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.BeerKegItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BeerKegItemRenderer extends GeoItemRenderer<BeerKegItem> {
  public BeerKegItemRenderer() {
    super(new BeerKegItemModel());
  }
}

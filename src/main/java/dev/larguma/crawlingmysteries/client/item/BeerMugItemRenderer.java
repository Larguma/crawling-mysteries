package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BeerMugItemRenderer extends GeoItemRenderer<BeerMugItem> {
  public BeerMugItemRenderer() {
    super(new BeerMugItemModel());
    addRenderLayer(new GooglyEyesLayer<BeerMugItem>(this, 0.13d, 0.35d, 0d, 270f));
  }
}

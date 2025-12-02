package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CrypticEyeItemRenderer extends GeoItemRenderer<CrypticEyeItem> {
  public CrypticEyeItemRenderer() {
    super(new CrypticEyeItemModel());

    addRenderLayer(new AutoGlowingGeoLayer<>(this));
  }
}

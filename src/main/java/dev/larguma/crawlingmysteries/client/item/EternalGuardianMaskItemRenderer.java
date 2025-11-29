package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EternalGuardianMaskItemRenderer extends GeoItemRenderer<EternalGuardianMaskItem> {
  public EternalGuardianMaskItemRenderer() {
    super(new EternalGuardianMaskItemModel());
  }
}

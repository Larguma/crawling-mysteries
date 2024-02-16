package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.item.custom.MysteriousAmuletItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MysteriousAmuletItemRenderer extends GeoItemRenderer<MysteriousAmuletItem> {

  public MysteriousAmuletItemRenderer() {
    super(new MysteriousAmuletItemModel());
  }
}

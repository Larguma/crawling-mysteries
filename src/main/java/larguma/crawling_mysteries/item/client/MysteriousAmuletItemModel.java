package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.custom.MysteriousAmuletItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MysteriousAmuletItemModel extends GeoModel<MysteriousAmuletItem> {

  @Override
  public Identifier getModelResource(MysteriousAmuletItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "geo/mysterious_amulet.geo.json");
  }

  @Override
  public Identifier getTextureResource(MysteriousAmuletItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/item/mysterious_amulet.png");
  }

  @Override
  public Identifier getAnimationResource(MysteriousAmuletItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "animations/mysterious_amulet.animation.json");
  }
}

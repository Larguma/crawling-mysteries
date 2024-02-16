package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.custom.CrypticEyeItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrypticEyeItemModel extends GeoModel<CrypticEyeItem> {

  @Override
  public Identifier getModelResource(CrypticEyeItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "geo/cryptic_eye.geo.json");
  }

  @Override
  public Identifier getTextureResource(CrypticEyeItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/item/cryptic_eye.png");
  }

  @Override
  public Identifier getAnimationResource(CrypticEyeItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "animations/cryptic_eye.animation.json");
  }
}

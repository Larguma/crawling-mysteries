package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.custom.EternalGuardianHeadItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EternalGuardianHeadItemModel extends GeoModel<EternalGuardianHeadItem> {

  @Override
  public Identifier getModelResource(EternalGuardianHeadItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "geo/eternal_guardian_head.geo.json");
  }

  @Override
  public Identifier getTextureResource(EternalGuardianHeadItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/item/eternal_guardian_head.png");
  }

  @Override
  public Identifier getAnimationResource(EternalGuardianHeadItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "animations/eternal_guardian_head.animation.json");
  }
}

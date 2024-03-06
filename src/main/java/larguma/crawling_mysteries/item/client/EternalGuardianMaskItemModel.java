package larguma.crawling_mysteries.item.client;

import larguma.crawling_mysteries.CrawlingMysteries;
import larguma.crawling_mysteries.item.custom.EternalGuardianMaskItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EternalGuardianMaskItemModel extends GeoModel<EternalGuardianMaskItem> {

  @Override
  public Identifier getModelResource(EternalGuardianMaskItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "geo/eternal_guardian_mask.geo.json");
  }

  @Override
  public Identifier getTextureResource(EternalGuardianMaskItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "textures/item/eternal_guardian_mask.png");
  }

  @Override
  public Identifier getAnimationResource(EternalGuardianMaskItem animatable) {
    return new Identifier(CrawlingMysteries.MOD_ID, "animations/eternal_guardian_mask.animation.json");
  }
}

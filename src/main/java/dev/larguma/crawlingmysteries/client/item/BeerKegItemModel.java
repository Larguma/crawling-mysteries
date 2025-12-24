package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.BeerKegItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BeerKegItemModel extends GeoModel<BeerKegItem> {
  @Override
  public ResourceLocation getModelResource(BeerKegItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "geo/block/beer_keg.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(BeerKegItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/block/beer_keg.png");
  }

  @Override
  public ResourceLocation getAnimationResource(BeerKegItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "animations/block/beer_keg.animation.json");
  }

}

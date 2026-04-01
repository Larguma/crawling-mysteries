package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.CookingAltarTier2Item;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CookingAltarTier2ItemModel extends GeoModel<CookingAltarTier2Item> {

  @Override
  public ResourceLocation getModelResource(CookingAltarTier2Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "geo/block/cooking_altar_tier_2.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(CookingAltarTier2Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/block/cooking_altar_tier_2.png");
  }

  @Override
  public ResourceLocation getAnimationResource(CookingAltarTier2Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
        "animations/block/cooking_altar_tier_2.animation.json");
  }
}

package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.CookingAltarTier1Item;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CookingAltarTier1ItemModel extends GeoModel<CookingAltarTier1Item> {
  @Override
  public ResourceLocation getModelResource(CookingAltarTier1Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "geo/block/cooking_altar_tier_1.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(CookingAltarTier1Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/block/cooking_altar_tier_1.png");
  }

  @Override
  public ResourceLocation getAnimationResource(CookingAltarTier1Item animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
        "animations/block/cooking_altar_tier_1.animation.json");
  }
}

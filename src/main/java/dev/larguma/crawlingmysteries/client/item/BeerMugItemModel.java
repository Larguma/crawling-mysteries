package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.BeerMugItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;

public class BeerMugItemModel extends GeoModel<BeerMugItem> {

  @Override
  public ResourceLocation getModelResource(BeerMugItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "geo/block/beer_mug_1.geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(BeerMugItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/block/beer_mug.png");
  }

  @Override
  public ResourceLocation getAnimationResource(BeerMugItem animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "animations/block/beer_mug.animation.json");
  }

  @Override
  public void setCustomAnimations(BeerMugItem animatable, long instanceId, AnimationState<BeerMugItem> animationState) {
    super.setCustomAnimations(animatable, instanceId, animationState);

    ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
    int beerLevel = animatable.getBeerLevel(stack);

    for (int level = 1; level <= 4; level++) {
      GeoBone beerBone = this.getAnimationProcessor().getBone("beer_" + level);
      if (beerBone != null) {
        beerBone.setHidden(level > beerLevel);
      }
    }

    GeoBone foamBone = this.getAnimationProcessor().getBone("foam");
    if (foamBone != null) {
      if (beerLevel == 4) {
        foamBone.setHidden(false);
      } else {
        foamBone.setHidden(true);
      }
    }
  }
}

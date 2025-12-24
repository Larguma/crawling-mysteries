package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.custom.BeerMugBlock;
import dev.larguma.crawlingmysteries.block.entity.BeerMugBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class BeerMugBlockEntityModel extends GeoModel<BeerMugBlockEntity> {

  @Override
  public ResourceLocation getModelResource(BeerMugBlockEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,
        "geo/block/beer_mug_" + animatable.getBlockState().getValue(BeerMugBlock.MUGS) + ".geo.json");
  }

  @Override
  public ResourceLocation getTextureResource(BeerMugBlockEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "textures/block/beer_mug.png");
  }

  @Override
  public ResourceLocation getAnimationResource(BeerMugBlockEntity animatable) {
    return ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "animations/block/beer_mug.animation.json");
  }

  @Override
  public void setCustomAnimations(BeerMugBlockEntity animatable, long instanceId,
      AnimationState<BeerMugBlockEntity> animationState) {
    super.setCustomAnimations(animatable, instanceId, animationState);

    if (animatable.getLevel() == null)
      return;

    int mugs = animatable.getBlockState().getValue(BeerMugBlock.MUGS);
    int beerLevel = animatable.getBeerLevel();

    for (int i = 1; i <= mugs; i++) {
      String suffix = mugs == 1 ? "" : "_" + i;

      for (int level = 1; level <= 4; level++) {
        GeoBone beerBone = this.getAnimationProcessor().getBone("beer_" + level + suffix);
        if (beerBone != null) {
          beerBone.setHidden(level > beerLevel);
        }
      }

      GeoBone foamBone = this.getAnimationProcessor().getBone("foam" + suffix);
      if (foamBone != null) {
        if (beerLevel == 4) {
          foamBone.setHidden(false);
        } else {
          foamBone.setHidden(true);
        }
      }
    }
  }
}

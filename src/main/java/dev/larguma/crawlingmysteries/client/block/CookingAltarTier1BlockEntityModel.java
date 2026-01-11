package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.entity.CookingAltarTier1BlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CookingAltarTier1BlockEntityModel extends DefaultedBlockGeoModel<CookingAltarTier1BlockEntity> {

  public CookingAltarTier1BlockEntityModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "cooking_altar_tier_1"));
  }
}

package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.entity.custom.CookingAltarTier2BlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CookingAltarTier2BlockEntityModel extends DefaultedBlockGeoModel<CookingAltarTier2BlockEntity> {

  public CookingAltarTier2BlockEntityModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "cooking_altar_tier_2"));
  }
}

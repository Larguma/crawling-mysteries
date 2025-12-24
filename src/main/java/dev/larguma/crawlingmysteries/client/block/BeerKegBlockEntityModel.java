package dev.larguma.crawlingmysteries.client.block;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.entity.BeerKegBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BeerKegBlockEntityModel extends DefaultedBlockGeoModel<BeerKegBlockEntity> {

  public BeerKegBlockEntityModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "beer_keg"));
  }
}

package dev.larguma.crawlingmysteries.block.entity.client.block;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.entity.TombstoneBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class TombstoneBlockEntityModel extends DefaultedBlockGeoModel<TombstoneBlockEntity> {
  public TombstoneBlockEntityModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "tombstone"));
  }
}

package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianMaskItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EternalGuardianMaskItemModel extends DefaultedItemGeoModel<EternalGuardianMaskItem> {
  public EternalGuardianMaskItemModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID,"eternal_guardian_mask"));
  }
}

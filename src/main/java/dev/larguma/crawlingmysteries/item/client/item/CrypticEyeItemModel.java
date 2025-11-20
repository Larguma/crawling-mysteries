package dev.larguma.crawlingmysteries.item.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.CrypticEyeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CrypticEyeItemModel extends DefaultedItemGeoModel<CrypticEyeItem> {
  public CrypticEyeItemModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MODID, "cryptic_eye"));
  }
}

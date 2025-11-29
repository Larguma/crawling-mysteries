package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.custom.EternalGuardianHeadItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EternalGuardianHeadItemModel extends DefaultedItemGeoModel<EternalGuardianHeadItem> {
  public EternalGuardianHeadItemModel() {
    super(ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "eternal_guardian_head"));
  }

}

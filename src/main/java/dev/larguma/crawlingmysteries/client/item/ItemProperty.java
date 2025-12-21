package dev.larguma.crawlingmysteries.client.item;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.data.custom.HorseshoeDataComponent;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ItemProperty {

  public static void register(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ItemProperties.register(
          ModItems.LUCKY_HORSESHOE.get(),
          ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, "tier"),
          (stack, level, entity, seed) -> {
            HorseshoeDataComponent component = stack.get(ModDataComponents.HORSESHOE_TIER.get());
            return component != null ? (float) component.tier() : 1.0f;
          });
    });
  }
}
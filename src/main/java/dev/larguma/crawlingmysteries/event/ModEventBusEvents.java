package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.entity.custom.EternalGuardianEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModEventBusEvents {
  @SubscribeEvent
  public static void registerAttributes(EntityAttributeCreationEvent event) {
    event.put(ModEntities.ETERNAL_GUARDIAN.get(), EternalGuardianEntity.createAttributes().build());
  }
}

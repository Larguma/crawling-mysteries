package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.entity.ai.DrunkAggroGoal;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModEntityEvents {

  @SubscribeEvent
  public static void onEntityJoin(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Monster monster) {
      monster.targetSelector.addGoal(1, new DrunkAggroGoal(monster));
    }
  }
}

package dev.larguma.crawlingmysteries.client.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID, value = Dist.CLIENT)
public class RenderEvents {

  @SubscribeEvent
  public static void onRenderFoodBar(RenderGuiLayerEvent.Pre event) {
    if (!event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {
      return;
    }

    if (Minecraft.getInstance().player != null
        && Minecraft.getInstance().player.hasEffect(ModMobEffects.IRON_STOMACH)) {
      event.setCanceled(true);
    }
  }
}

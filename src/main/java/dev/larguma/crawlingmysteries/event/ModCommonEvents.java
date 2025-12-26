package dev.larguma.crawlingmysteries.event;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.potion.ModPotions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModCommonEvents {
  @SubscribeEvent
  public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
    PotionBrewing.Builder builder = event.getBuilder();
    builder.addMix(Potions.AWKWARD, Items.WHEAT, ModPotions.BEER);
    builder.addMix(ModPotions.BEER, Items.GLOWSTONE_DUST, ModPotions.STRONG_BEER);
  }
}

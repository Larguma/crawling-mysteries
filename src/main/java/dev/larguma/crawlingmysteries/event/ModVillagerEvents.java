package dev.larguma.crawlingmysteries.event;

import java.util.List;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.villager.ModVillager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = CrawlingMysteries.MOD_ID)
public class ModVillagerEvents {

  @SubscribeEvent
  public static void onVillagerTradesEvent(VillagerTradesEvent event) {
    if (event.getType() == ModVillager.BARKEEPER.value()) {
      Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

      trades.get(1).add((pTrader, pRandom) -> new MerchantOffer(
          new ItemCost(Items.EMERALD, 6),
          new ItemStack(ModItems.MUSIC_DISC_OST_01.get(), 1), 1, 10, 0.05f));
    }
  }

  @SubscribeEvent
  public static void onEntityTick(EntityTickEvent.Pre event) {
    if (event.getEntity() instanceof Villager villager) {
      VillagerData data = villager.getVillagerData();

      if (data.getProfession() == ModVillager.BARKEEPER.value()
          && data.getType() != ModVillager.BLANK_TYPE.value()) {
        villager.setVillagerData(data.setType(ModVillager.BLANK_TYPE.value()));
      }
    }
  }
}

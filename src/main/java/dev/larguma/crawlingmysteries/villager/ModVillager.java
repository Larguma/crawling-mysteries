package dev.larguma.crawlingmysteries.villager;

import com.google.common.collect.ImmutableSet;
import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModVillager {
  public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister
      .create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, CrawlingMysteries.MOD_ID);
  public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister
      .create(BuiltInRegistries.VILLAGER_PROFESSION, CrawlingMysteries.MOD_ID);
  public static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister
      .create(BuiltInRegistries.VILLAGER_TYPE, CrawlingMysteries.MOD_ID);

  public static final Holder<PoiType> BEER_KEG_POI = POI_TYPES.register("beer_keg_poi",
      () -> new PoiType(ImmutableSet.copyOf(ModBlocks.TOMBSTONE.get().getStateDefinition().getPossibleStates()), 1, 1));
  public static final Holder<VillagerProfession> BARKEEPER = PROFESSIONS.register("barkeeper",
      () -> new VillagerProfession("barkeeper", holder -> holder.value() == BEER_KEG_POI.value(),
          holder -> holder.value() == BEER_KEG_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
          SoundEvents.GENERIC_DRINK));

  public static final Holder<VillagerType> BLANK_TYPE = VILLAGER_TYPES.register("blank",
      () -> new VillagerType("blank"));

  public static void register(IEventBus eventBus) {
    POI_TYPES.register(eventBus);
    PROFESSIONS.register(eventBus);
    VILLAGER_TYPES.register(eventBus);
  }
}
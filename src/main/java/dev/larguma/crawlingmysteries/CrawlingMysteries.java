package dev.larguma.crawlingmysteries;

import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.data.ModDataAttachments;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.effect.ModMobEffects;
import dev.larguma.crawlingmysteries.entity.ModEntities;
import dev.larguma.crawlingmysteries.item.ModCreativeModeTabs;
import dev.larguma.crawlingmysteries.item.ModItems;
import dev.larguma.crawlingmysteries.loot.ModLootModifiers;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CrawlingMysteries.MOD_ID)
public class CrawlingMysteries {
  public static final String MOD_ID = "crawlingmysteries";
  public static final Logger LOGGER = LogUtils.getLogger();
  public static final UUID ELDRICTH_WEAVER_UUID = UUID.fromString("4a14921f-ea91-4d12-8583-4bba50f6de8b");
  public static final String ELDRICTH_WEAVER_NAME = "Larguma";

  public CrawlingMysteries(IEventBus modEventBus, ModContainer modContainer) {

    NeoForge.EVENT_BUS.register(this);

    ModBlockEntities.register(modEventBus);
    ModBlocks.register(modEventBus);
    ModCreativeModeTabs.register(modEventBus);
    ModDataAttachments.register(modEventBus);
    ModDataComponents.register(modEventBus);
    ModEntities.register(modEventBus);
    ModItems.register(modEventBus);
    ModLootModifiers.register(modEventBus);
    ModMobEffects.register(modEventBus);
    ModSpells.init();

    modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigClient.CLIENT_SPEC);
    modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    modEventBus.addListener(this::addCreative);
  }

  private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
      event.accept(ModItems.ETERNAL_GUARDIAN_SPAWN_EGG);
    }
  }

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
  }
}

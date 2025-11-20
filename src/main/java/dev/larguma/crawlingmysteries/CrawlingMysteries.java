package dev.larguma.crawlingmysteries;

import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.larguma.crawlingmysteries.block.ModBlocks;
import dev.larguma.crawlingmysteries.block.entity.ModBlockEntities;
import dev.larguma.crawlingmysteries.item.ModCreativeModeTabs;
import dev.larguma.crawlingmysteries.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CrawlingMysteries.MODID)
public class CrawlingMysteries {
    public static final String MODID = "crawlingmysteries";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final UUID ELDRICTH_WEAVER_UUID = UUID.fromString("4a14921f-ea91-4d12-8583-4bba50f6de8b");
    public static final String ELDRICTH_WEAVER_NAME = "Larguma";

    // The constructor for the mod class is the first code that is run when your mod
    // is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and
    // pass them in automatically.
    public CrawlingMysteries(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class
        // (CrawlingMysteries) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in
        // this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config
        // file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}

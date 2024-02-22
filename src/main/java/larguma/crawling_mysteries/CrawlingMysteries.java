package larguma.crawling_mysteries;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.ModBlockEntities;
import larguma.crawling_mysteries.config.CrawlingMysteriesConfig;
import larguma.crawling_mysteries.item.ModItemGroups;
import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.util.ModLootTableModifiers;
import net.fabricmc.api.ModInitializer;

public class CrawlingMysteries implements ModInitializer {
	public static final String MOD_ID = "crawling-mysteries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CrawlingMysteriesConfig CONFIG = CrawlingMysteriesConfig.createAndLoad();
	public static final UUID ELDRICTH_WEAVER_UUID = UUID.fromString("4a14921f-ea91-4d12-8583-4bba50f6de8b");
	public static final String ELDRICTH_WEAVER_NAME = "Larguma";

	@Override
	public void onInitialize() {
		LOGGER.info("Let's uncover some mysteries");

		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		
		ModLootTableModifiers.modifyLootTables();

		ModBlockEntities.registerBlockEntities();
	}
}
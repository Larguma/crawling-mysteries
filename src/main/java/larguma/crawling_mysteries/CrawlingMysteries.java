package larguma.crawling_mysteries;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import larguma.crawling_mysteries.item.ModItemGroups;
import larguma.crawling_mysteries.item.ModItems;

public class CrawlingMysteries implements ModInitializer {
	public static final String MOD_ID = "crawling-mysteries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CrawlingMysteriesConfig CONFIG = CrawlingMysteriesConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Let's uncover some mysteries");

		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		
	}
}
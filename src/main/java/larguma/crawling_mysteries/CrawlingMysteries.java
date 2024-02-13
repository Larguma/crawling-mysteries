package larguma.crawling_mysteries;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingMysteries implements ModInitializer {
	public static final String MOD_ID = "crawling-mysteries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Let's uncover some mysteries");
		
	}
}
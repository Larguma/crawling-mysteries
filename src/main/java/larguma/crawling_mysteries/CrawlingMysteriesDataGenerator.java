package larguma.crawling_mysteries;

import larguma.crawling_mysteries.datagen.ModAdvancementProvider;
import larguma.crawling_mysteries.datagen.ModBlockTagProvider;
import larguma.crawling_mysteries.datagen.ModEntityTypeTagProvider;
import larguma.crawling_mysteries.datagen.ModItemTagProvider;
import larguma.crawling_mysteries.datagen.ModLootTableProvider;
import larguma.crawling_mysteries.datagen.ModModelProvider;
import larguma.crawling_mysteries.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CrawlingMysteriesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModAdvancementProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModEntityTypeTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
	}
}

package larguma.crawling_mysteries;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import larguma.crawling_mysteries.block.ModBlocks;
import larguma.crawling_mysteries.block.entity.ModBlockEntities;
import larguma.crawling_mysteries.config.CrawlingMysteriesConfig;
import larguma.crawling_mysteries.effect.ModEffect;
import larguma.crawling_mysteries.entity.ModEntities;
import larguma.crawling_mysteries.entity.custom.EternalGuardianEntity;
import larguma.crawling_mysteries.item.ModItems;
import larguma.crawling_mysteries.networking.ModMessages;
import larguma.crawling_mysteries.particle.ModParticles;
import larguma.crawling_mysteries.util.ModLootTableModifiers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.Identifier;

public class CrawlingMysteries implements ModInitializer {
	public static final String MOD_ID = "crawling-mysteries";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CrawlingMysteriesConfig CONFIG = CrawlingMysteriesConfig.createAndLoad();
	public static final UUID ELDRICTH_WEAVER_UUID = UUID.fromString("4a14921f-ea91-4d12-8583-4bba50f6de8b");
	public static final String ELDRICTH_WEAVER_NAME = "Larguma";
	public static final Identifier ICON_TEXTURE = new Identifier(MOD_ID, "icon.png");

	public static final OwoItemGroup CRAWLING_MYSTERIES_GROUP = OwoItemGroup
			.builder(new Identifier(CrawlingMysteries.MOD_ID, "crawling_mysteries_group"),
					() -> Icon.of(ModItems.CRYPTIC_EYE))
			.buttonStackHeight(3)
			.initializer(group -> {
				group.addTab(Icon.of(ModItems.CRYPTIC_EYE), "trinkets", null, true);
				group.addTab(Icon.of(ModBlocks.TOMBSTONE), "others", null, true);
			})
			.build();

	@Override
	public void onInitialize() {
		LOGGER.info("Awakening the mystical energies... The shadows whisper as new secrets are ready to be unveiled.");

		FieldRegistrationHandler.register(ModItems.class, MOD_ID, false);
		FieldRegistrationHandler.register(ModBlocks.class, MOD_ID, false);
		FieldRegistrationHandler.register(ModBlockEntities.class, MOD_ID, false);
		FieldRegistrationHandler.register(ModEntities.class, MOD_ID, false);
		FieldRegistrationHandler.register(ModParticles.class, MOD_ID, false);
		FieldRegistrationHandler.register(ModEffect.class, MOD_ID, false);

		CRAWLING_MYSTERIES_GROUP.initialize();
		ModLootTableModifiers.modifyLootTables();

		ModMessages.init();
	}
}
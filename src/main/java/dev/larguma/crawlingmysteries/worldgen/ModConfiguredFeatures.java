package dev.larguma.crawlingmysteries.worldgen;

import java.util.List;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ModConfiguredFeatures {

  public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTERIOUS_STONE_ORE_KEY = registerKey(
      "mysterious_stone_ore");

  public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
    RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    List<OreConfiguration.TargetBlockState> mysteriousStoneOres = List.of(
        OreConfiguration.target(deepslateReplaceables, ModBlocks.MYSTERIOUS_STONE.get().defaultBlockState()));

    register(context, MYSTERIOUS_STONE_ORE_KEY, Feature.ORE, new OreConfiguration(mysteriousStoneOres, 3));
  }

  public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
    return ResourceKey.create(Registries.CONFIGURED_FEATURE,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name));
  }

  private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
      BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature,
      FC configuration) {
    context.register(key, new ConfiguredFeature<>(feature, configuration));
  }
}

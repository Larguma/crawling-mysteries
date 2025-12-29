package dev.larguma.crawlingmysteries.worldgen;

import java.util.List;

import dev.larguma.crawlingmysteries.CrawlingMysteries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class ModPlacedFeatures {
  public static final ResourceKey<PlacedFeature> MYSTERIOUS_STONE_ORE_PLACED_KEY = registerKey(
      "mysterious_stone_ore_placed");

  public static void bootstrap(BootstrapContext<PlacedFeature> context) {
    HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

    register(context, MYSTERIOUS_STONE_ORE_PLACED_KEY,
        configuredFeatures.getOrThrow(ModConfiguredFeatures.MYSTERIOUS_STONE_ORE_KEY),
        ModOrePlacements.commonOrePlacement(1,
            HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0))));

  }

  private static ResourceKey<PlacedFeature> registerKey(String name) {
    return ResourceKey.create(Registries.PLACED_FEATURE,
        ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, name));
  }

  private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
      Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
    context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
  }

}

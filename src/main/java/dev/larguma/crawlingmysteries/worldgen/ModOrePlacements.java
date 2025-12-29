package dev.larguma.crawlingmysteries.worldgen;

import java.util.List;

import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ModOrePlacements {
  public static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange) {
    return List.of(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
  }

  public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
    return orePlacement(CountPlacement.of(count), heightRange);
  }

  public static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
    return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
  }
}

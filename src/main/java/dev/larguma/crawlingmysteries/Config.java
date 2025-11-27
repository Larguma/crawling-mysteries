package dev.larguma.crawlingmysteries;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

  public static final ModConfigSpec.BooleanValue RENDER_TRINKETS = BUILDER
      .translation("config.crawlingmysteries.render_trinkets")
      .define("render_trinkets", true);

  public static final ModConfigSpec.BooleanValue ENABLE_TOMBSTONE = BUILDER
      .translation("config.crawlingmysteries.enable_tombstone")
      .define("enable_tombstone", true);

  static final ModConfigSpec SPEC = BUILDER.build();
}

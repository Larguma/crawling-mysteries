package dev.larguma.crawlingmysteries;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
  private static final ModConfigSpec.Builder BUILDER_COMMON = new ModConfigSpec.Builder();
  private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();

  // #region Common
  public static final ModConfigSpec.BooleanValue RENDER_TRINKETS = BUILDER_COMMON
      .translation("config.crawlingmysteries.render_trinkets")
      .define("render_trinkets", true);

  static final ModConfigSpec COMMON_SPEC = BUILDER_COMMON.build();
  // #endregion Common

  // #region Server
  public static final ModConfigSpec.BooleanValue ENABLE_TOMBSTONE = BUILDER_SERVER
      .translation("config.crawlingmysteries.enable_tombstone")
      .define("enable_tombstone", true);

  static final ModConfigSpec SERVER_SPEC = BUILDER_SERVER.build();
  // #endregion Server
}

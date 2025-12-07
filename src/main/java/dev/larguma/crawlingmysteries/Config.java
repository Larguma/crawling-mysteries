package dev.larguma.crawlingmysteries;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
  private static final ModConfigSpec.Builder BUILDER_CLIENT = new ModConfigSpec.Builder();
  private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();

  // #region Client
  public static final ModConfigSpec.BooleanValue RENDER_TRINKETS = BUILDER_CLIENT
      .translation("config.crawlingmysteries.render_trinkets")
      .define("render_trinkets", true);

  public static final ModConfigSpec.BooleanValue RENDER_PASSIVE_SPELL_HUD = BUILDER_CLIENT
      .translation("config.crawlingmysteries.render_passive_spell_hud")
      .define("render_passive_spell_hud", true);

  static final ModConfigSpec CLIENT_SPEC = BUILDER_CLIENT.build();
  // #endregion Client

  // #region Server
  public static final ModConfigSpec.BooleanValue ENABLE_TOMBSTONE = BUILDER_SERVER
      .translation("config.crawlingmysteries.enable_tombstone")
      .define("enable_tombstone", true);

  static final ModConfigSpec SERVER_SPEC = BUILDER_SERVER.build();
  // #endregion Server
}

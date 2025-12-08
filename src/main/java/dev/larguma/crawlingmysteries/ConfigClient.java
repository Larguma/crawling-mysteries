package dev.larguma.crawlingmysteries;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigClient {

  public static final ModConfigSpec CLIENT_SPEC;
  public static final Client CLIENT;
  private static final String CONFIG_PREFIX = "config." + CrawlingMysteries.MOD_ID;

  static {
    final Pair<Client, ModConfigSpec> PAIR = new ModConfigSpec.Builder().configure(Client::new);
    CLIENT = PAIR.getLeft();
    CLIENT_SPEC = PAIR.getRight();
  }

  public static class Client {

    public final ModConfigSpec.BooleanValue renderTrinkets;
    public final ModConfigSpec.BooleanValue renderPassiveSpellHud;

    Client(ModConfigSpec.Builder builder) {

      builder.comment(" CLIENT ONLY SETTINGS, MOSTLY RENDERING");
      builder.comment(" [Client]");
      // builder.push("client"); to make subcategories

      renderTrinkets = builder
          .comment(" Whether trinkets should be rendered on players")
          .translation(CONFIG_PREFIX + ".render_trinkets")
          .define("render_trinkets", true);

      renderPassiveSpellHud = builder
          .comment(" Whether the passive spell HUD should be rendered")
          .translation(CONFIG_PREFIX + ".render_passive_spell_hud")
          .define("render_passive_spell_hud", true);
    }
  }
}

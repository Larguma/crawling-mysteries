package dev.larguma.crawlingmysteries;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

  public static final ModConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "config." + CrawlingMysteries.MOD_ID;

  static {
    final Pair<Server, ModConfigSpec> SERVER_PAIR = new ModConfigSpec.Builder().configure(Server::new);
    SERVER_SPEC = SERVER_PAIR.getRight();
    SERVER = SERVER_PAIR.getLeft();
  }

  public static class Server {

    public final ModConfigSpec.BooleanValue enableTombstone;

    public Server(ModConfigSpec.Builder builder) {

      builder.comment(" SERVER ONLY SETTINGS, MOSTLY GAMEPLAY");
      builder.comment(" [Server]");
      // builder.push("server"); to make subcategories

      enableTombstone = builder
          .comment(" Whether tombstones should be created on player death when keeping inventory is disabled")
          .translation(CONFIG_PREFIX + ".enable_tombstone")
          .define("enable_tombstone", true);
    }
  }
}

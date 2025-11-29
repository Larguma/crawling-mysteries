package dev.larguma.crawlingmysteries.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ModUtils {
  public static PlayerTeam getOrCreateTeam(Scoreboard scoreboard, String teanName, ChatFormatting color) {
    if (scoreboard.getTeamNames().contains(teanName)) {
      return scoreboard.getPlayerTeam(teanName);
    } else {
      PlayerTeam team = scoreboard.addPlayerTeam(teanName);
      team.setDisplayName(Component.literal(teanName));
      team.setColor(color);
      return team;
    }
  }

  public static void removeTeam(Scoreboard scoreboard, String teamName) {
    if (scoreboard.getTeamNames().contains(teamName)) {
      PlayerTeam team = scoreboard.getPlayerTeam(teamName);
      scoreboard.removePlayerTeam(team);
    }
  }
}

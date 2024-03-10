package larguma.crawling_mysteries.util;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModUtils {
  public static Team getOrCreateTeam(Scoreboard scoreboard, String teanName, Formatting color) {
    if (scoreboard.getTeamNames().contains(teanName)) {
      return scoreboard.getTeam(teanName);
    } else {
      Team team = scoreboard.addTeam(teanName);
      team.setDisplayName(Text.literal(teanName));
      team.setColor(color);
      return team;
    }
  }
}

package de.aventax.api;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class ScoreboardManager {

    public Scoreboard scoreboard;
    public Objective objective;
    private final HashMap<Integer, ScoreboardEntry> entryHashMap = new HashMap<>();

    public class ScoreboardEntry {

        Team team;

        public void updateText(String text) {
            if (text.length() > 16) {
                String[] data = text.substring(0, 16).split("");
                String lastColorCode = "";
                int pl = 0;
                for (int i = 0; i < data.length; i++) {
                    if (data[i].equalsIgnoreCase("§")) {
                        if (i <= 14) {
                            lastColorCode = data[i] + data[i + 1];
                        } else {
                            pl = 1;
                            lastColorCode = data[i] + text.substring(16).split("")[0];
                        }
                    }
                }

                team.setSuffix(lastColorCode + text.substring(16 + pl));
                team.setPrefix(text.substring(0, 16 - pl));
            } else {
                team.setSuffix("");
                team.setPrefix(text);
            }
        }

    }
}
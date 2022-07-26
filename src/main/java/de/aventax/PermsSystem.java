package de.aventax;

import de.aventax.commands.RankCMD;
import de.aventax.utils.MongoDB;
import de.aventax.utils.RangSystem;
import de.aventax.utils.ScoreboardTeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PermsSystem extends JavaPlugin {

    public static final String PREFIX = "§cPerms §7┃ §f";

    @Override
    public void onEnable() {
        MongoDB.connect("host", 27017, "user", "pass", "PW");
        Bukkit.getPluginManager().registerEvents(new RangSystem(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardTeamManager(), this);
        getCommand("rang").setExecutor(new RankCMD());

    }
}

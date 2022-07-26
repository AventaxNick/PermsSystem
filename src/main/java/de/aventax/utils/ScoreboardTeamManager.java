package de.aventax.utils;

import de.aventax.PermsSystem;
import de.aventax.api.Rank;
import de.aventax.api.TeamObject;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import obf.Bukkit.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScoreboardTeamManager implements Listener {

    public static HashMap<UUID, TeamObject> teamColor = new HashMap<>();
    private static final HashMap<UUID, Scoreboard> playerScoreboard = new HashMap<>();

    public static Scoreboard getScoreboard(Player p) {
        if (playerScoreboard.containsKey(p.getUniqueId())) {
            return playerScoreboard.get(p.getUniqueId());
        } else {
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            p.setScoreboard(sb);
            playerScoreboard.put(p.getUniqueId(), sb);
            return sb;
        }
    }

    public static void setTeamObj(Player p, TeamObject teamObject) {
        teamColor.put(p.getUniqueId(), teamObject);
        if (playerScoreboard.containsKey(p.getUniqueId())) setNewTeam(p);
    }

    public static void resetTeamObj(Player p) {
        teamColor.remove(p.getUniqueId());
        if (playerScoreboard.containsKey(p.getUniqueId())) setNewTeam(p);
    }

    public static void setNewTeam(Player p) {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            setNew(p, p1);//Player there see, Display
            setNew(p1, p);
        }
    }

    public static void setNew(Player see, Player where) {
        if (teamColor.containsKey(where.getUniqueId())) {
            TeamObject teamObject = teamColor.get(where.getUniqueId());
            Scoreboard sb = getScoreboard(see);

            Team result = null;
            List<String> teamNames = new ArrayList<>();
            for (Team team : sb.getTeams()) {
                teamNames.add(team.getName());
                boolean a = true;
                boolean b = true;

                if (teamObject.prefix != null && !team.getPrefix().equals(teamObject.prefix)) a = false;
                if (teamObject.suffix != null && !team.getSuffix().equals(teamObject.suffix)) b = false;

                if (a && b) {
                    result = team;
                    teamNames.clear();
                    break;
                }
            }

            if (result == null) {

                String random = CodeGenerator.getFullCode();

                while (teamNames.contains(random + "C")) {
                    random = CodeGenerator.getFullCode();
                }

                Team team = sb.registerNewTeam(random + "C");

                if (teamObject.prefix != null) team.setPrefix(teamObject.prefix);
                if (teamObject.suffix != null) team.setSuffix(teamObject.suffix);

                team.addPlayer(where);

            } else {
                result.addPlayer(where);
            }

            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) where).getHandle());
            ((CraftPlayer) see).getHandle().playerConnection.sendPacket(playerInfo);
            PacketPlayOutPlayerInfo playerInfo2 = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) where).getHandle());
            ((CraftPlayer) see).getHandle().playerConnection.sendPacket(playerInfo2);

        } else {
            Rank r = RangSystem.getRang(where.getUniqueId());
            Scoreboard sb = getScoreboard(see);
            int a = (Rank.values().length + 1 - r.id);
            String id;
            if (a < 10) {
                id = "0" + a;
            } else {
                id = a + "";
            }
            Team team = sb.getTeam("T" + id);
            if (team == null) team = sb.registerNewTeam("T" + id);
            team.setPrefix(r.prefix);
            team.addPlayer(where);
            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) where).getHandle());
            ((CraftPlayer) see).getHandle().playerConnection.sendPacket(playerInfo);
            PacketPlayOutPlayerInfo playerInfo2 = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) where).getHandle());
            ((CraftPlayer) see).getHandle().playerConnection.sendPacket(playerInfo2);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        playerScoreboard.remove(e.getPlayer().getUniqueId());
        teamColor.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Scheduler.runTaskLater(PermsSystem.getPlugin(PermsSystem.class), () -> e.getPlayer().setScoreboard(getScoreboard(p)), 5);

        Scheduler.runTaskLater(PermsSystem.getPlugin(PermsSystem.class), () -> {
            e.getPlayer().setScoreboard(getScoreboard(p));
            setNewTeam(e.getPlayer());
        }, 10);
        Scheduler.runTaskLater(PermsSystem.getPlugin(PermsSystem.class), () -> {
            e.getPlayer().setScoreboard(getScoreboard(p));
            setNewTeam(e.getPlayer());
        }, 20);
        Scheduler.runTaskLater(PermsSystem.getPlugin(PermsSystem.class), () -> {
            e.getPlayer().setScoreboard(getScoreboard(p));
            setNewTeam(e.getPlayer());
        }, 40);
    }

}

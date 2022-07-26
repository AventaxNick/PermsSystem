package de.aventax.commands;

import de.aventax.PermsSystem;
import de.aventax.api.Rank;
import de.aventax.api.UUIDFetcher;
import de.aventax.utils.MongoDB;
import de.aventax.utils.RangSystem;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String sa, String[] args) {

        if (commandSender instanceof Player) {

            Player p = (Player) commandSender;
            if (!RangSystem.attachment.get(p.getUniqueId()).getPermissions().containsKey("perms.*")) {
                p.sendMessage("§cKeine Rechte");
                return true;
            }
            if (!RangSystem.attachment.get(p.getUniqueId()).getPermissions().get("perms.*")) {
                p.sendMessage("§cKeine Rechte.");
                return true;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage("§e/rang set [NAME] [RANG]");
            commandSender.sendMessage("§e/rang user [Name]");
            commandSender.sendMessage("§e/rang user [Name] add [Permission]");
            commandSender.sendMessage("§e/rang user [Name] remove [Permission]");
            commandSender.sendMessage("§e/rang group [RangSystem]");
            commandSender.sendMessage("§e/rang group [RangSystem] remove [Permission]");
            commandSender.sendMessage("§e/rang group [RangSystem] add [Permission]");
            commandSender.sendMessage("§e/rang group [RangSystem] remove [Permission] [ServerName]");
            commandSender.sendMessage("§e/rang group [RangSystem] add [Permission] [ServerName]");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                Rank rang = null;
                for (Rank rank : Rank.values()) {
                    if (rank.toString().equalsIgnoreCase(args[1])) {
                        rang = rank;
                        break;
                    }
                }
                if (rang == null) {
                    commandSender.sendMessage("§cRang nicht gefunden.");
                    return true;
                }
                String user = null;

                for (String s : rang.permissions) {
                    if (user == null) {
                        user = " " + s;
                    } else {
                        user += "\n " + s;
                    }
                }

                commandSender.sendMessage("§cRang " + rang.name + " besitzt folgene Permissions: \n" + user);
            }
            if (args[0].equalsIgnoreCase("user")) {
                UUID target = UUIDFetcher.getUUID(args[1]);
                if (target != null) {
                    return true;
                }
                String out = String.join(", ", RangSystem.getPerms(target));
                Rank rank = RangSystem.getRang(target);
                commandSender.sendMessage("§cDer Spieler hat den Rank: " + rank.color + rank.name);
                commandSender.sendMessage("§cSpieler " + args[1] + " besitzt folgene Permissions: \n" + out);
            }
        }

        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("group")) {
                Rank r = null;
                for (Rank rank : Rank.values()) {
                    if (rank.toString().equalsIgnoreCase(args[1])) {
                        r = rank;
                        break;
                    }
                }
                if (r == null) {
                    commandSender.sendMessage("§cRang nicht gefunden.");
                    return true;
                }
                Rank finalR = r;
                if (args[2].equalsIgnoreCase("add")) {
                    if (!RangSystem.getPerms(finalR).contains(args[3])) {
                        finalR.permissions.add(args[3]);
                        MongoDB.getCollection("Permission").getStructure().insertOne(new Document("key", finalR.name + "." + args[4]).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den RangSystem " + finalR.name + " die permission " + args[3] + " hinzugefügt.");
                    }
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    if (!RangSystem.getPerms(finalR).contains(args[3])) {
                        finalR.permissions.remove(args[3]);
                        MongoDB.getCollection("Permission").getStructure().deleteOne(new Document("key", finalR.name + "." + args[4]).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den RangSystem " + finalR.name + " die permission " + args[3] + " entfernt.");
                    }
                }
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("user")) {
                UUID target = UUIDFetcher.getUUID(args[1]);
                if (target == null) {
                    return true;
                }
                if (args[2].equalsIgnoreCase("add")) {
                    if (!RangSystem.getPerms(target).contains(args[3])) {
                        MongoDB.getCollection("Permission").getStructure().insertOne(new Document("key", target.toString()).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den Spieler " + args[1] + " die permission " + args[3] + " hinzugefügt.");

                    }
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    if (!RangSystem.getPerms(target).contains(args[3])) {
                        MongoDB.getCollection("Permission").getStructure().deleteOne(new Document("key", target.toString()).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den Spieler " + args[1] + " die permission " + args[3] + " entfernt.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("group")) {
                Rank rang = null;
                for (Rank rank : Rank.values()) {
                    if (rank.toString().equalsIgnoreCase(args[1])) {
                        rang = rank;
                        break;
                    }
                }
                if (rang == null) {
                    commandSender.sendMessage("§cRang nicht gefunden.");
                    return true;
                }
                Rank finalR = rang;
                if (args[2].equalsIgnoreCase("add")) {
                    if (!RangSystem.getPerms(finalR).contains(args[3])) {
                        finalR.permissions.add(args[3]);
                        MongoDB.getCollection("Permission").getStructure().insertOne(new Document("key", finalR.name).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den RangSystem " + finalR.name + " die permission " + args[3] + " hinzugefügt.");

                    }
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    if (!RangSystem.getPerms(finalR).contains(args[3])) {
                        finalR.permissions.remove(args[3]);
                        MongoDB.getCollection("Permission").getStructure().deleteOne(new Document("key", finalR.name).append("perm", args[3]));
                        commandSender.sendMessage(PermsSystem.PREFIX + "Du hast den RangSystem " + finalR.name + " die permission " + args[3] + " entfernt.");

                    }
                }
            }
        }

        if (args.length == 3) {

            if (args[0].equalsIgnoreCase("set")) {
                UUID target = UUIDFetcher.getUUID(args[1]);
                if (target != null) {
                    commandSender.sendMessage("§cSpieler nicht gefunden.");
                    return true;
                }
                Rank r = null;
                for (Rank rank : Rank.values()) {
                    if (rank.toString().equalsIgnoreCase(args[2])) {
                        r = rank;
                        break;
                    }
                }
                if (r == null) {
                    commandSender.sendMessage("§cRang nicht gefunden.");
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    RangSystem.rang.put(target, r);
                }
                RangSystem.setRank(target, r);
                commandSender.sendMessage("§eRang geändert.");
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("settemp")) {
                UUID target = UUIDFetcher.getUUID(args[1]);
                if (target != null) {
                    commandSender.sendMessage("§cSpieler nicht gefunden.");
                    return true;
                }
                Rank rang = null;
                for (Rank rank : Rank.values()) {
                    if (rank.toString().equalsIgnoreCase(args[2])) {
                        rang = rank;
                        break;
                    }
                }
                if (rang == null) {
                    commandSender.sendMessage("§cRang nicht gefunden.");
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    if (RangSystem.rang.containsKey(target)) {
                        if (rang.getId() > RangSystem.rang.get(target).getId()) {
                            RangSystem.rang.put(target, rang);
                        }
                    } else {
                        RangSystem.rang.put(target, rang);
                    }
                }
                RangSystem.setTempRank(target, rang, Integer.valueOf(args[3]));
                commandSender.sendMessage("§eRang geändert.");
            }
        }
        return true;
    }
}
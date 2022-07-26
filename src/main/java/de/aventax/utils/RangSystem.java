package de.aventax.utils;

import com.mongodb.Block;
import de.aventax.PermsSystem;
import de.aventax.api.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class RangSystem implements Listener {

    public static HashMap<UUID, PermissionAttachment> attachment = new HashMap<>();
    public static HashMap<UUID, Long> joinTime = new HashMap<>();
    public static HashMap<UUID, Rank> rang = new HashMap<>();
    public List<UUID> allowCommands = new ArrayList<>();

    public static LinkedList<String> getPerms(UUID player) {
        LinkedList<String> perms = new LinkedList<>();

        final boolean[] b = {false};

        MongoDB.getCollection("Permission").getStructure().find(new Document("key", player.toString())).forEach((Block<Document>) document -> perms.add(document.getString("perm")));

        while (!b[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return perms;
    }

    public static LinkedList<String> getPerms(Rank rank) {
        LinkedList<String> perms = new LinkedList<>();

        for (String s : rank.permissions) {
            perms.add(s);
        }

        return perms;
    }

    public static void updateRang(final UUID uuid) {
        if (uuid != null) {
            Document document = MongoDB.getCollection("Rang").getStructure().find(new Document("uuid", uuid.toString())).first();
            if (document == null) {
                String rang = Rank.USER.name;
                MongoDB.getCollection("Rang").getStructure().insertOne(new Document("uuid", uuid.toString()).append("rang", rang));
                Rank normalrank = Rank.USER;
                for (Rank rank2 : Rank.values()) {
                    if (rank2.toString().equalsIgnoreCase(rang)) {
                        normalrank = rank2;
                        break;
                    }
                }

                final Rank norrang = normalrank;
                final ArrayList<Rank> setrank = new ArrayList<Rank>();
                setrank.add(norrang);

                MongoDB.getCollection("TempRang").getStructure().find(new Document("uuid", uuid.toString())).forEach((Block<Document>) document2 -> {

                    if (System.currentTimeMillis() - document2.getLong("time") < 0) {
                        for (Rank rank2 : Rank.values()) {
                            if (rank2.toString().equalsIgnoreCase(document2.getString("rang"))) {
                                if (setrank.get(0) == null) {
                                    setrank.clear();
                                    setrank.add(norrang);
                                }

                                if (rank2.getId() > setrank.get(0).getId()) {
                                    setrank.clear();
                                    setrank.add(rank2);
                                }
                            }
                        }
                    }

                });
                return;
            }
            String rang = document.getString("rang");
            if (rang == null) {
                rang = Rank.USER.name;
            }

            Rank normalrank = Rank.USER;
            for (Rank rank2 : Rank.values()) {
                if (rank2.toString().equalsIgnoreCase(rang)) {
                    normalrank = rank2;
                    break;
                }
            }

            final Rank norrang = normalrank;
            final ArrayList<Rank> setrank = new ArrayList<Rank>();
            setrank.add(norrang);

            MongoDB.getCollection("TempRang").getStructure().find(new Document("uuid", uuid.toString())).forEach((Block<Document>) document2 -> {

                if (System.currentTimeMillis() - document2.getLong("time") < 0) {
                    for (Rank rank2 : Rank.values()) {
                        if (rank2.toString().equalsIgnoreCase(document2.getString("rang"))) {
                            if (setrank.get(0) == null) {
                                setrank.clear();
                                setrank.add(norrang);
                            }

                            if (rank2.getId() > setrank.get(0).getId()) {
                                setrank.clear();
                                setrank.add(rank2);
                            }
                        }
                    }
                }

            });
        }
    }

    public static boolean hasMinimumRank(UUID uuid, Rank rank) {
        if (!rang.containsKey(uuid)) {
            updateRang(uuid);
        }
        long time = System.currentTimeMillis();
        while (!rang.containsKey(uuid) && System.currentTimeMillis() - time <= 1000) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int current = rang.containsKey(uuid) ? rang.get(uuid).getId() : Rank.USER.getId();
        int needed = rank.getId();
        return current >= needed;
    }

    public static Rank getRang(UUID uuid) {
        if (!rang.containsKey(uuid)) {
            updateRang(uuid);
        }
        long time = System.currentTimeMillis();

        while (!rang.containsKey(uuid) && System.currentTimeMillis() - time < 1000) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rang.containsKey(uuid) ? rang.get(uuid) : Rank.USER;
    }

    public static void setRank(UUID uuid, Rank newrank) {
        Document document = MongoDB.getCollection("Rang").getStructure().find(new Document("uuid", uuid.toString())).first();
        if (document == null) {
            MongoDB.getCollection("Rang").getStructure().insertOne(new Document("uuid", uuid.toString()).append("rang", newrank));
            return;
        }
        Document doc = document.append("rang", newrank.name);
        MongoDB.getCollection("Rang").getStructure().findOneAndReplace(new Document("uuid", uuid.toString()), doc);
    }

    public static void setTempRank(UUID uuid, Rank newrank, int time) {
        Document doc = new Document("uuid", uuid.toString())
                .append("rang", newrank.name)
                .append("rtime", time)
                .append("id", UUID.randomUUID().toString());
        MongoDB.getCollection("TempRang").getStructure().insertOne(doc);
    }

    public void permissionAkt(Player player) {

        LinkedList<String> perms = new LinkedList<>();

        final boolean[] b = {false};

        Rank rank = getRang(player.getUniqueId());

        for (int rid = Rank.USER.id; rid <= rank.id; rid++) {
            Rank r = null;
            for (Rank r2 : Rank.values()) {
                if (r2.id == rid) r = r2;
            }
            if (r != null) {
                for (String s : getPerms(r)) {
                    perms.add(s);
                }
            }
        }

        MongoDB.getCollection("Permission").getStructure().find(new Document("key", player.getUniqueId().toString())).forEach((Block<Document>) document -> perms.add(document.getString("perm")));

        while (!b[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!attachment.containsKey(player.getUniqueId())) {
            PermissionAttachment attach = player.addAttachment(PermsSystem.getPlugin(PermsSystem.class));
            attachment.put(player.getUniqueId(), attach);
        } else {
            player.removeAttachment(attachment.get(player.getUniqueId()));
            PermissionAttachment attach = player.addAttachment(PermsSystem.getPlugin(PermsSystem.class));
            attachment.put(player.getUniqueId(), attach);
        }
        PermissionAttachment attach = attachment.get(player.getUniqueId());

        if (perms.contains("-*")) {
            attach.setPermission("*", false);
            for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
                attach.setPermission(perm, false);
            }
            attach.setPermission("minecraft.*", false);
            attach.setPermission("perms.*", false);
        }

        for (String s : perms) {
            if (!s.equalsIgnoreCase("*") && !s.equalsIgnoreCase("-*")) {
                if (s.startsWith("-")) {
                    attach.setPermission(s.replace("-", ""), false);
                } else {
                    attach.setPermission(s, true);
                }
            }
        }

        if (perms.contains("*")) {
            attach.setPermission("*", true);
            for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
                attach.setPermission(perm, true);
            }
            attach.setPermission("minecraft.*", true);
            attach.setPermission("perms.*", true);
        }

        if (!allowCommands.contains(player.getUniqueId())) allowCommands.add(player.getUniqueId());

    }

}

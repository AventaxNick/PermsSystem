package de.aventax.api;

import lombok.Getter;

import java.util.LinkedList;

@Getter
public enum Rank {

    ADMIN("§4", "§4Admin | ", "§4Admin | ", "§a§l", 18, 18),
    SRDEVELOPER("§b", "§bSrDeveloper | ", "§bSrDeveloper | ", "§a§l", 17, 27),
    DEVELOPER("§b", "§bDeveloper | ", "§bDeveloper | ", "§a§l", 16, 27),
    TESTDEVELOPER("§b", "§bDeveloper | ", "§bDeveloper | ", "§a§l", 15, 27),
    SRMODERATOR("§2", "§2SrModerator | ", "§2SrModerator | ", "§c§l", 14, 12),
    MODERATOR("§2", "§2Moderator | ", "§2Moderator | ", "§c§l", 13, 12),
    CONTENT("§c", "§cContent | ", "§cContent | ", "§b§l", 12, 12),
    SUPPORTER("§a", "§aSupporter | ", "§aSupporter | ", "§b§l", 11, 12),
    BUILDER("§3", "§3Builder | ", "§3Builder | ", "§9§l", 10, 13),
    DESIGNER("§1", "§1Designer | ", "§1Designer | ", "§d§l", 9, 13),
    YOUTUBER("§5", "§5YT§4+§5 | ", "§5YT §7• §f", "§b§l", 8, 23),
    FREUND("§9", "§9Freund | ", "§9Freund | ", "§d§l", 7, 41),
    SUPERHELD("§e", "§eSuperheld | ", "§eSuperheld | ", "§d§l", 6, 28),
    BOSS("§d", "§dBoss | ", "§dBoss | ", "§1§l", 5, 28),
    CREATOR("§5", "§5Creator | ", "§5Creator | ", "§b", 4, 28),
    CHIEF("§c", "§cChief | ", "§cChief | ", "§e", 3, 28),
    GAMER("§6", "§6Gamer | ", "§6Gamer | ", "§7", 2, 28),
    USER("§7", "§7Spieler | ", "§7Spieler | ", "§7", 1, 26);

    public final String color;
    public final String prefix;
    public final String chatPrefix;
    public final int id;
    public final int weight;
    public final LinkedList<String> permissions = new LinkedList<>();
    public final String name;

    Rank(String color, String chatPrefix, String prefix, String tabColor, int id, int weight) {
        this.chatPrefix = chatPrefix;
        this.color = color;
        this.prefix = prefix;
        this.id = id;
        this.weight = weight;
        this.name = name();
    }

    public int getId() {
        return this.id;
    }

}

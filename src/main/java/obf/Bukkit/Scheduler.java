package obf.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {

    public static BukkitTask runTask(Plugin plugin, Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, () -> runnable.run());
    }

    public static BukkitTask runTaskTimer(Plugin plugin, Runnable runnable, long a, long b) {
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> runnable.run(), a, b);
    }

    public static BukkitTask runTaskLater(Plugin plugin, Runnable runnable, long a) {
        return Bukkit.getScheduler().runTaskLater(plugin, () -> runnable.run(), a);
    }

    public static BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long a, long b) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> runnable.run(), a, b);
    }

    public static BukkitTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> runnable.run());
    }

    public static BukkitTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long a) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> runnable.run(), a);
    }

}

package me.catst0day.capi.Schedulers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CAPIMainScheduler {

    public static void runTask(Plugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static CompletableFuture<Void> runTaskAsync(Plugin plugin, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static BukkitTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static void runTaskTimer(Plugin plugin, Consumer<BukkitTask> taskConsumer, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(plugin, taskConsumer, delay, period);
    }
    public static BukkitTask scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    public static void runAtLocation(Plugin plugin, Location loc, Runnable runnable) {
        if (isLocationLoaded(loc)) {
            runTask(plugin, runnable);
        } else if (loc != null) {
            plugin.getLogger().warning("Task skipped: Chunk not loaded at " + loc.getBlockX() + "," + loc.getBlockZ());
        }
    }

    public static void runAtEntity(Plugin plugin, Entity entity, Runnable runnable) {
        if (entity != null && entity.isValid()) {
            runAtLocation(plugin, entity.getLocation(), runnable);
        }
    }

    public static boolean isLocationLoaded(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        return loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    @Deprecated
    public static int runTaskLater(Runnable runnable, long delay, Plugin plugin) {
        return runTaskLater(plugin, runnable, delay).getTaskId();
    }
}
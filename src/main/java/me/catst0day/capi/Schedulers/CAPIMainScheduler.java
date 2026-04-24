package me.catst0day.capi.Schedulers;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CAPIMainScheduler {

    private static BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }

    // --- Базовые методы запуска ---

    public static void runTask(Plugin plugin, Runnable runnable) {
        getScheduler().runTask(plugin, runnable);
    }

    public static CompletableFuture<Void> runTaskAsync(Plugin plugin, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static int runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return getScheduler().runTaskLater(plugin, runnable, delay).getTaskId();
    }

    public static int runLaterAsync(Plugin plugin, Runnable runnable, long delay) {
        return getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay).getTaskId();
    }

    public static int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
        return getScheduler().runTaskTimer(plugin, runnable, delay, period).getTaskId();
    }

    public static int runTimerAsync(Plugin plugin, Runnable runnable, long delay, long period) {
        return getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period).getTaskId();
    }

    // --- Методы с проверкой локации (Безопасность чанков) ---

    public static void runAtLocation(Plugin plugin, Location location, Runnable runnable) {
        World world = location.getWorld();
        if (world != null && world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            runTask(plugin, runnable);
        } else {
            plugin.getLogger().warning("Task skipped: Chunk at " + location.toVector() + " is not loaded.");
        }
    }

    public static void runAtLocation(Plugin plugin, Chunk chunk, Runnable runnable) {
        if (chunk.isLoaded()) {
            runTask(plugin, runnable);
        } else {
            plugin.getLogger().warning("Task skipped: Chunk [" + chunk.getX() + "," + chunk.getZ() + "] is not loaded.");
        }
    }

    public static int runAtLocationLater(Plugin plugin, Location location, Runnable runnable, long delay) {
        return runTaskLater(plugin, () -> runAtLocation(plugin, location, runnable), delay);
    }

    public static int runAtLocationTimer(Plugin plugin, Location location, Runnable runnable, long delay, long period) {
        return scheduleSyncRepeatingTask(plugin, () -> runAtLocation(plugin, location, runnable), delay, period);
    }

    // --- Методы для работы с сущностями ---

    public static void runAtEntity(Plugin plugin, Entity entity, Runnable runnable) {
        if (entity != null && entity.isValid()) {
            runAtLocation(plugin, entity.getLocation(), runnable);
        }
    }

    public static void runAtEntityWithFallback(Plugin plugin, Entity entity, Runnable runnable, Runnable fallback) {
        if (entity != null && entity.isValid() && entity.getLocation().getChunk().isLoaded()) {
            runAtEntity(plugin, entity, runnable);
        } else {
            runTask(plugin, fallback);
        }
    }

    public static int runAtEntityLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
        return runTaskLater(plugin, () -> runAtEntity(plugin, entity, runnable), delay);
    }

    public static int runAtEntityTimer(Plugin plugin, Entity entity, Runnable runnable, long delay, long period) {
        return scheduleSyncRepeatingTask(plugin, () -> runAtEntity(plugin, entity, runnable), delay, period);
    }

    @Deprecated(since = "1.0.2.133-021-U")
    public static int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period, Plugin plugin) {
        return scheduleSyncRepeatingTask(plugin, runnable, delay, period);
    }

    @Deprecated
    public static int runTaskLater(Runnable runnable, long delay, Plugin plugin) {
        return runTaskLater(plugin, runnable, delay);
    }
}
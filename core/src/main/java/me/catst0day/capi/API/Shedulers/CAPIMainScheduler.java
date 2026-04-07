package me.catst0day.capi.API.Shedulers;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
public class CAPIMainScheduler {

    private static BukkitScheduler getScheduler() {
        return org.bukkit.Bukkit.getScheduler();
    }

    @Deprecated
    public static CompletableFuture<Void> runTask(Runnable runnable, Plugin plugin) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getScheduler().runTask(plugin, task -> {
            runnable.run();
            future.complete(null);
        });
        return future;
    }

    @Deprecated
    public static CompletableFuture<Void> runTaskAsynchronously(Runnable runnable, Plugin plugin) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getScheduler().runTaskAsynchronously(plugin, task -> {
            runnable.run();
            future.complete(null);
        });
        return future;
    }

    @Deprecated
    public static int runTaskLater(Runnable runnable, long delay, Plugin plugin) {
        return getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    @Deprecated
    public static int runLaterAsync(Runnable runnable, long delay, Plugin plugin) {
        return getScheduler().scheduleAsyncDelayedTask(plugin, runnable, delay);
    }

    @Deprecated
    public static int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period, Plugin plugin) {
        return getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, period);
    }

    @Deprecated
    public static int runTimerAsync(Runnable runnable, long delay, long period, Plugin plugin) {
        return getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, delay, period);
    }

    // Публичные методы с явным указанием плагина
    public static void runTask(Plugin plugin, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getScheduler().runTask(plugin, task -> {
            runnable.run();
            future.complete(null);
        });
    }

    public static CompletableFuture<Void> runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getScheduler().runTaskAsynchronously(plugin, task -> {
            runnable.run();
            future.complete(null);
        });
        return future;
    }

    public static int runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    public static int runLaterAsync(Plugin plugin, Runnable runnable, long delay) {
        return getScheduler().scheduleAsyncDelayedTask(plugin, runnable, delay);
    }

    public static int scheduleSyncRepeatingTask(Plugin plugin, Runnable runnable, long delay, long period) {
        return getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, period);
    }

    public static int runTimerAsync(Plugin plugin, Runnable runnable, long delay, long period) {
        return getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, delay, period);
    }


    public static void runAtLocation(Plugin plugin, Location location, Runnable runnable) {
        World world = location.getWorld();
        if (world != null && world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            runTask(plugin, runnable);
        } else {
            plugin.getLogger().warning("Cannot run task at location: chunk not loaded");
        }
    }

    public static void runAtLocation(Plugin plugin, Chunk chunk, Runnable runnable) {
        if (chunk.isLoaded()) {
            runTask(plugin, runnable);
        } else {
            plugin.getLogger().warning("Cannot run task at chunk: chunk not loaded");
        }
    }

    public static void runAtLocation(Plugin plugin, World world, int x, int z, Runnable runnable) {
        if (world.isChunkLoaded(x, z)) {
            runTask(plugin, runnable);
        } else {
            plugin.getLogger().warning("Cannot run task at coordinates: chunk not loaded");
        }
    }

    public static int runAtLocationLater(Plugin plugin, Location location, Runnable runnable, long delay) {
        return runTaskLater(plugin, () -> runAtLocation(plugin, location, runnable), delay);
    }

    public static int runAtLocationTimer(Plugin plugin, Location location, Runnable runnable, long delay, long period) {
        return scheduleSyncRepeatingTask(plugin, () -> runAtLocation(plugin, location, runnable), delay, period);
    }

    // Методы для работы с сущностями
    public static void runAtEntity(Plugin plugin, Entity entity, Runnable runnable) {
        Location location = entity.getLocation();
        runAtLocation(plugin, location, runnable);
    }

    public static void runAtEntityWithFallback(Plugin plugin, Entity entity, Runnable runnable, Runnable fallback) {
        try {
            runAtEntity(plugin, entity, runnable);
        } catch (Exception e) {
            runTask(plugin, fallback);
        }
    }

    public static int runAtEntityLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
        return runTaskLater(plugin, () -> runAtEntity(plugin, entity, runnable), delay);
    }

    public static int runAtEntityTimer(Plugin plugin, Entity entity, Runnable runnable, long delay, long period) {
        return scheduleSyncRepeatingTask(plugin, () -> runAtEntity(plugin, entity, runnable), delay, period);
    }
}
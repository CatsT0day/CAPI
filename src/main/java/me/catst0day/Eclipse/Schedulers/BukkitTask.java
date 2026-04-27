
package me.catst0day.Eclipse.Schedulers;

import org.bukkit.plugin.Plugin;

public class BukkitTask implements EclipseTask {
    private final org.bukkit.scheduler.BukkitTask task;
    private final Plugin plugin;

    public BukkitTask(org.bukkit.scheduler.BukkitTask task, Plugin plugin) {
        this.task = task;
        this.plugin = plugin;
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}

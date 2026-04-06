
package me.catst0day.capi.Shedulers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.Plugin;

public class BukkitCAPITask implements CAPITask {
    private final BukkitTask task;
    private final Plugin plugin;

    public BukkitCAPITask(BukkitTask task, Plugin plugin) {
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

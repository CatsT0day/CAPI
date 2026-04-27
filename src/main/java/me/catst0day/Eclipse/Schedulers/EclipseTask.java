package me.catst0day.Eclipse.Schedulers;

import org.bukkit.plugin.Plugin;

public interface EclipseTask {
    boolean isCancelled();
    void cancel();
    Plugin getPlugin();
}
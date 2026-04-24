package me.catst0day.capi.Schedulers;

import org.bukkit.plugin.Plugin;

public interface CAPITask {
    boolean isCancelled();
    void cancel();
    Plugin getPlugin();
}
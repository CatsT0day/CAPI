package me.catst0day.capi.Shedulers;

import org.bukkit.plugin.Plugin;

public interface CAPITask {
    boolean isCancelled();
    void cancel();
    Plugin getPlugin();
}

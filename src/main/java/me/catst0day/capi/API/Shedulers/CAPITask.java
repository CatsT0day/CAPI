package me.catst0day.capi.API.Shedulers;

import org.bukkit.plugin.Plugin;

public interface CAPITask {
    boolean isCancelled();
    void cancel();
    Plugin getPlugin();
}
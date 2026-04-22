package me.catst0day.capi.Commands.commandAPI;

import org.bukkit.command.CommandSender;
import me.catst0day.capi.CAPI;

public interface Cmd {
    boolean perform(CAPI plugin, CommandSender sender, String[] args);
    void get(CAPI plugin);
}
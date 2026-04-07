package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Day extends CommandTemplate {
    public Day(CAPI plugin) {
        super(plugin, "night", List.of(), "CAPI.command.time", false, 0);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        player.getWorld().setTime(18000);
        sender.sendMessage(plugin.getMessage("nightSet"));
        return true;
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }
}
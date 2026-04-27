package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Day extends CommandTemplate {

    public Day(Eclipse plugin) {
        super(plugin, "day", List.of(), EclipsePermissionManager.CAPIPermissions.DAY, true, 60, "Set day time");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (player == null) return false;

        try {
            player.getWorld().setTime(0);
            plugin.sendCFGmessage(sender, plugin.getMessage("daySet"));
            return true;
        } catch (Exception e) {
            plugin.sendCFGmessage(sender, plugin.getMessage("commandError"));
            return false;
        }
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
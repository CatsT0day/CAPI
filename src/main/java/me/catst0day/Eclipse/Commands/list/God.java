package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class God extends CommandTemplate {

    public God(Eclipse plugin) {
        super(plugin, "god", List.of(), EclipsePermissionManager.CAPIPermissions.GOD, true, 0, "Toggle god mode");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        plugin.toggleGodMode(player, args);
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
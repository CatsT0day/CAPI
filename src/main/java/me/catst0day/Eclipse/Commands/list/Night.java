package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Night extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Night(Eclipse plugin) {
        super(plugin, "night", List.of(), EclipsePermissionManager.CAPIPermissions.NIGHT, false, 0L, "set nithgt time");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.NIGHT);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        player.getWorld().setTime(18000);
        sender.sendMessage(plugin.getMessage("nightSet"));
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
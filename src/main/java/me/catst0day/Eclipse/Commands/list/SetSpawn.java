package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawn extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public SetSpawn(Eclipse plugin) {
        super(plugin, "setspawn", List.of("ss"), EclipsePermissionManager.CAPIPermissions.SETSPAWN, true, 0L, "set a new spwn");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.SETSPAWN);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (!hasPermission((CommandSender) player, args)) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        Location loc = player.getLocation();
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.getConfig().set("spawn.yaw", loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();

        player.sendMessage(plugin.getMessage("spawnSet"));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }
}
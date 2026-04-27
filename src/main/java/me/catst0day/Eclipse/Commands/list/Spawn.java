
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Spawn extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Spawn(Eclipse plugin) {
        super(plugin, "spawn", List.of(), EclipsePermissionManager.CAPIPermissions.SPAWN, true, 5L, "tp to spawn");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.SPAWN, "teleport to spawn");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (!hasPermission((CommandSender) player, args)) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        String worldName = plugin.getConfig().getString("spawn.world");
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cSpawn world not found!");
            return true;
        }

        Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        player.teleport(spawnLocation);
        player.sendMessage(plugin.getMessage("spawnTeleportSuccess"));

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
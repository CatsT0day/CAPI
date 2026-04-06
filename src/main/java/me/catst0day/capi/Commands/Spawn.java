package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {
    private final CatAPI plugin;

    public Spawn(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (!player.hasPermission("catapi.spawn")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        // Получаем данные спавна из конфига
        String worldName = plugin.getConfig().getString("spawn.world");
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cМир спавна не найден!");
            return true;
        }

        Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        player.teleport(spawnLocation);
        player.sendMessage(plugin.getMessage("spawnTeleportSuccess"));

        return true;
    }
}

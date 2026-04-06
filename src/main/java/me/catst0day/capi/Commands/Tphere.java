
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tphere implements CommandExecutor {
    private final CatAPI plugin;

    public Tphere(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (!player.hasPermission("catapi.tphere")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        // Проверка аргументов
        if (args.length != 1) {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/tphere <игрок>"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Проверка, что игрок онлайн
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        // Телепортация игрока к отправителю
        target.teleport(player.getLocation());

        // Сообщения для обоих игроков
        player.sendMessage(plugin.getMessage("tphereSuccess").replace("%s", target.getName()));
        target.sendMessage(plugin.getMessage("tphereToYouSuccess").replace("%s", player.getName()));

        return true;
    }
}

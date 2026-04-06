package me.catst0day.capi.Commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Near implements CommandExecutor {

    private final JavaPlugin plugin;

    public Near(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверка, что команду вызвал игрок
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.noPermission")));
            return true;
        }

        Player player = (Player) sender;

        // Установка радиуса по умолчанию
        double radius = 10.0;

        // Проверка, указан ли радиус в аргументах
        if (args.length > 0) {
            try {
                radius = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.ivalidRadius")));
                return true;
            }
        }

        // Проверка на разрешения
        if (!player.hasPermission("catAPI.near.max.radius." + (int) radius)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.CannotUseRadius")));
            return true;
        }

        // Получение списка игроков рядом
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getLocation().distance(player.getLocation()) <= radius) {
                nearbyPlayers.add(onlinePlayer);
            }
        }

        // Формирование сообщения
        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.NoOneAtNear")));
        } else {
            StringBuilder playersList = new StringBuilder();
            for (Player nearbyPlayer : nearbyPlayers) {
                int distance = (int) player.getLocation().distance(nearbyPlayer.getLocation());
                playersList.append(nearbyPlayer.getName()).append(" (").append(distance).append(" блоков) ");
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nearPlayers"))
                    .replace("{radius}", String.valueOf(radius))
                    .replace("{players}", playersList.toString()));
        }

        return true;
    }
}

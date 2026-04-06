package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload implements CommandExecutor {
    private final CatAPI plugin;

    public Reload(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player) || !player.isOp() && !player.hasPermission("catapi.reload")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/CatAPI reload"));
            return true;
        }

        try {
            plugin.reloadConfig();
            player.sendMessage(plugin.getMessage("configReloaded"));
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Ошибка при перезагрузке конфига: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
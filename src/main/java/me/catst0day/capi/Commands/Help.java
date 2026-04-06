package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Help implements CommandExecutor {
    private final CatAPI plugin;

    public Help(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender snr, Command cmd, String label, String[] args) {
        if (!(snr instanceof Player)) {
            snr.sendMessage(plugin.getMessage("noPermission"));
            return false;
        }


        List<String> helpMessages = plugin.getConfig().getStringList("messages.help");
        if (helpMessages == null || helpMessages.isEmpty()) {
            snr.sendMessage(ChatColor.RED + "Помощь недоступна: конфигурация не загружена.");
            return true;
        }
        for (String line : helpMessages) {
            String coloredLine = ChatColor.translateAlternateColorCodes('&', line);
            snr.sendMessage(coloredLine);
        }

        return true;
    }
}
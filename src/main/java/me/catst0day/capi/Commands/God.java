
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class God implements CommandExecutor {
    private final CatAPI plugin;

    public God(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("catapi.god")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }
        Player Sender = (Player) sender;

        plugin.toggleGodMode(Sender, args);
        return true;
    }
}
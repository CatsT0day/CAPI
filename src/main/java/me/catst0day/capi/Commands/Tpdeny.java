
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class Tpdeny implements CommandExecutor {
    private final CatAPI plugin;

    public Tpdeny(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (!plugin.getTpaRequests().containsValue(playerId)) {
            player.sendMessage(plugin.getMessage("noPendingRequests"));
            return true;
        }

        for (UUID requester : plugin.getTpaRequests().keySet()) {
            if (plugin.getTpaRequests().get(requester).equals(playerId)) {
                Player requesterPlayer = Bukkit.getPlayer(requester);
                if (requesterPlayer != null) {
                    plugin.getTpaRequests().remove(requester);
                    player.sendMessage(plugin.getMessage("tpaDenied").replace("%player%", requesterPlayer.getName()));
                    requesterPlayer.sendMessage(plugin.getMessage("tpaRequestDenied").replace("%player%", player.getName()));
                }
                break;
            }
        }
        return true;
    }
}
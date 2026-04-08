package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tpdeny extends CommandTemplate {
    public Tpdeny(CAPI plugin) {
        super(plugin, "tpdeny", List.of(), "catapi.tpdeny", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
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

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return new ArrayList<>();
    }
}

package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class Near extends CommandTemplate {

    public Near(CAPI plugin) {
        super(plugin, "near", List.of(), "catapi.near", true, 20L);
        setTabCompleteArguments(List.of("10", "20", "50", "100"));
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        double radius = 10.0;

        if (args.length > 0) {
            try {
                radius = Double.parseDouble(args[0]);
                if (radius <= 0 || radius > 100) {
                    player.sendMessage(plugin.getMessage("InvalidRadius"));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getMessage("InvalidRadius"));
                return true;
            }
        }


        String permission = "catapi.near.radius." + (int) radius;
        if (!player.hasPermission(permission)) {
            player.sendMessage(plugin.getMessage("CannotUseRadius"));
            return true;
        }

        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getLocation().distance(player.getLocation()) <= radius) {
                nearbyPlayers.add(onlinePlayer);
            }
        }

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(plugin.getMessage("NoOneAtNear"));
        } else {
            StringBuilder playersList = new StringBuilder();
            for (Player nearbyPlayer : nearbyPlayers) {
                int distance = (int) player.getLocation().distance(nearbyPlayer.getLocation());
                playersList.append(nearbyPlayer.getName())
                        .append(" (")
                        .append(distance)
                        .append(" ) ");
            }
            player.sendMessage(plugin.getMessage("nearPlayers")
                    .replace("{radius}", String.valueOf(radius))
                    .replace("{players}", playersList.toString()));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return filterByInput(args[0], tabCompleteArguments);
        }
        return null;
    }
}
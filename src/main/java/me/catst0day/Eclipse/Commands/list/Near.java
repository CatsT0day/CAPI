package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Near extends CommandTemplate {

    public Near(Eclipse plugin) {
        super(plugin, "near", List.of(), CAPIPermissions.NEAR, true, 0, "Find nearby players");
        setTabCompleteArguments(List.of("10", "20", "50", "100"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (player == null) return true;

        double radius = 10.0;
        if (args.length > 0) {
            try {
                radius = Double.parseDouble(args[0]);
                if (radius <= 0 || radius > 100) {
                    player.sendMessage(plugin.getMessage("InvalidRadius"));
                    return true;
                }

                if (!plugin.getPermissionManager().hasPermission(player, CAPIPermissions.NEAR_RADIUS, String.valueOf((int) radius))) {
                    player.sendMessage(plugin.getMessage("CannotUseRadius"));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getMessage("InvalidRadius"));
                return true;
            }
        }

        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getWorld().equals(player.getWorld())
                    && onlinePlayer.getLocation().distance(player.getLocation()) <= radius) {
                nearbyPlayers.add(onlinePlayer);
            }
        }

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(plugin.getMessage("NoOneAtNear"));
        } else {
            StringBuilder playersList = new StringBuilder();
            for (Player nearbyPlayer : nearbyPlayers) {
                int distance = (int) player.getLocation().distance(nearbyPlayer.getLocation());
                playersList.append(nearbyPlayer.getName()).append(" (").append(distance).append(") ");
            }
            player.sendMessage(plugin.getMessage("nearPlayers")
                    .replace("{radius}", String.valueOf(radius))
                    .replace("{players}", playersList.toString()));
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String r : List.of("10", "20", "50", "100")) {
                if (r.startsWith(prefix)) completions.add(r);
            }
        }
        return completions;
    }
}

package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class Near extends CommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Near(CAPI plugin) {
        super(plugin, "near", List.of(), CAPIPermissionManager.CAPIPerm.NEAR, true, 20L, "see plrs nearby");
        this.permissionManager = plugin.getPermissionManager();
        setTabCompleteArguments(List.of("10", "20", "50", "100"));
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        // Базовое разрешение на использование команды /near
        if (!permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.NEAR)) {
            return false;
        }

        // Если указан радиус, проверяем разрешение для этого радиуса
        if (args.length > 0) {
            try {
                int radius = Integer.parseInt(args[0]);
                if (radius > 0 && radius <= 100) {
                    if (!permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.NEAR_RADIUS, String.valueOf(radius))) {
                        return false;
                    }
                }
                } catch (NumberFormatException e) {
                    // Если аргумент не число, разрешение не проверяем
                }
            }

            return true;
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
                            .append(") ");
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
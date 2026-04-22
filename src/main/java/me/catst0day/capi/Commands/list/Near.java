package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

@CAPICommandAnnotation(
        name = "near",
        aliases = {"nearby"},
        permission = CAPIPerm.NEAR,
        requirePlayer = true,
        cooldownSeconds = 20,
        description = "See players nearby"
)
public class Near extends CAPICommandTemplate {

    private String noOneAtNearMsg;
    private String invalidRadiusMsg;
    private String nearPlayersMsg;
    private String cannotUseRadiusMsg;

    public Near(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of("10", "20", "50", "100"));
    }

    @Override
    public void get(CAPI plugin) {
        noOneAtNearMsg = plugin.getMessage("NoOneAtNear");
        invalidRadiusMsg = plugin.getMessage("InvalidRadius");
        nearPlayersMsg = plugin.getMessage("nearPlayers");
        cannotUseRadiusMsg = plugin.getMessage("CannotUseRadius");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        double radius = 10.0;

        if (args.length > 0) {
            try {
                radius = Double.parseDouble(args[0]);
                if (radius <= 0 || radius > 100) {
                    player.sendMessage(invalidRadiusMsg);
                    return true;
                }

                // Проверка прав на радиус
                if (!plugin.getPermissionManager().hasPermission(player, CAPIPerm.NEAR_RADIUS, String.valueOf((int) radius))) {
                    player.sendMessage(cannotUseRadiusMsg);
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(invalidRadiusMsg);
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
            player.sendMessage(noOneAtNearMsg);
        } else {
            StringBuilder playersList = new StringBuilder();
            for (Player nearbyPlayer : nearbyPlayers) {
                int distance = (int) player.getLocation().distance(nearbyPlayer.getLocation());
                playersList.append(nearbyPlayer.getName())
                        .append(" (")
                        .append(distance)
                        .append(") ");
            }
            player.sendMessage(nearPlayersMsg
                    .replace("{radius}", String.valueOf(radius))
                    .replace("{players}", playersList.toString()));
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> possibleRadii = List.of("10", "20", "50", "100");
            for (String radius : possibleRadii) {
                if (radius.startsWith(prefix)) {
                    completions.add(radius);
                }
            }
        }
        return completions;
    }
}
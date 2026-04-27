package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tpdeny extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Tpdeny(Eclipse plugin) {
        super(plugin, "tpdeny", List.of("teleportdeny"), EclipsePermissionManager.CAPIPermissions.TPA, true, 0L, "deny tp request");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.TPA);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

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

                    player.sendMessage(plugin.getMessage("tpaDenied")
                            .replace("%player%", requesterPlayer.getName()));
                    requesterPlayer.sendMessage(plugin.getMessage("tpaRequestDenied")
                            .replace("%player%", player.getName()));
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return new ArrayList<>();
    }
}
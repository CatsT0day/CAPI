package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tpaaccept extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Tpaaccept(Eclipse plugin) {
        super(plugin, "tpaccept", List.of("teleportaccept"), EclipsePermissionManager.CAPIPermissions.TPA, true, 0L, "accept tp request");
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
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        UUID playerId = player.getUniqueId();
        EclipsePlr plr = plugin.getPlayer(playerId);
        UUID requesterId = null;
        for (UUID key : plugin.getTpaRequests().keySet()) {
            if (plugin.getTpaRequests().get(key).equals(playerId)) {
                requesterId = key;
                break;
            }
        }

        if (requesterId == null) {
            sender.sendMessage(plugin.getMessage("noPendingRequests"));
            return true;
        }

        Player requesterPlayer = Bukkit.getPlayer(requesterId);
        if (requesterPlayer == null) {
            sender.sendMessage(plugin.getMessage("requesterOffline"));
            plugin.getTpaRequests().remove(requesterId);
            return true;
        }
        plugin.getTpaRequests().remove(requesterId);
        sender.sendMessage(plugin.getMessage("tpaAccepted")
                .replace("%player%", requesterPlayer.getName()));
        requesterPlayer.sendMessage(plugin.getMessage("tpaRequestAccepted")
                .replace("%player%", player.getName()));
        plr.teleportAsynchronously(requesterPlayer);

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return new ArrayList<>();
    }
}
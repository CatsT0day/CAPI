package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tpaaccept extends CommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Tpaaccept(CAPI plugin) {
        super(plugin, "tpaccept", List.of("teleportaccept"), CAPIPermissionManager.CAPIPerm.TPA, true, 0L, "accept tp request");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.TPA);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return execute((CommandSender) player, player, args);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        UUID playerId = player.getUniqueId();

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

        // Получаем локацию игрока, который отправил запрос
        Location targetLocation = requesterPlayer.getLocation();

        // Удаляем запрос из списка
        plugin.getTpaRequests().remove(requesterId);

        // Отправляем сообщения обоим игрокам
        sender.sendMessage(plugin.getMessage("tpaAccepted")
                .replace("%player%", requesterPlayer.getName()));
        requesterPlayer.sendMessage(plugin.getMessage("tpaRequestAccepted")
                .replace("%player%", player.getName()));

        // Используем единый метод телепортации с задержкой и боссбаром
        plugin.teleport(player, targetLocation);

        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return new ArrayList<>();
    }
}
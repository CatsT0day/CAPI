
package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tpaaccept extends CommandTemplate {
    public Tpaaccept(CAPI plugin) {
        super(plugin, "tpaccept",  List.of(),"catapi.tpaccept", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return execute((CommandSender) player, player, args);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
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
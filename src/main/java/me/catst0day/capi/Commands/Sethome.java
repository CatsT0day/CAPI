package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Sethome extends CommandTemplate {

    public Sethome(CAPI plugin) {
        super(plugin, "sethome", List.of(), "catapi.home.set", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return execute((CommandSender) player, player, args);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission("catapi.home.set")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("sethomeUsage"));
            return true;
        }

        String homeName = args[0];
        Location location = player.getLocation();

        if (plugin.getInstance().getHomeManager().setHome(player.getUniqueId(), homeName, location)) {
            sender.sendMessage(plugin.getMessage("homeSet")
                    .replace("{homename}", homeName));
        } else {
            sender.sendMessage(plugin.getMessage("homeSetFailed"));
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return new ArrayList<>();
    }
}
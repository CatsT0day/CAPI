package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Tphere extends CommandTemplate {
    public Tphere(CAPI plugin) {
        super(plugin, "tphere", List.of(), "catapi.tphere", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/tphere <игрок>"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Проверка, что игрок онлайн
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        target.teleport(player.getLocation());
        sender.sendMessage(plugin.getMessage("tphereSuccess").replace("%s", target.getName()));
        target.sendMessage(plugin.getMessage("tphereToYouSuccess").replace("%s", player.getName()));

        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(getOnlinePlayerNames(args[0]));
        } else if (args.length > 1) {
            completions.clear();
        }

        return completions;
    }
}
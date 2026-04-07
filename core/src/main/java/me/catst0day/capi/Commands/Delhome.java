package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Delhome extends CommandTemplate {

    public Delhome(CAPI plugin) {
        super(plugin, "delhome", List.of(), "catapi.home.delete", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return execute((CommandSender) player, player, args);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission("catapi.home.delete")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("delhomeUsage"));
            return true;
        }

        String homeName = args[0];

        if (plugin.getInstance().getHomeManager().deleteHome(player.getUniqueId(), homeName)) {
            sender.sendMessage(plugin.getMessage("homeDeleted")
                    .replace("{homename}", homeName));
        } else {
            sender.sendMessage(plugin.getMessage("homeDeleteFailed")
                    .replace("{homename}", homeName));
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        try {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                List<String> playerHomes = plugin.getInstance().getHomeManager().getPlayerHomes(player.getUniqueId());
                for (String home : playerHomes) {
                    if (home.toLowerCase().startsWith(prefix)) {
                        completions.add(home);
                    }
                }
                completions.sort(String.CASE_INSENSITIVE_ORDER);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка автодополнения для команды /delhome: " + e.getMessage());
        }

        return completions;
    }
}
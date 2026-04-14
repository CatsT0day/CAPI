package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Delhome extends CommandTemplate {

    public Delhome(CAPI plugin) {
        super(plugin, "delhome", List.of("dhome", "dh"), CAPIPermissionManager.CAPIPerm.HOME, true, 0L, "delete your existing homes");
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
            plugin.sendCFGmessage(sender, "Usage".formatted("/delhome <homename>"));
            return true;
        }

        String homeName = args[0];

        if (CAPI.getInstance().getHomeManager().deleteHome(player.getUniqueId(), homeName)) {
            plugin.sendCFGmessage(sender, "homeDeleted")
                    .replace("{homename}", homeName);
        } else {
            plugin.sendCFGmessage(sender, "homeDeleteFailed")
                    .replace("{homename}", homeName);
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        try {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                List<String> playerHomes = CAPI.getInstance().getHomeManager().getPlayerHomes(player.getUniqueId());
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
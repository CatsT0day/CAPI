package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Delhome extends CAPICommandTemplate {

    public Delhome(CAPI plugin) {
        super(plugin, "delhome", List.of("remhome"), CAPIPermissionManager.CAPIPerm.HOME, true, 0, "del home");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length != 1) {
            plugin.sendCFGmessage(sender, plugin.getMessage("Usage").replace("%s", "/delhome <name>"));
            return true;
        }

        String homeName = args[0];

        if (plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName)) {
            plugin.sendCFGmessage(sender,
                    plugin.getMessage("homeDeletionSuccess").replace("{homename}", homeName));
        } else {
            plugin.sendCFGmessage(sender,
                    plugin.getMessage("homeNotFound").replace("{homename}", homeName));
        }
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> playerHomes = plugin.getHomeManager().getPlayerHomes(player.getUniqueId());
            for (String home : playerHomes) {
                if (home.toLowerCase().startsWith(prefix)) {
                    completions.add(home);
                }
            }
        }
        return completions;
    }
}
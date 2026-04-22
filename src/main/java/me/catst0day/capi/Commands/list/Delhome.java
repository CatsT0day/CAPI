package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

@CAPICommandAnnotation(
        name = "delhome",
        aliases = {"dhome", "dh"},
        permission = CAPIPermissionManager.CAPIPerm.HOME,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "delete your existing homes"
)
public class Delhome extends CAPICommandTemplate {
    private String successMsg;
    private String errorMsg;
    private String usageMsg;

    public Delhome(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        successMsg = plugin.getMessage("homeDeletionSuccess");
        errorMsg = plugin.getMessage("commandError");
        usageMsg = plugin.getMessage("homeDeleteUsage");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission("catapi.home.delete")) {
            plugin.sendCFGmessage(sender, "noPermission");
            return true;
        }

        if (args.length != 1) {
            plugin.sendCFGmessage(sender, usageMsg);
            return true;
        }

        String homeName = args[0];

        if (CAPI.getInstance().getHomeManager().deleteHome(player.getUniqueId(), homeName)) {
            plugin.sendCFGmessage(sender,
                    successMsg.replace("{homename}", homeName));
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
            List<String> playerHomes = CAPI.getInstance().getHomeManager()
                    .getPlayerHomes(player.getUniqueId());
            for (String home : playerHomes) {
                if (home.toLowerCase().startsWith(prefix)) {
                    completions.add(home);
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        }

        return completions;
    }
}
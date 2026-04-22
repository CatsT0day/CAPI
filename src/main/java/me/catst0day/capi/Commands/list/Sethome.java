
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Sethome extends CAPICommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Sethome(CAPI plugin) {
        super(plugin, "sethome", List.of("sh"), CAPIPermissionManager.CAPIPerm.HOME, true, 0L, "set home");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.HOME);
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

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("sethomeUsage"));
            return true;
        }

        String homeName = args[0];
        Location location = player.getLocation();

        if (!canSetMoreHomes(player)) {
            int maxHomes = getMaxHomes(player);
            sender.sendMessage(plugin.getMessage("homeLimitReached")
                    .replace("%max%", String.valueOf(maxHomes)));
            return true;
        }

        if (plugin.getInstance().getHomeManager().setHome(player.getUniqueId(), homeName, location)) {
            sender.sendMessage(plugin.getMessage("homeSet")
                    .replace("%homename%", homeName));
        } else {
            sender.sendMessage(plugin.getMessage("homeSetFailed"));
        }
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return new ArrayList<>();
    }

    private boolean canSetMoreHomes(Player player) {
        int maxHomes = getMaxHomes(player);
        int currentHomes = plugin.getInstance().getHomeManager()
                .getPlayerHomes(player.getUniqueId()).size();
        return currentHomes < maxHomes;
    }

    private int getMaxHomes(Player player) {
        for (int i = 100; i >= 1; i--) {
            if (permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.MAX_HOMES, String.valueOf(i))) {
                return i;
            }
        }
        return 1;
    }
}
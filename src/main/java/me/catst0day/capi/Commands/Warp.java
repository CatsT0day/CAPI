package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Warp extends CommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Warp(CAPI plugin) {
        super(plugin, "warp", List.of(), CAPIPermissionManager.CAPIPerm.WARP_USE, true, 0L, "tp to warp");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.WARP_USE);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/warp <название>"));
            String warpList = String.join(", ", plugin.getInstance().getWarpManager().getWarpList());
            if (warpList.isEmpty()) {
                sender.sendMessage(plugin.getMessage("noWarpsAvailable"));
            } else {
                sender.sendMessage(plugin.getMessage("warpList").replace("%s", warpList));
            }
            return true;
        }

        String warpName = args[0];
        Location warpLocation = plugin.getInstance().getWarpManager().loadWarp(warpName);

        if (warpLocation == null) {
            sender.sendMessage(plugin.getMessage("warpNotFound").replace("{warpname}", warpName));
            return true;
        }

        player.teleport(warpLocation);
        sender.sendMessage(plugin.getMessage("warpTeleported").replace("{warpname}", warpName));

        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String warp : plugin.getInstance().getWarpManager().getWarpList()) {
                if (warp.toLowerCase().startsWith(prefix)) {
                    completions.add(warp);
                }
            }
        } else if (args.length > 1) {
            completions.clear();
        }

        return completions;
    }
}
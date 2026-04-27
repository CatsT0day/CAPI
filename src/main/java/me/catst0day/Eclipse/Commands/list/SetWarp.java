package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class SetWarp extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public SetWarp(Eclipse plugin) {
        super(plugin, "setwarp", List.of("sw"), EclipsePermissionManager.CAPIPermissions.WARP_SET, true, 0L, "set warp");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.WARP_SET);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (!hasPermission((CommandSender) player, args)) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/setwarp <название>"));
            return true;
        }

        String warpName = args[0];

        if (plugin.getI().getWarpManager().warpExists(warpName)) {
            player.sendMessage(plugin.getMessage("warpAlreadyExists")
                    .replace("{warpname}", warpName));
            return true;
        }

        if (plugin.getI().getWarpManager().saveWarp(warpName, player.getLocation())) {
            player.sendMessage(plugin.getMessage("warpCreatedSuccessfully")
                    .replace("{warpname}", warpName));
        } else {
            player.sendMessage(plugin.getMessage("warpCreationFailed"));
        }
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }
}
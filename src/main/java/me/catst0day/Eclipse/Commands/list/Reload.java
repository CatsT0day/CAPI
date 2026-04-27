package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Reload extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Reload(Eclipse plugin) {
        super(plugin, "CRelaod", List.of(), EclipsePermissionManager.CAPIPermissions.RELOAD, true, 0L, "reload CAPI");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.RELOAD);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/CatAPI reload"));
            return true;
        }

        if (!hasPermission(player, args)) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        try {
            plugin.reloadConfig();
            Eclipse.getI().loadTranslations();
            player.sendMessage(plugin.getMessage("configReloaded"));
        } catch (Exception e) {
            player.sendMessage(plugin.getMessage("commandError"));
            plugin.getLogger().severe("Error (Exception) when reloaded CAPI: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return null;
    }
}
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPIOnEnableInitter;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Reload extends CAPICommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Reload(CAPI plugin) {
        super(plugin, "CRelaod", List.of(), CAPIPermissionManager.CAPIPerm.RELOAD, true, 0L, "reload CAPI");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.RELOAD);
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
            CAPIOnEnableInitter.loadTranslations();
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
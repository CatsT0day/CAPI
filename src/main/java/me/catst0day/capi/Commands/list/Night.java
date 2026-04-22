package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Night extends CAPICommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Night(CAPI plugin) {
        super(plugin, "night", List.of(), CAPIPermissionManager.CAPIPerm.NIGHT, false, 0L, "set nithgt time");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.NIGHT);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        player.getWorld().setTime(18000);
        sender.sendMessage(plugin.getMessage("nightSet"));
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
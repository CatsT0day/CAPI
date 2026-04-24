package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class God extends CAPICommandTemplate {

    public God(CAPI plugin) {
        super(plugin, "god", List.of(), CAPIPermissionManager.CAPIPerm.GOD, true, 0, "Toggle god mode");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        plugin.toggleGodMode(player, args);
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
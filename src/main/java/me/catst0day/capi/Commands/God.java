package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class God extends CommandTemplate {

    public God(CAPI plugin) {
        super(plugin, "god",  List.of(), CAPIPermissionManager.CAPIPerm.GOD, true, 20L, "enable god mode");
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        plugin.toggleGodMode(player, args);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return null; // Для команды /god таб‑комплит не требуется
    }
}
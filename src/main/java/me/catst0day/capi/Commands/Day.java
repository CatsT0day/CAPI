package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import java.util.List;

public class Day extends CommandTemplate {
    public Day(CAPI plugin) {
        super(plugin, "day", List.of("d"), CAPIPerm.DAY, false, 0, "set day time");
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        player.getWorld().setTime(0);
        sender.sendMessage(plugin.getMessage("daySet"));
        return true;
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }
}
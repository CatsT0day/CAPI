package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.API.Managers.WarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class SetWarp extends CommandTemplate {

    public SetWarp(CAPI plugin) {
        super(plugin, "setwarp",  List.of(),"catapi.warp.set", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/setwarp <название>"));
            return true;
        }

        String warpName = args[0];

        if (plugin.getInstance().getWarpManager().warpExists(warpName)) {
            player.sendMessage(plugin.getMessage("warpAlreadyExists")
                    .replace("{warpname}", warpName));
            return true;
        }

        if (plugin.getInstance().getWarpManager().saveWarp(warpName, player.getLocation())) {
            player.sendMessage(plugin.getMessage("warpCreatedSuccessfully")
                    .replace("{warpname}", warpName));
        } else {
            player.sendMessage(plugin.getMessage("warpCreationFailed"));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return null;
    }
}
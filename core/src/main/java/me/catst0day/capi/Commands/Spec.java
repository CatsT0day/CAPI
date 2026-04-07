package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Spec extends CommandTemplate {
    public Spec(CAPI plugin) {
        super(plugin, "spec", List.of(), "catapi.spec", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(target.getLocation());
                player.sendMessage(plugin.getMessage("specSuccess")
                        .formatted(target.getName()));
            } else {
                player.sendMessage(plugin.getMessage("playerNotFound"));
            }
        } else {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/spec [игрок]"));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames(args[0]);
        }
        return null;
    }
}
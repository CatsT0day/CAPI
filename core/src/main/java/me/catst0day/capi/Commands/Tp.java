package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tp extends CommandTemplate {
    public Tp(CAPI plugin) {
        super(plugin, "tp", List.of(),"catapi.tp", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.teleport(target);
                player.sendMessage(plugin.getMessage("tpSuccess").formatted(target.getName()));
            } else {
                player.sendMessage(plugin.getMessage("playerNotFound"));
            }
        } else {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/tp [игрок]"));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(getOnlinePlayerNames(args[0]));
        }

        return completions;
    }
}
package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Sudo extends CommandTemplate {
    public Sudo(CAPI plugin) {
        super(plugin, "sudo",  List.of(),"catapi.sudo", false, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/sudo <player> <command>"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(plugin.getMessage("sudoTargetOffline")
                    .replace("%s", targetName));
            return true;
        }


        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandToExecute = commandBuilder.toString().trim();

        try {
            boolean success = Bukkit.dispatchCommand(target, commandToExecute);

            if (success) {
                sender.sendMessage(plugin.getMessage("sudoSuccess")
                        .replace("%s", target.getName())
                        .replaceFirst("%s", commandToExecute));
            } else {
                sender.sendMessage(plugin.getMessage("sudoCommandFailed")
                        .replace("%s", target.getName()));
            }
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessage("sudoCommandFailed")
                    .replace("%s", target.getName()));
        }

        return true;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(getOnlinePlayerNames(args[0]));
        } else if (args.length > 1) {
            completions.clear();
        }

        return completions;
    }
}
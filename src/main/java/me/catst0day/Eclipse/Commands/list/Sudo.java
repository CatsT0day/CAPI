
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Sudo extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Sudo(Eclipse plugin) {
        super(plugin, "sudo", List.of(), EclipsePermissionManager.CAPIPermissions.SUDO, false, 0L, "perform a cmd as other plr");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.SUDO);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
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
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String name = onlinePlayer.getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    completions.add(name);
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return completions;
    }
}
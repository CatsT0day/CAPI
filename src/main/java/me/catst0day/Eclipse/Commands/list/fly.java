package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class fly extends CommandTemplate {

    public fly(Eclipse plugin) {
        super(plugin, "fly", List.of("flight"), EclipsePermissionManager.CAPIPermissions.FLY, true, 0, "Set flight mode for players");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return execute(player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return execute(sender, args);
    }

    private boolean execute(CommandSender sender, String[] args) {
        boolean silent = false;
        String targetName = null;
        Boolean state = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                silent = true;
                continue;
            }
            if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("on")) {
                state = true;
                continue;
            }
            if (arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("off")) {
                state = false;
                continue;
            }
            targetName = arg;
        }

        Player target = (targetName == null && sender instanceof Player) ? (Player) sender : Bukkit.getPlayer(targetName != null ? targetName : "");

        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (state == null) {
            state = !target.getAllowFlight();
        }

        EclipsePlr user = plugin.getPlayer(target);

        target.setFallDistance(0.0F);
        user.setAllowFlight(state);
        if (!state) user.setAllowFlight(false);

        String statusLabel = state ? plugin.getMessage("enabled") : plugin.getMessage("disabled");

        if (!silent) {
            if (target.equals(sender)) {
                sender.sendMessage(plugin.getMessage("flyToggled").replace("%s", statusLabel));
            } else {
                sender.sendMessage(plugin.getMessage("flyToggleSuccess")
                        .replace("%s", statusLabel)
                        .replace("%e", target.getName()));

                target.sendMessage(plugin.getMessage("flyToggled").replace("%s", statusLabel));
            }
        }

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) return null;
        if (args.length == 2) return List.of("true", "false", "-s");
        return List.of();
    }
}
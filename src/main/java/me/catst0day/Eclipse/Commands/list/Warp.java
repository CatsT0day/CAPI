package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Warp extends CommandTemplate {

    public Warp(Eclipse plugin) {
        super(
                plugin,
                "warp",
                List.of("warps"),
                EclipsePermissionManager.CAPIPermissions.WARP_USE,
                false,
                0L,
                "Teleports to warp location"
        );
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        String warpName = null;
        String targetName = null;
        boolean silent = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                if (sender.hasPermission("capi.command.silent")) {
                    silent = true;
                }
            } else if (warpName == null && plugin.getWarpManager().warpExists(arg)) {
                warpName = arg;
            } else if (targetName == null) {
                targetName = arg;
            }
        }

        Player target;
        if (targetName != null) {
            target = Bukkit.getPlayer(targetName);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
                return true;
            }
            target = (Player) sender;
        }

        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (warpName == null && args.length > 0) {
            warpName = args[0];
        }

        if (warpName == null) {
            showWarpList(sender);
            return true;
        }

        Location loc = plugin.getWarpManager().getWarp(warpName);
        if (loc == null) {
            sender.sendMessage(plugin.getMessage("warpNotFound").replace("{warpname}", warpName));
            return true;
        }

        if (!target.hasPermission("capi.warp." + warpName.toLowerCase()) && !target.hasPermission("capi.warp.all")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        target.teleport(loc);

        if (!silent) {
            target.sendMessage(plugin.getMessage("warpTeleported").replace("{warpname}", warpName));
            if (sender != target) {
                sender.sendMessage(plugin.getMessage("tpSuccess").replace("%s", warpName));
            }
        }

        return true;
    }

    private void showWarpList(CommandSender sender) {
        List<String> warps = plugin.getWarpManager().getWarpList();
        if (warps.isEmpty()) {
            sender.sendMessage(plugin.getMessage("warpNotFound").replace("{warpname}", "NONE"));
            return;
        }
        sender.sendMessage(String.join(", ", warps));
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return plugin.getWarpManager().getWarpList().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
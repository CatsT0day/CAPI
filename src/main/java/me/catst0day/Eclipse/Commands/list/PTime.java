package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class PTime extends CommandTemplate {

    public PTime(Eclipse plugin) {
        super(plugin, "ptime", List.of(), CAPIPermissions.PTIME, true, 0, "Set personal player time");
        setTabCompleteArguments(List.of("freeze", "unfreeze", "day", "night", "morning", "dusk", "realtime", "reset"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        boolean silent = false;
        boolean smooth = false;
        Action action = null;
        String targetName = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s") && sender.hasPermission("capi.silent")) silent = true;
            else if (arg.equalsIgnoreCase("-smooth")) smooth = true;
            else {
                Action tempAction = Action.getByName(arg);
                if (tempAction != null) action = tempAction;
                else targetName = arg;
            }
        }

        Player target = targetName != null ? plugin.getServer().getPlayer(targetName) : player;
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (action == null) {
            sender.sendMessage(plugin.getMessage("currentTime")
                    .replace("%time%", formatTime(target.getPlayerTime()))
                    .replace("%player%", target.getName())
                    .replace("%frozen%", !target.isPlayerTimeRelative() ? " (frozen)" : ""));
            return true;
        }

        switch (action) {
            case FREEZE -> target.setPlayerTime(target.getPlayerTime(), false);
            case UNFREEZE -> target.setPlayerTime(target.getPlayerTime() - target.getWorld().getTime(), true);
            case DAY -> setTime(target, 1000, smooth, sender, silent);
            case NIGHT -> setTime(target, 13000, smooth, sender, silent);
            case MORNING -> setTime(target, 0, smooth, sender, silent);
            case DUSK -> setTime(target, 12000, smooth, sender, silent);
            case REALTIME -> target.setPlayerTime(target.getWorld().getTime(), smooth);
            case RESET -> {
                target.resetPlayerTime();
                if (!silent) sender.sendMessage(plugin.getMessage("timeReset").replace("%player%", target.getName()));
            }
        }
        return true;
    }

    private void setTime(Player target, long time, boolean smooth, CommandSender sender, boolean silent) {
        target.setPlayerTime(time, smooth);
        if (!silent) {
            String tStr = formatTime(time);
            sender.sendMessage(plugin.getMessage("timeSet").replace("%time%", tStr).replace("%player%", target.getName()));
        }
    }

    private String formatTime(long time) {
        long ticks = time % 24000;
        int hours24 = (int) (ticks / 1000);
        int minutes = (int) ((ticks % 1000) * 0.06);
        return String.format("%02d:%02d", hours24, minutes);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }

    private enum Action {
        FREEZE, UNFREEZE, DAY, NIGHT, MORNING, DUSK, REALTIME, RESET;
        public static Action getByName(String name) {
            for (Action a : values()) if (a.name().equalsIgnoreCase(name)) return a;
            return null;
        }
    }
}
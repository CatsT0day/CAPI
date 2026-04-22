package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

@CAPICommandAnnotation(
        name = "ptime",
        aliases = {"time"},
        permission = CAPIPerm.PTIME,
        requirePlayer = false,
        cooldownSeconds = 0,
        description = "Player time management"
)
public class PTime extends CAPICommandTemplate {

    private String noPermissionMsg;
    private String playerOnlyMsg;
    private String playerNotFoundMsg;
    private String currentTimeMsg;
    private String timeFrozenMsg;
    private String timeUnfrozenMsg;
    private String timeResetMsg;
    private String yourTimeResetMsg;
    private String timeSetMsg;
    private String usageMsg;
    private String cantSetTimeMsg;

    public PTime(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of(
                "freeze", "unfreeze", "day", "night",
                "morning", "dusk", "realtime", "reset",
                "playername", "-s", "-smooth"
        ));
    }

    @Override
    public void get(CAPI plugin) {
        noPermissionMsg = plugin.getMessage("noPermission");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        currentTimeMsg = plugin.getMessage("currentTime");
        timeFrozenMsg = plugin.getMessage("timeFrozen");
        timeUnfrozenMsg = plugin.getMessage("timeUnfrozen");
        timeResetMsg = plugin.getMessage("timeReset");
        yourTimeResetMsg = plugin.getMessage("yourTimeReset");
        timeSetMsg = plugin.getMessage("timeSet");
        usageMsg = plugin.getMessage("usage");
        cantSetTimeMsg = plugin.getMessage("cantSetTime");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (requirePlayer && !(sender instanceof Player)) {
            sender.sendMessage(playerOnlyMsg);
            return true;
        }

        boolean silent = false;
        boolean smooth = false;
        Action action = null;
        String targetName = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                if (sender.hasPermission("capi.silent")) {
                    silent = true;
                }
            } else if (arg.equalsIgnoreCase("-smooth")) {
                smooth = true;
            } else {
                Action tempAction = Action.getByName(arg);
                if (tempAction != null) {
                    action = tempAction;
                } else {
                    targetName = arg;
                }
            }
        }

        Player target = targetName != null
                ? plugin.getServer().getPlayer(targetName)
                : (player != null ? player : null);

        if (target == null) {
            sender.sendMessage(playerNotFoundMsg);
            return true;
        }

        if (action == null) {
            long currentTime = target.getPlayerTime();
            String timeStr = formatTime(currentTime);
            boolean isFrozen = !target.isPlayerTimeRelative();
            sender.sendMessage(currentTimeMsg
                    .replace("%time%", timeStr)
                    .replace("%player%", target.getName())
                    .replace("%frozen%", isFrozen ? " (frozen)" : ""));
            return true;
        }

        switch (action) {
            case FREEZE:
                target.setPlayerTime(target.getPlayerTime(), false);
                if (!silent) sender.sendMessage(timeFrozenMsg.replace("%player%", target.getName()));
                break;
            case UNFREEZE:
                long worldTime = target.getWorld().getTime();
                target.setPlayerTime(target.getPlayerTime() - worldTime, true);
                if (!silent) sender.sendMessage(timeUnfrozenMsg.replace("%player%", target.getName()));
                break;
            case DAY:
                setTime(target, 1000, smooth, sender, silent);
                break;
            case NIGHT:
                setTime(target, 13000, smooth, sender, silent);
                break;
            case MORNING:
                setTime(target, 0, smooth, sender, silent);
                break;
            case DUSK:
                setTime(target, 12000, smooth, sender, silent);
                break;
            case REALTIME:
                setTimeToWorld(target, smooth, sender, silent);
                break;
            case RESET:
                target.resetPlayerTime();
                if (!silent) {
                    sender.sendMessage(timeResetMsg.replace("%player%", target.getName()));
                    if (!target.equals(sender)) {
                        target.sendMessage(yourTimeResetMsg);
                    }
                }
                break;
        }
        return true;
    }

    private void setTime(Player target, long time, boolean smooth, CommandSender sender, boolean silent) {
        // Здесь должна быть логика установки времени с плавностью
        target.setPlayerTime(time, smooth);
        if (!silent) {
            String timeStr = formatTime(time);
            sender.sendMessage(timeSetMsg
                    .replace("%time%", timeStr)
                    .replace("%player%", target.getName()));
            if (!target.equals(sender)) {
                target.sendMessage(plugin.getMessage("yourTimeSet")
                        .replace("%time%", timeStr));
            }
        }
    }

    private void setTimeToWorld(Player target, boolean smooth, CommandSender sender, boolean silent) {
        long worldTime = target.getWorld().getTime();
        target.setPlayerTime(worldTime, smooth);
        if (!silent) {
            String timeStr = formatTime(worldTime);
            sender.sendMessage(timeSetMsg
                    .replace("%time%", timeStr)
                    .replace("%player%", target.getName()));
        }
    }

    private String formatTime(long time) {
        long ticks = time % 24000;
        int hours24 = (int) (ticks / 1000);
        int minutes = (int) ((ticks % 1000) * 0.06);
        String hours12 = hours24 > 12 ? (hours24 - 12) + "" : hours24 == 0 ? "12" : hours24 + "";
        String ampm = hours24 >= 12 ? "PM" : "AM";
        return String.format("%02d:%02d (%s %s)", hours24, minutes, hours12, ampm);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }

    private enum Action {
        FREEZE, UNFREEZE, DAY, NIGHT, MORNING, DUSK, REALTIME, RESET;

        public static Action getByName(String name) {
            for (Action action : values()) {
                if (action.name().equalsIgnoreCase(name)) {
                    return action;
                }
            }
            return null;
        }
    }
}
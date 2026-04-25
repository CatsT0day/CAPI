package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class exp extends CAPICommandTemplate {

    private enum Action { SET, ADD, TAKE, CLEAR }

    private final Pattern randPattern = Pattern.compile("%rand\\/(\\d+|\\d+.?\\d+?)-(\\d+|\\d+.?\\d+?)%");
    private final Pattern percentPattern = Pattern.compile("(^(\\d+(\\.\\d+)?)(%)((\\[(\\d+(\\.\\d+)?)\\-(\\d+(\\.\\d+)?)\\])?(\\[([a-zA-z\\d]+)\\]+)?)$)");

    public exp(CAPI plugin) {
        super(
                plugin,
                "exp",
                List.of("experience"),
                CAPIPermissionManager.CAPIPerm.EXP,
                false,
                0,
                "edit player exp"
        );
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
                return true;
            }
            sendCurrentExpInfo(sender, (Player) sender);
            return true;
        }

        boolean silent = false;
        Action action = Action.ADD;
        long amount = 0;
        boolean isLevels = false;
        String targetName = null;

        boolean isPercent = false;
        Integer min = null;
        Integer max = null;
        String percentSourcePlayer = null;

        for (String arg : args) {
            String lower = arg.toLowerCase();
            if (lower.equals("-s")) { silent = true; continue; }
            if (lower.equals("add")) { action = Action.ADD; continue; }
            if (lower.equals("set")) { action = Action.SET; continue; }
            if (lower.equals("take")) { action = Action.TAKE; continue; }
            if (lower.equals("clear")) { action = Action.CLEAR; continue; }

            if (lower.contains("l")) {
                try {
                    amount = Long.parseLong(lower.replace("l", ""));
                    isLevels = true;
                    continue;
                } catch (NumberFormatException ignored) {}
            }

            Matcher randMatcher = randPattern.matcher(arg);
            if (randMatcher.find()) {
                try {
                    int r1 = Integer.parseInt(randMatcher.group(1));
                    int r2 = Integer.parseInt(randMatcher.group(2));
                    amount = new Random().nextInt(Math.max(1, Math.abs(r2 - r1) + 1)) + Math.min(r1, r2);
                    continue;
                } catch (Exception ignored) {}
            }

            Matcher percMatcher = percentPattern.matcher(arg);
            if (percMatcher.find()) {
                try {
                    amount = Long.parseLong(percMatcher.group(2));
                    isPercent = true;
                    if (percMatcher.group(7) != null) min = Integer.parseInt(percMatcher.group(7));
                    if (percMatcher.group(9) != null) max = Integer.parseInt(percMatcher.group(9));
                    percentSourcePlayer = percMatcher.group(12);
                    continue;
                } catch (Exception ignored) {}
            }

            Player found = Bukkit.getPlayer(arg);
            if (targetName == null && found != null) {
                targetName = arg;
            } else {
                try {
                    amount = Long.parseLong(arg);
                } catch (NumberFormatException ignored) {}
            }
        }

        Player target = (targetName != null) ? Bukkit.getPlayer(targetName) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }
        if (args.length == 1 && targetName != null) {
            sendCurrentExpInfo(sender, target);
            return true;
        }

        if (isPercent) {
            long sourceVal;
            if (percentSourcePlayer != null) {
                Player pSrc = Bukkit.getPlayer(percentSourcePlayer);
                sourceVal = (pSrc != null) ? (isLevels ? pSrc.getLevel() : getTotalExperience(pSrc)) : 0;
            } else {
                sourceVal = isLevels ? target.getLevel() : getTotalExperience(target);
            }
            amount = (sourceVal * amount) / 100;
            if (min != null && amount < min) amount = min;
            if (max != null && amount > max) amount = max;
        }

        long currentVal = isLevels ? target.getLevel() : getTotalExperience(target);
        long result;

        switch (action) {
            case ADD -> result = currentVal + amount;
            case TAKE -> result = Math.max(0, currentVal - amount);
            case CLEAR -> result = 0;
            default -> result = amount;
        }

        if (isLevels) {
            target.setLevel((int) result);
        } else {
            setTotalExperience(target, (int) result);
        }

        if (!silent) {
            String expStr = result + (isLevels ? "L" : "");
            sender.sendMessage(plugin.getMessage("expSelfFeedback")
                    .replace("[player]", target.getName())
                    .replace("[exp]", expStr));
            if (!target.equals(sender)) {
                target.sendMessage(plugin.getMessage("expTargetFeedback")
                        .replace("[sender]", sender.getName())
                        .replace("[exp]", expStr));
            }
        }
        return true;
    }

    private void sendCurrentExpInfo(CommandSender sender, Player target) {
        String msg = plugin.getMessage("expCurrent")
                .replace("[player]", target.getName())
                .replace("[lvl]", String.valueOf(target.getLevel()))
                .replace("[currentExp]", String.valueOf(getExpAtLevel(target)))
                .replace("[totalExp]", String.valueOf(getTotalExperience(target)));
        sender.sendMessage(msg);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>(List.of("add", "set", "take", "clear"));
            suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            return suggestions.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2) return List.of("add", "set", "take", "clear");
        return new ArrayList<>();
    }

    private int getExpAtLevel(Player player) {
        return Math.round(player.getExp() * getExpToLevelUp(player.getLevel()));
    }

    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        int res = 0;
        for (int i = 0; i < level; i++) res += getExpToLevelUp(i);
        res += getExpAtLevel(player);
        return res;
    }

    private void setTotalExperience(Player player, int exp) {
        player.setTotalExperience(exp);
        player.setLevel(0);
        player.setExp(0);
        while (exp >= getExpToLevelUp(player.getLevel())) {
            exp -= getExpToLevelUp(player.getLevel());
            player.setLevel(player.getLevel() + 1);
        }
        player.setExp((float) exp / (float) getExpToLevelUp(player.getLevel()));
    }

    private int getExpToLevelUp(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }
}
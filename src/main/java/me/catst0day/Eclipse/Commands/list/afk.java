package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class afk extends CommandTemplate {

    public afk(Eclipse plugin) {
        super(plugin, "afk", List.of("away"), EclipsePermissionManager.CAPIPermissions.AFK, true, 5, "enable afk");
        setTabCompleteArguments(List.of("-p:", "-s"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        String targetName = null;
        String reason = "";
        boolean silent = false;

        for (String arg : args) {
            if (arg.toLowerCase().equals("-s")) {
                if (sender.hasPermission("capi.silent")) {
                    silent = true;
                }
            } else if (arg.startsWith("-p:")) {
                targetName = arg.substring("-p:".length());
            } else {
                if (!reason.isEmpty()) reason += " ";
                reason += arg;
            }
        }

        Player targetPlayer = targetName != null ? Bukkit.getPlayer(targetName) : player;
        if (targetPlayer == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        EclipsePlr targetUser = new EclipsePlr(targetPlayer.getUniqueId());

        if (!reason.isEmpty()) {
            targetUser.setMetadata("afkReason", reason);
        }

        // Исправленная логика получения boolean из метадаты
        Object afkMeta = targetUser.getMetadata("isAfk");
        boolean isCurrentlyAfk = afkMeta != null && (boolean) afkMeta;
        boolean newAfkStatus = !isCurrentlyAfk;

        targetUser.setMetadata("isAfk", newAfkStatus);

        if (!silent) {
            String msgKey = newAfkStatus ? "afkSuccessMsg" : "afkSuccessSilentMsg";
            targetPlayer.sendMessage(plugin.getMessage(msgKey));

            // Работа с Title
            List<String> subTitles = plugin.getConfig().getStringList("afkSubTitles");
            if (subTitles.isEmpty()) {
                subTitles = List.of(plugin.getMessage("afkSTitle1"), plugin.getMessage("afkSTitle2"));
            }

            String randomSub = subTitles.get((int) (Math.random() * subTitles.size()));
            targetPlayer.sendTitle(plugin.getMessage("afkTitle"), randomSub, 10, 70, 20);
        }

        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("-p:");
            completions.add("-s");
        } else if (args.length >= 2 && args[0].startsWith("-p:")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }
        return completions;
    }
}
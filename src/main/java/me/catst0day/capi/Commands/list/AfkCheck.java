package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.User.CAPIUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class AfkCheck extends CAPICommandTemplate {

    public AfkCheck(CAPI plugin) {
        super(plugin, "afkcheck", List.of("isafk"), CAPIPermissionManager.CAPIPerm.AFKCHECK, false, 0, "Check afk");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        Player targetPlayer;
        String targetName;

        if (args.length == 0) {
            if (player == null) {
                sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
                return true;
            }
            targetPlayer = player;
            targetName = player.getName();
        } else {
            targetName = args[0];
            targetPlayer = Bukkit.getPlayer(targetName);
        }

        if (targetPlayer == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        CAPIUser targetUser = new CAPIUser(targetPlayer.getUniqueId());
        boolean isAfk = targetUser.getMetadata("isAfk") != null && (boolean) targetUser.getMetadata("isAfk");

        String msg = isAfk ? plugin.getMessage("isAfk") : plugin.getMessage("notAfk");
        sender.sendMessage(msg.replace("%player%", targetName));

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
            String prefix = args[0].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(prefix)) {
                    completions.add(onlinePlayer.getName());
                }
            }
        }
        return completions;
    }
}
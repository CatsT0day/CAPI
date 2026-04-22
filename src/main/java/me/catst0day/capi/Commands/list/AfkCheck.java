package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import me.catst0day.capi.User.CAPIUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

@CAPICommandAnnotation(
        name = "afkcheck",
        aliases = {"afkstatus"},
        permission = CAPIPerm.AFKCHECK,
        requirePlayer = false,
        cooldownSeconds = 0,
        description = "Check if player is AFK"
)
public class AfkCheck extends CAPICommandTemplate {

    private String isAfkMsg;
    private String notAfkMsg;
    private String playerNotFoundMsg;

    public AfkCheck(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        isAfkMsg = plugin.getMessage("isAfk");
        notAfkMsg = plugin.getMessage("notAfk");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length == 0) {
            // Проверка себя
            CAPIUser user = new CAPIUser(player.getUniqueId());
            boolean isAfk = user.getMetadata("isAfk") != null &&
                    (boolean) user.getMetadata("isAfk");

            sender.sendMessage(isAfk ? isAfkMsg : notAfkMsg);
            return true;
        }

        // Проверка другого игрока
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            sender.sendMessage(playerNotFoundMsg);
            return true;
        }

        CAPIUser targetUser = new CAPIUser(targetPlayer.getUniqueId());
        boolean isAfk = targetUser.getMetadata("isAfk") != null &&
                (boolean) targetUser.getMetadata("isAfk");

        String message = isAfk ? isAfkMsg.replace("%player", targetName) :
                notAfkMsg.replace("%player", targetName);
        sender.sendMessage(message);
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
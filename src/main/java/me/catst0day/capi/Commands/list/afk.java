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
        name = "afk",
        aliases = {"away"},
        permission = CAPIPerm.AFK,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "Toggle AFK mode. Reason can be provided"
)
public class afk extends CAPICommandTemplate {

    private String afkTitle;
    private List<String> afkSubTitles;
    private String afkSuccessMsg;
    private String afkSuccessSilentMsg;
    private String playerNotFoundMsg;

    public afk(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of("-p:", "-s"));
    }

    @Override
    public void get(CAPI plugin) {
        afkTitle = plugin.getMessage("afkTitle");
        afkSubTitles = plugin.getConfig().getStringList("afkSubTitles");
        if (afkSubTitles.isEmpty()) {
            afkSubTitles = List.of(
                    plugin.getMessage("afkSTitle1"),
                    plugin.getMessage("afkSTitle2"),
                    plugin.getMessage("afkSTitle3"),
                    plugin.getMessage("afkSTitle4")
            );
        }
        afkSuccessMsg = plugin.getMessage("afkSuccess");
        afkSuccessSilentMsg = plugin.getMessage("afkSuccessSilent");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
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
                if (!reason.isEmpty()) {
                    reason += " ";
                }
                reason += arg;
            }
        }

        Player targetPlayer = targetName != null ? Bukkit.getPlayer(targetName) : player;
        if (targetPlayer == null) {
            sender.sendMessage(playerNotFoundMsg);
            return true;
        }

        CAPIUser targetUser = new CAPIUser(targetPlayer.getUniqueId());


        if (!reason.isEmpty()) {
            targetUser.setMetadata("afkReason", reason);
        }
        boolean isNowAfk = targetUser.getMetadata("isAfk") == null &&
                (boolean) targetUser.getMetadata("isAfk");
        targetUser.setMetadata("isAfk", !isNowAfk);

        if (!silent) {
            String message = isNowAfk ? afkSuccessMsg : afkSuccessSilentMsg;
            targetPlayer.sendMessage(message);

            // Показываем title сообщение
            int randomIndex = (int) (Math.random() * afkSubTitles.size());
            String subTitle = afkSubTitles.get(randomIndex);
            targetPlayer.sendTitle(afkTitle, subTitle, 10, 70, 20);
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
            // Дополнение имён игроков
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}

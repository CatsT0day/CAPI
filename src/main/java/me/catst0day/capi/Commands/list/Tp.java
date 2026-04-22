package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

@CAPICommandAnnotation(
        name = "tp",
        aliases = {"teleport"},
        permission = CAPIPerm.TP,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "Teleport to another player"
)
public class Tp extends CAPICommandTemplate {

    private String tpSuccessMsg;
    private String playerNotFoundMsg;
    private String noPermissionMsg;
    private String usageMsg;

    public Tp(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        tpSuccessMsg = plugin.getMessage("tpSuccess");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        noPermissionMsg = plugin.getMessage("noPermission");
        usageMsg = plugin.getMessage("usage");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            player.sendMessage(noPermissionMsg);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.teleport(target);
                player.sendMessage(tpSuccessMsg.formatted(target.getName()));
            } else {
                player.sendMessage(playerNotFoundMsg);
            }
        } else {
            player.sendMessage(usageMsg.replace("%s", "/tp [игрок]"));
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

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
        name = "tphere",
        aliases = {"teleporthere"},
        permission = CAPIPerm.TPHERE,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "Teleport another player to your location"
)
public class Tphere extends CAPICommandTemplate {

    private String noPermissionMsg;
    private String usageMsg;
    private String playerNotFoundMsg;
    private String tphereSuccessMsg;
    private String tphereToYouSuccessMsg;

    public Tphere(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        noPermissionMsg = plugin.getMessage("noPermission");
        usageMsg = plugin.getMessage("usage");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        tphereSuccessMsg = plugin.getMessage("tphereSuccess");
        tphereToYouSuccessMsg = plugin.getMessage("tphereToYouSuccess");
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        return plugin.getPermissionManager().hasPermission(player, CAPIPerm.TPHERE);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(noPermissionMsg);
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(usageMsg.replace("%s", "/tphere <игрок>"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(playerNotFoundMsg);
            return true;
        }

        target.teleport(player.getLocation());
        sender.sendMessage(tphereSuccessMsg.replace("%s", target.getName()));
        target.sendMessage(tphereToYouSuccessMsg.replace("%s", player.getName()));

        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
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
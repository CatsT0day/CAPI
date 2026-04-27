package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tphere extends CommandTemplate {

    public Tphere(Eclipse plugin) {
        super(plugin, "tphere", List.of("s"), CAPIPermissions.TPHERE, true, 0, "Teleport a player to you");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (player == null) return true;

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/tphere <player>"));
            return true;
        }

        EclipsePlr plr = Eclipse.getI().getPlayer(player);
        EclipsePlr target = Eclipse.getI().getPlayer(UUID.fromString(args[0]));
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        target.teleportAsynchronously(player);
        sender.sendMessage(plugin.getMessage("tphereSuccess").replace("%s", target.getName()));
        target.sendMsg(plugin.getMessage("tphereToYouSuccess").replace("%s", player.getName()));
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) completions.add(p.getName());
            }
        }
        return completions;
    }
}
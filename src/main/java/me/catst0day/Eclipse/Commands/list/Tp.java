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

public class Tp extends CommandTemplate {

    public Tp(Eclipse plugin) {
        super(plugin, "tp", List.of("teleport"), CAPIPermissions.TP, true, 0, "Teleport to a player");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (player == null) return true;

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                EclipsePlr plr = Eclipse.getI().getPlayer(player);
                plr.teleportAsynchronously(target);
                player.sendMessage(plugin.getMessage("tpSuccess").replace("%s", target.getName()));
            } else {
                player.sendMessage(plugin.getMessage("playerNotFound"));
            }
        } else {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/tp [player]"));
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) completions.add(p.getName());
            }
        }
        return completions;
    }
}
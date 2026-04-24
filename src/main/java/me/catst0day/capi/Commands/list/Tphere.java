package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Tphere extends CAPICommandTemplate {

    public Tphere(CAPI plugin) {
        super(plugin, "tphere", List.of("s"), CAPIPerm.TPHERE, true, 0, "Teleport a player to you");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (player == null) return true;

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/tphere <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        target.teleport(player.getLocation());
        sender.sendMessage(plugin.getMessage("tphereSuccess").replace("%s", target.getName()));
        target.sendMessage(plugin.getMessage("tphereToYouSuccess").replace("%s", player.getName()));
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
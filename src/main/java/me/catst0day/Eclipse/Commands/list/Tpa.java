package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import java.util.ArrayList;
import java.util.List;

public class Tpa extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Tpa(Eclipse plugin) {
        super(plugin, "tpa", List.of("tpr", "teleportrequest"), EclipsePermissionManager.CAPIPermissions.TPA, true, 0L, "send tp request");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.TPA);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/tpa <игрок>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        plugin.getTpaRequests().put(player.getUniqueId(), target.getUniqueId());
        sender.sendMessage(plugin.getMessage("tpaSent").replace("%player%", target.getName()));

        TextComponent message = new TextComponent(
                plugin.getMessage("tpaReceived").replace("%player%", player.getName())
        );
        TextComponent acceptButton = new TextComponent(plugin.getMessage("acceptButton"));
        acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent denyButton = new TextComponent(plugin.getMessage("denyButton"));
        denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
        denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        target.spigot().sendMessage(message, acceptButton, denyButton);
        return true;
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
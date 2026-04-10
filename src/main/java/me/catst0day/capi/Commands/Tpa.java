package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import java.util.ArrayList;
import java.util.List;

public class Tpa extends CommandTemplate {

    private final CAPIPermissionManager permissionManager;

    public Tpa(CAPI plugin) {
        super(plugin, "tpa", List.of("tpr", "teleportrequest"), CAPIPermissionManager.CAPIPerm.TPA, true, 0L, "send tp request");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.TPA);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
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
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(getOnlinePlayerNames(args[0]));
        } else if (args.length > 1) {
            completions.clear();
        }

        return completions;
    }
}
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class Tpa implements CommandExecutor {
    private final CatAPI plugin;

    public Tpa(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/tpa <игрок>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        plugin.getTpaRequests().put(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(plugin.getMessage("tpaSent").replace("%player%", target.getName()));

        TextComponent message = new TextComponent(
                plugin.getMessage("tpaReceived").replace("%player%", player.getName())
        );
        TextComponent acceptButton = new TextComponent(" [ПРИНЯТЬ]");
        acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent denyButton = new TextComponent(" [ОТКЛОНИТЬ]");
        denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
        denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        target.spigot().sendMessage(message, acceptButton, denyButton);
        return true;
    }
}
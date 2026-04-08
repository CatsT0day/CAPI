
package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends CommandTemplate {
    public Reload(CAPI plugin) {
        super(plugin, "CatAPI", List.of(), "catapi.reload", true, 0L); // Кулдаун 0 — не нужен для reload
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/CatAPI reload"));
            return true;
        }


        if (!player.hasPermission("catapi.reload")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        try {
            plugin.reloadConfig();
            plugin.loadTranslations();
            player.sendMessage(plugin.getMessage("configReloaded"));
        } catch (Exception e) {
            player.sendMessage(plugin.getMessage("commandError"));
            plugin.getLogger().severe("Error (Exception) when reloaded CAPI: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return null;
    }
}
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gm implements CommandExecutor {
    private final CatAPI plugin;

    public Gm(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (!player.hasPermission("catapi.gm")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length == 1) {
            try {
                int mode = Integer.parseInt(args[0]);
                if (mode < 0 || mode > 3) throw new IndexOutOfBoundsException();

                GameMode gameMode = switch (mode) {
                    case 0 -> GameMode.SURVIVAL;
                    case 1 -> GameMode.CREATIVE;
                    case 2 -> GameMode.ADVENTURE;
                    case 3 -> GameMode.SPECTATOR;
                    default -> throw new IllegalStateException("Unexpected value: " + mode);
                };

                player.setGameMode(gameMode);
                player.sendMessage(plugin.getGameModeMessage(gameMode));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                player.sendMessage(plugin.getMessage("invalidMode"));
            }
        } else {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/gm [номер]"));
        }
        return true;
    }
}

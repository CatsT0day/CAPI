
package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class Gm extends CommandTemplate {

    public Gm(CAPI plugin) {
        super(plugin, "gm",  List.of(),"catapi.gm", true, 20L);
        setTabCompleteArguments(Arrays.asList("0", "1", "2", "3", "survival", "creative", "adventure", "spectator", "s", "c", "a", "spec"));
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            String usageMessage = plugin.getMessage("usage");
            player.sendMessage(String.format(usageMessage, "/gm [num|mode]"));
            return true;
        }

        try {
            GameMode gameMode = parseGameMode(args[0]);
            if (gameMode != null) {
                player.setGameMode(gameMode);
                player.sendMessage(plugin.getGameModeMessage(gameMode));
            } else {
                player.sendMessage(plugin.getMessage("invalidMode"));
            }
        } catch (Exception e) {
            player.sendMessage(plugin.getMessage("invalidMode"));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    private GameMode parseGameMode(String input) {
        String lowerInput = input.toLowerCase();


        try {
            int mode = Integer.parseInt(lowerInput);
            return switch (mode) {
                case 0 -> GameMode.SURVIVAL;
                case 1 -> GameMode.CREATIVE;
                case 2 -> GameMode.ADVENTURE;
                case 3 -> GameMode.SPECTATOR;
                default -> null;
            };
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    };

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return filterByInput(args[0], tabCompleteArguments);
        }
        return null;
    }
}
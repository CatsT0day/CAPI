package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.ArrayList;

public class Gm extends CommandTemplate {

    public Gm(Eclipse plugin) {
        super(plugin, "gm", List.of("gamemode"), EclipsePermissionManager.CAPIPermissions.GM, true, 0, "Change your gamemode");
        setTabCompleteArguments(List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator"));
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(String.format(plugin.getMessage("usage"), "/gm [mode]"));
            return true;
        }

        GameMode gm = parseGameMode(args[0].toLowerCase());
        if (gm != null) {
            player.setGameMode(gm);
            player.sendMessage(plugin.getGameModeMessage(String.valueOf(gm)));
        } else {
            player.sendMessage(plugin.getMessage("invalidMode"));
        }
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    private GameMode parseGameMode(String input) {
        return switch (input) {
            case "0", "survival", "s" -> GameMode.SURVIVAL;
            case "1", "creative", "c" -> GameMode.CREATIVE;
            case "2", "adventure", "a" -> GameMode.ADVENTURE;
            case "3", "spectator", "spec" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            getTabCompleteArguments().forEach(opt -> {
                if (opt.startsWith(prefix)) completions.add(opt);
            });
        }
        return completions;
    }
}
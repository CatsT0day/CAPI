package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.ArrayList;

@CAPICommandAnnotation(
        name = "gm",
        aliases = {"gamemode"},
        permission = CAPIPermissionManager.CAPIPerm.GM,
        requirePlayer = true,
        cooldownSeconds = 20,
        description = "change your game mode"
)
public class Gm extends CAPICommandTemplate {

    private String usageMsg;
    private String successMsg;
    private String invalidModeMsg;
    private String playerOnlyMsg;

    public Gm(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator", "s", "c", "a", "spec"));
    }

    @Override
    public void get(CAPI plugin) {
        usageMsg = plugin.getMessage("usage");
        successMsg = plugin.getMessage("gmSuccess");
        invalidModeMsg = plugin.getMessage("invalidMode");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(String.format(usageMsg, "/gm [num|mode]"));
            return true;
        }

        String mode = args[0].toLowerCase();
        GameMode gameMode = parseGameMode(mode);

        if (gameMode != null) {
            player.setGameMode(gameMode);
            player.sendMessage(successMsg);
            return true;
        }

        player.sendMessage(invalidModeMsg);
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(playerOnlyMsg);
        return true;
    }

    private GameMode parseGameMode(String input) {
        try {
            int mode = Integer.parseInt(input);
            return switch (mode) {
                case 0 -> GameMode.SURVIVAL;
                case 1 -> GameMode.CREATIVE;
                case 2 -> GameMode.ADVENTURE;
                case 3 -> GameMode.SPECTATOR;
                default -> null;
            };
        } catch (NumberFormatException e) {
            return switch (input) {
                case "survival", "s" -> GameMode.SURVIVAL;
                case "creative", "c" -> GameMode.CREATIVE;
                case "adventure", "a" -> GameMode.ADVENTURE;
                case "spectator", "spec" -> GameMode.SPECTATOR;
                default -> null;
            };
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String option : getTabCompleteArguments()) {
                if (option.toLowerCase().startsWith(prefix)) {
                    completions.add(option);
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return completions;
    }
}
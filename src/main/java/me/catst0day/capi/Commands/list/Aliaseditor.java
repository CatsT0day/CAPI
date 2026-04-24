package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class Aliaseditor extends CAPICommandTemplate {

    public Aliaseditor(CAPI plugin) {
        super(plugin, "aliaseditor", List.of("ae"), CAPIPermissionManager.CAPIPerm.ALIAS_EDITOR, true, 0, "ALias editor :)");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            showAliasList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage(String.format(plugin.getMessage("aliasEditor.addCommandPrompt"), "/ae add <alias>"));
                    return true;
                }
                createNewAlias(player, args[1]);
                return true;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(plugin.getMessage("aliasEditor.invalidAlias"));
                    return true;
                }
                removeAlias(player, args[1]);
                return true;
            default:
                // Обработка подкоманд для конкретного алиаса: /ae <alias> add/remove
                if (args.length >= 3) {
                    String aliasName = args[0];
                    if (args[1].equalsIgnoreCase("add")) {
                        addCommand(player, aliasName, String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                        return true;
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        try {
                            removeCommand(player, aliasName, Integer.parseInt(args[2]));
                        } catch (NumberFormatException e) {
                            player.sendMessage(plugin.getMessage("aliasEditor.invalidAlias"));
                        }
                        return true;
                    }
                }
                showAliasCommands(player, args[0]);
                return true;
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    private void showAliasList(Player player) {
        Map<String, List<String>> aliases = plugin.getAliasManager().getAllAliases();
        player.sendMessage(plugin.getMessage("aliasEditor.listTitle"));

        int index = 1;
        String format = plugin.getMessage("aliasEditor.aliasItem");
        for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
            player.sendMessage(format
                    .replace("%index%", String.valueOf(index++))
                    .replace("%alias%", entry.getKey())
                    .replace("%commands_count%", String.valueOf(entry.getValue().size())));
        }
    }

    private void createNewAlias(Player player, String aliasName) {
        plugin.getAliasManager().createAlias(aliasName, new ArrayList<>());
        player.sendMessage(plugin.getMessage("aliasEditor.created").replace("%alias%", aliasName));
    }

    private void removeAlias(Player player, String aliasName) {
        if (plugin.getAliasManager().removeAlias(aliasName)) {
            player.sendMessage(plugin.getMessage("aliasEditor.removed").replace("%alias%", aliasName));
        } else {
            player.sendMessage(plugin.getMessage("aliasEditor.invalidAlias"));
        }
    }

    private void showAliasCommands(Player player, String aliasName) {
        List<String> commands = plugin.getAliasManager().getCommandsForAlias(aliasName);
        if (commands == null) {
            player.sendMessage(plugin.getMessage("aliasEditor.invalidAlias"));
            return;
        }

        player.sendMessage(plugin.getMessage("aliasEditor.commandsTitle").replace("%alias%", aliasName));
        String format = plugin.getMessage("aliasEditor.commandItem");
        for (int i = 0; i < commands.size(); i++) {
            player.sendMessage(format
                    .replace("%index%", String.valueOf(i + 1))
                    .replace("%command%", commands.get(i)));
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("add", "remove"));
            completions.addAll(plugin.getAliasManager().getAllAliasNames());
        }
        return completions;
    }

    public void addCommand(Player player, String aliasName, String command) {
        plugin.getAliasManager().addCommandToAlias(aliasName, command);
        player.sendMessage(plugin.getMessage("aliasEditor.saveSuccess").replace("%alias%", aliasName));
    }

    public void removeCommand(Player player, String aliasName, int index) {
        plugin.getAliasManager().removeCommandFromAlias(aliasName, index - 1);
        player.sendMessage(plugin.getMessage("aliasEditor.removeCommand")
                .replace("%alias%", aliasName)
                .replace("%index%", String.valueOf(index)));
    }
}
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

@CAPICommandAnnotation(
        name = "aliaseditor",
        aliases = {"aliasedit", "ae" , "aeditor", "aedit"},
        permission = CAPIPermissionManager.CAPIPerm.ALIAS_EDITOR,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "alias editor for custom commands"
)
public class Aliaseditor extends CAPICommandTemplate {

    private String noPermissionMsg;
    private String playerOnlyMsg;
    private String aliasListTitle;
    private String aliasItemFormat;
    private String aliasCreatedMsg;
    private String aliasRemovedMsg;
    private String commandsTitle;
    private String commandItemFormat;
    private String addCommandPrompt;
    private String removeCommandMsg;
    private String saveSuccessMsg;
    private String invalidAliasMsg;

    public Aliaseditor(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        noPermissionMsg = plugin.getMessage("noPermission");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
        aliasListTitle = plugin.getMessage("aliasEditor.listTitle");
        aliasItemFormat = plugin.getMessage("aliasEditor.aliasItem");
        aliasCreatedMsg = plugin.getMessage("aliasEditor.created");
        aliasRemovedMsg = plugin.getMessage("aliasEditor.removed");
        commandsTitle = plugin.getMessage("aliasEditor.commandsTitle");
        commandItemFormat = plugin.getMessage("aliasEditor.commandItem");
        addCommandPrompt = plugin.getMessage("aliasEditor.addCommandPrompt");
        removeCommandMsg = plugin.getMessage("aliasEditor.removeCommand");
        saveSuccessMsg = plugin.getMessage("aliasEditor.saveSuccess");
        invalidAliasMsg = plugin.getMessage("aliasEditor.invalidAlias");
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
                    player.sendMessage(String.format(addCommandPrompt, "/aliaseditor add <alias>"));
                    return true;
                }
                createNewAlias(player, args[1]);
                return true;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(invalidAliasMsg);
                    return true;
                }
                removeAlias(player, args[1]);
                return true;
            default:
                showAliasCommands(player, args[0]);
                return true;
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(playerOnlyMsg);
        return true;
    }

    private void showAliasList(Player player) {
        Map<String, List<String>> aliases = plugin.getAliasManager().getAllAliases();

        player.sendMessage(aliasListTitle);
        player.sendMessage(" ");

        int index = 1;
        for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
            String formatted = aliasItemFormat
                    .replace("%index%", String.valueOf(index))
                    .replace("%alias%", entry.getKey())
                    .replace("%commands_count%", String.valueOf(entry.getValue().size()));
            player.sendMessage(formatted);
            index++;
        }

        if (aliases.isEmpty()) {
            player.sendMessage(plugin.getMessage("aliasEditor.noAliases"));
        }
    }

    private void createNewAlias(Player player, String aliasName) {
        plugin.getAliasManager().createAlias(aliasName, new ArrayList<>());
        player.sendMessage(aliasCreatedMsg.replace("%alias%", aliasName));
        showAliasCommands(player, aliasName);
    }

    private void removeAlias(Player player, String aliasName) {
        if (plugin.getAliasManager().removeAlias(aliasName)) {
            player.sendMessage(aliasRemovedMsg.replace("%alias%", aliasName));
        } else {
            player.sendMessage(invalidAliasMsg);
        }
    }

    private void showAliasCommands(Player player, String aliasName) {
        List<String> commands = plugin.getAliasManager().getCommandsForAlias(aliasName);

        if (commands == null) {
            player.sendMessage(invalidAliasMsg);
            return;
        }

        player.sendMessage(commandsTitle.replace("%alias%", aliasName));
        player.sendMessage(" ");

        for (int i = 0; i < commands.size(); i++) {
            String formatted = commandItemFormat
                    .replace("%index%", String.valueOf(i + 1))
                    .replace("%command%", commands.get(i));
            player.sendMessage(formatted);
        }

        if (commands.isEmpty()) {
            player.sendMessage(plugin.getMessage("aliasEditor.noCommands"));
        }

        player.sendMessage(" ");
        player.sendMessage(String.format(addCommandPrompt, "/aliaseditor %s add <command>", aliasName));
        player.sendMessage(String.format(removeCommandMsg, "/aliaseditor %s remove <index>", aliasName));
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("add", "remove"));
            completions.addAll(plugin.getAliasManager().getAllAliasNames());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            List<String> aliases = plugin.getAliasManager().getAllAliasNames();
            String prefix = args[1].toLowerCase();
            for (String alias : aliases) {
                if (alias.toLowerCase().startsWith(prefix)) {
                    completions.add(alias);
                }
            }
        } else if (args.length >= 2) {
            String aliasName = args[0];
            if (plugin.getAliasManager().getCommandsForAlias(aliasName) != null) {
                switch (args[1].toLowerCase()) {
                    case "add":
                        completions.add("<command>");
                        break;
                    case "remove":
                        List<String> commands = plugin.getAliasManager().getCommandsForAlias(aliasName);
                        if (commands != null) {
                            for (int i = 0; i < commands.size(); i++) {
                                completions.add(String.valueOf(i + 1));
                            }
                        }
                        break;
                }
            }
        }

        return completions;
    }

    public void addCommand(Player player, String aliasName, String command) {
        if (!plugin.getAliasManager().getAllAliasNames().contains(aliasName)) {
            player.sendMessage(invalidAliasMsg);
            return;
        }
        plugin.getAliasManager().addCommandToAlias(aliasName, command);
        player.sendMessage(saveSuccessMsg.replace("%alias%", aliasName));
        showAliasCommands(player, aliasName);
    }

    public void removeCommand(Player player, String aliasName, int index) {
        if (!plugin.getAliasManager().getAllAliasNames().contains(aliasName)) {
            player.sendMessage(invalidAliasMsg);
            return;
        }
        plugin.getAliasManager().removeCommandFromAlias(aliasName, index - 1);
        player.sendMessage(removeCommandMsg.replace("%alias%", aliasName).replace("%index%", String.valueOf(index)));
        showAliasCommands(player, aliasName);
    }
}
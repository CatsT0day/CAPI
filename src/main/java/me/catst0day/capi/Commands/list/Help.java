package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Help extends CAPICommandTemplate {

    public Help(CAPI plugin) {
        super(plugin, "help", List.of("?", "h"), CAPIPerm.HELP, true, 0, "Show help information");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        List<String> helpMessages = plugin.getConfig().getStringList("messages.help");

        if (helpMessages.isEmpty()) {
            player.sendMessage(plugin.getMessage("helpUnavailable"));
            return true;
        }

        for (String line : helpMessages) {
            player.sendMessage(line);
        }
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
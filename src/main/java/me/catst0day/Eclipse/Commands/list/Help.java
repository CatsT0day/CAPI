package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Help extends CommandTemplate {

    public Help(Eclipse plugin) {
        super(plugin, "help", List.of("?", "h"), CAPIPermissions.HELP, true, 0, "Show help information");
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
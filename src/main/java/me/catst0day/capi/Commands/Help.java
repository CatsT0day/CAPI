package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Help extends CommandTemplate {

    public Help(CAPI plugin) {
        super(plugin, "help", List.of("n"), CAPIPermissionManager.CAPIPerm.HELP, true, 0L, "help menu");
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        List<String> helpMessages = plugin.getConfig().getStringList("messages.help");
        if (helpMessages == null || helpMessages.isEmpty()) {
            player.sendMessage(plugin.getMessage("helpUnavailable"));
            return true;
        }

        for (String line : helpMessages) {
            player.sendMessage(line);
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        return null;
    }
}
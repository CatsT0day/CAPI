package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

@CAPICommandAnnotation(
        name = "help",
        permission = CAPIPermissionManager.CAPIPerm.HELP,
        requirePlayer = true,
        cooldownSeconds = 0,
        description = "help menu"
)
public class Help extends CAPICommandTemplate {

    private List<String> helpMessages;
    private String helpUnavailableMsg;
    private String playerOnlyMsg;

    public Help(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        helpMessages = plugin.getConfig().getStringList("messages.help");
        helpUnavailableMsg = plugin.getMessage("helpUnavailable");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (helpMessages == null || helpMessages.isEmpty()) {
            player.sendMessage(helpUnavailableMsg);
            return true;
        }

        for (String line : helpMessages) {
            player.sendMessage(line);
        }
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(playerOnlyMsg);
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }
}
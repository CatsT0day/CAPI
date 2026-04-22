package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CAPICommandAnnotation(
        name = "god",
        aliases = {},
        permission = CAPIPermissionManager.CAPIPerm.GOD,
        requirePlayer = true,
        cooldownSeconds = 20,
        description = "enable god mode"
)
public class God extends CAPICommandTemplate {

    private String godToggledMsg;
    private String noPermissionMsg;
    private String playerOnlyMsg;

    public God(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        godToggledMsg = plugin.getMessage("godToggled");
        noPermissionMsg = plugin.getMessage("noPermission");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        plugin.toggleGodMode(player, args);
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
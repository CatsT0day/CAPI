package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import java.util.List;

@CAPICommandAnnotation(
        name = "day",
        aliases = {"d"},
        permission = CAPIPerm.DAY,
        cooldownSeconds = 60,
        description = "set day time"
)
public class Day extends CAPICommandTemplate {
    private String successMsg;
    private String errorMsg;
    private String usageMsg;

    public Day(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        successMsg = plugin.getMessage("daySet");
        errorMsg = plugin.getMessage("commandError");
        usageMsg = plugin.getMessage("usage")
                .replace("%s", "/day");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        try {
            player.getWorld().setTime(0);
            plugin.sendCFGmessage(sender, successMsg);
            return true;
        } catch (Exception e) {
            plugin.sendCFGmessage(sender, errorMsg);
            return false;
        }
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}

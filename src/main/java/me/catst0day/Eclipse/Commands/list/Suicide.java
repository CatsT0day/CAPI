package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Suicide extends CommandTemplate {

    public Suicide(Eclipse plugin) {
        super(
                plugin,
                "suicide",
                List.of("killyourself"),
                CAPIPermissions.SUICIDE,
                true,
                0,
                "Commit suicide and die instantly"
        );
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        EntityDamageEvent damageEvent = new EntityDamageEvent(
                player,
                EntityDamageEvent.DamageCause.SUICIDE,
                Float.MAX_VALUE
        );

        player.getServer().getPluginManager().callEvent(damageEvent);

        if (!damageEvent.isCancelled()) {
            player.setHealth(0.0);
            player.sendMessage(plugin.getMessage("suicideMessage"));

            String displayName = player.getDisplayName();
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage(String.format(
                        plugin.getMessage("suicideSuccess"),
                        displayName
                ));
            }
        } else {
            player.sendMessage(plugin.getMessage("suicideCancelled"));
        }

        return true;
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
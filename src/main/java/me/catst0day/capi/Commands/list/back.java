package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class back extends CAPICommandTemplate implements Listener {

    private final HashMap<UUID, Location> lastLocations = new HashMap<>();

    public back(CAPI plugin) {
        super(plugin, "back", List.of("return"), CAPIPerm.BACK, false, 0, "Teleports back to last saved location (last death loc)");
        setTabCompleteArguments(List.of("playername", "-s"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        String targetName = null;
        boolean silent = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                silent = true;
            } else {
                targetName = arg;
            }
        }

        Player target = targetName != null ? plugin.getServer().getPlayer(targetName) : player;

        if (target == null) {
            plugin.sendCFGmessage(sender, plugin.getMessage("playerNotFound"));
            return true;
        }

        return localTeleport(target, sender, silent);
    }

    private boolean localTeleport(Player target, CommandSender sender, boolean silent) {
        Location lastLoc = lastLocations.get(target.getUniqueId());

        if (lastLoc == null || lastLoc.getWorld() == null) {
            plugin.sendCFGmessage(sender, plugin.getMessage("backNoInfo"));
            return true;
        }

        target.teleport(lastLoc);

        if (!silent) {
            String msg = plugin.getMessage("backFeedback")
                    .replace("[worldName]", lastLoc.getWorld().getName())
                    .replace("[x]", String.valueOf(lastLoc.getBlockX()))
                    .replace("[y]", String.valueOf(lastLoc.getBlockY()))
                    .replace("[z]", String.valueOf(lastLoc.getBlockZ()));

            plugin.sendCFGmessage(sender, msg);
        }

        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND ||
                event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            lastLocations.put(event.getPlayer().getUniqueId(), event.getFrom());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        lastLocations.put(event.getEntity().getUniqueId(), event.getEntity().getLocation());
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
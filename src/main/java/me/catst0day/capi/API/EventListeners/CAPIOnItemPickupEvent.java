package me.catst0day.capi.EventListeners;

import me.catst0day.capi.CAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CAPIOnItemPickupEvent {
    protected final CAPI plugin;

    public CAPIOnItemPickupEvent(CAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.isGodMode(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessage("godModeItemPickupDenied"));
        }
    }
}

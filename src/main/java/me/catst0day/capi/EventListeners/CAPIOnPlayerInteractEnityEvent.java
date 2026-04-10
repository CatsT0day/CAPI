package me.catst0day.capi.EventListeners;

import me.catst0day.capi.CAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CAPIOnPlayerInteractEnityEvent {
    protected final CAPI plugin;

    public CAPIOnPlayerInteractEnityEvent(CAPI plugin) {
        this.plugin = plugin;
    }
   @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (plugin.isGodMode(player.getUniqueId()) && event.getRightClicked() instanceof LivingEntity) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessage("godDamageDenied"));
        }
    }
}

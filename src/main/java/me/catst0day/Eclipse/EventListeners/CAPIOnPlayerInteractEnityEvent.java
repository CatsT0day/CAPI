package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CAPIOnPlayerInteractEnityEvent {
    protected final Eclipse plugin;

    public CAPIOnPlayerInteractEnityEvent(Eclipse plugin) {
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

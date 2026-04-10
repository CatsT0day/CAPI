
package me.catst0day.capi.EventListeners;

import me.catst0day.capi.CAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener; // Добавлен импорт интерфейса Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CAPIOnEntityDamageEvent implements Listener {
    protected final CAPI plugin;

    public CAPIOnEntityDamageEvent(CAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        if (plugin.isGodMode(damager.getUniqueId())) {
            event.setCancelled(true);
            damager.sendMessage(plugin.getMessage("godDamageDenied"));
        }
    }
}
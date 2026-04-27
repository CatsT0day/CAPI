
package me.catst0day.Eclipse.EventListeners;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener; // Добавлен импорт интерфейса Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CAPIOnEntityDamageEvent implements Listener {
    protected final Eclipse plugin;

    public CAPIOnEntityDamageEvent(Eclipse plugin) {
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
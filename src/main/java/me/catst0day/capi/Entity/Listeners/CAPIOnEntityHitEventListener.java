package me.catst0day.capi.Entity.Listeners;

import me.catst0day.capi.Bossbar.CAPIBarStyle;
import me.catst0day.capi.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.Bossbar.CAPIBossBarInfo;
import org.bukkit.ChatColor;
import me.catst0day.capi.Bossbar.CAPIBarColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;
import java.util.HashMap;

public class CAPIOnEntityHitEventListener implements Listener {
    private final Plugin plugin;
    private final HashMap<UUID, CAPIBossBarInfo> activeBars = new HashMap<>();

    public CAPIOnEntityHitEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) event.getDamager();
        LivingEntity target = (LivingEntity) event.getEntity();

        showBossBarForEntity(player, target);
    }

    private void showBossBarForEntity(Player player, LivingEntity entity) {
        double currentHealth = entity.getHealth();
        double maxHealth = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        UUID entityId = entity.getUniqueId();
        String barName = "entity_health_" + entityId;


        CAPIBossBarInfo oldBar = activeBars.get(entityId);
        if (oldBar != null) {
            oldBar.remove();
        }


        CAPIBossBarInfo barInfo = new CAPIBossBarInfo(plugin, player, barName);
        barInfo.setPercentage(maxHealth, currentHealth);
        barInfo.setTitleOfBar(ChatColor.RED + entity.getType().name() + " " +
                ChatColor.WHITE + "(" + (int) currentHealth + "/" + (int) maxHealth + " HP)");
        barInfo.setColor(CAPIBarColor.RED);
        barInfo.setStyle(CAPIBarStyle.SOLID);
        barInfo.setKeepForTicks(40);
        barInfo.setMakeVisible(true);

        BossBar bossBar = barInfo.getBar();
        if (bossBar != null) {
            bossBar.addPlayer(player);
        } else {
            plugin.getLogger().severe("Failed to create BossBar for entity: " + entityId);
            return;
        }


        activeBars.put(entityId, barInfo);

        CAPIMainScheduler.runTaskLater(plugin, () -> {
            activeBars.remove(entityId);
            barInfo.remove();
        }, 40L);
    }
}
package me.catst0day.capi.API.Entity.Listeners;

import me.catst0day.capi.API.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.API.EventListeners.BossBarInfo;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.HashMap;

public class CAPIOnEntityHitEventListener implements Listener {
    private final Plugin plugin;
    private final HashMap<UUID, BossBarInfo> activeBars = new HashMap<>();

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
        double maxHealth = entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        UUID entityId = entity.getUniqueId();
        String barName = "entity_health_" + entityId;

        // Удаляем старый боссбар, если он существует
        BossBarInfo oldBar = activeBars.get(entityId);
        if (oldBar != null) {
            oldBar.remove(); // Используем встроенный метод удаления
        }

        // Создаём новый боссбар
        BossBarInfo barInfo = new BossBarInfo(plugin, player, barName);

        // Настраиваем параметры
        barInfo.setPercentage(maxHealth, currentHealth);
        barInfo.setTitleOfBar(ChatColor.RED + entity.getType().name() + " " +
                ChatColor.WHITE + "(" + (int) currentHealth + "/" + (int) maxHealth + " HP)");
        barInfo.setColor(BarColor.RED);
        barInfo.setStyle(BarStyle.SOLID);
        barInfo.setKeepForTicks(40); // 2 секунды (40 тиков)
        barInfo.setMakeVisible(true); // Обязательно устанавливаем видимость

        // Показываем боссбар — теперь безопасно, так как createBossBar() вызывается внутри getBar()
        BossBar bossBar = barInfo.getBar();
        if (bossBar != null) {
            bossBar.addPlayer(player);
        } else {
            plugin.getLogger().severe("Failed to create BossBar for entity: " + entityId);
            return;
        }

        // Сохраняем в кэш
        activeBars.put(entityId, barInfo);

        // Запускаем таймер на удаление через 2 секунды
        CAPIMainScheduler.runTaskLater(plugin, () -> {
            // Удаляем из кэша
            activeBars.remove(entityId);
            // Скрываем боссбар через метод remove() класса BossBarInfo
            barInfo.remove();
        }, 40L); // 40 тиков = 2 секунды
    }
}
package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vanish implements CommandExecutor {
    private final CatAPI plugin;
    private final Map<UUID, BossBar> vanishBossBars = new HashMap<>();
    private final Map<UUID, String> originalPlayerListNames = new HashMap<>(); // Хранит оригинальные имена для восстановления

    public Vanish(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("playerOnly"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("catapi.vanish")) {
            player.sendMessage(plugin.getMessage("noPermissionVanish"));
            return true;
        }

        toggleVanish(player);
        return true;
    }

    private void toggleVanish(Player player) {
        UUID playerId = player.getUniqueId();

        if (vanishBossBars.containsKey(playerId)) {
            disableVanish(player, playerId);
        } else {
            enableVanish(player, playerId);
        }
    }

    private void enableVanish(Player player, UUID playerId) {
        // Сохраняем оригинальное имя для таба
        originalPlayerListNames.put(playerId, player.getPlayerListName());

        // Скрываем игрока от всех онлайн‑игроков
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }

        // Скрываем имя игрока в табе (для всех игроков)
        player.setPlayerListName(null);

        // Создаём боссбар
        NamespacedKey bossBarKey = new NamespacedKey(plugin, "vanish." + playerId.toString());
        BossBar bossBar = Bukkit.createBossBar(
                bossBarKey,
                plugin.getMessage("vanishBossBarTitle"),
                BarColor.PURPLE,
                BarStyle.SOLID
        );
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);

        // Сохраняем боссбар в мапе
        vanishBossBars.put(playerId, bossBar);

        // Даём бесконечное ночное зрение (уровень 3)
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                2,
                false,
                false
        ));

        player.sendMessage(plugin.getMessage("vanishEnabled"));
        plugin.getLogger().info("Игрок " + player.getName() + " вошёл в режим ваниш");
    }

    private void disableVanish(Player player, UUID playerId) {
        // Показываем игрока всем онлайн‑игрокам
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.showPlayer(plugin, player);
            }
        }

        // Восстанавливаем оригинальное имя в табе
        String originalName = originalPlayerListNames.remove(playerId);
        if (originalName != null) {
            player.setPlayerListName(originalName);
        }

        // Удаляем боссбар
        BossBar bossBar = vanishBossBars.remove(playerId);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }

        // Снимаем эффект ночного зрения
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        player.sendMessage(plugin.getMessage("vanishDisabled"));
        plugin.getLogger().info("Игрок " + player.getName() + " вышел из режима ваниш");
    }
}
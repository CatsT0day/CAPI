package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Feed implements CommandExecutor {
    private final CatAPI plugin;

    public Feed(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (!player.hasPermission("catapi.feed")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        int currentFood = player.getFoodLevel();
        if (currentFood >= 20) {
            player.sendMessage(plugin.getMessage("feedSuccess"));
            return true;
        }

        new BukkitRunnable() {
            int food = currentFood;

            @Override
            public void run() {
                if (food >= 20 || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                food = Math.min(food + 2, 20);
                player.setFoodLevel(food);
                if (food == 20) {
                    player.sendMessage(plugin.getMessage("feedSuccess"));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 тиков = 1 секунда

        return true;
    }
}
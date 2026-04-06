package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Heal implements CommandExecutor {
    private final CatAPI plugin;

    public Heal(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            target = (Player) sender;
        } else {
            if (!sender.hasPermission("catapi.heal.others")) {
                sender.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("playerNotFound"));
                return true;
            }
        }

        double currentHealth = target.getHealth();
        double maxHealth = target.getMaxHealth();

        if (currentHealth >= maxHealth) {
            if (target == sender) {
                target.sendMessage(plugin.getMessage("healSuccess"));
            } else {
                target.sendMessage(plugin.getMessage("healTargeted").replace("%s", sender.getName()));
                sender.sendMessage(plugin.getMessage("healTargeted").replace("%s", target.getName()));
            }
            return true;
        }

        new BukkitRunnable() {
            double health = currentHealth;

            @Override
            public void run() {
                if (health >= maxHealth || !target.isOnline()) {
                    this.cancel();
                    if (target.isOnline() && health >= maxHealth) {
                        if (target == sender) {
                            target.sendMessage(plugin.getMessage("healSuccess"));
                        } else {
                            target.sendMessage(plugin.getMessage("healTargeted").replace("%s", sender.getName()));
                            sender.sendMessage(plugin.getMessage("healTargeted").replace("%s", target.getName()));
                        }
                    }
                    return;
                }
                health = Math.min(health + 2.0, maxHealth);
                target.setHealth(health);
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 тиков = 1 секунда

        return true;
    }
}
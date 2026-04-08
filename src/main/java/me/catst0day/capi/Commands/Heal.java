package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

public class Heal extends CommandTemplate implements Listener {

    private BukkitRunnable healingTask;

    public Heal(CAPI plugin) {
        super(plugin, "heal",  List.of(),"catapi.heal", true, 120L);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (healingTask != null) {
            healingTask.cancel();
        }

        Player target;
        if (args.length == 0) {
            target = player;
        } else {
            if (!player.hasPermission("catapi.heal.others")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getMessage("playerNotFound"));
                return true;
            }
        }

        double currentHealth = target.getHealth();
        double maxHealth = target.getMaxHealth();

        if (currentHealth >= maxHealth) {
            if (target == player) {
                target.sendMessage(plugin.getMessage("healSuccess"));
            } else {
                target.sendMessage(plugin.getMessage("healTargeted").replace("%s", player.getName()));
                player.sendMessage(plugin.getMessage("healTargeted").replace("%s", target.getName()));
            }
            return true;
        }

        healingTask = new BukkitRunnable() {
            double health = currentHealth;
            Player taskTarget = target;

            @Override
            public void run() {
                if (health >= maxHealth || !taskTarget.isOnline()) {
                    this.cancel();
                    if (taskTarget.isOnline() && health >= maxHealth) {
                        if (taskTarget == player) {
                            taskTarget.sendMessage(plugin.getMessage("healSuccess"));
                        } else {
                            taskTarget.sendMessage(plugin.getMessage("healTargeted")
                                    .replace("%s", player.getName()));
                            player.sendMessage(plugin.getMessage("healTargeted")
                                    .replace("%s", taskTarget.getName()));
                        }
                    }
                    return;
                }
                health = Math.min(health + 2.0, maxHealth);
                taskTarget.setHealth(health);
            }
        };
        healingTask.runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damagedPlayer = (Player) event.getEntity();


        if (isPlayerHealing(damagedPlayer)) {
            stopHealing(damagedPlayer);
            damagedPlayer.sendMessage(plugin.getMessage("healDamaged"));
        }
    }


    private boolean isPlayerHealing(Player player) {
        return healingTask != null;
    }


    private void stopHealing(Player player) {
        if (healingTask != null) {
            healingTask.cancel();
            healingTask = null;
        }
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            // Предлагаем имена онлайн‑игроков
            return getOnlinePlayerNames(args[0]);
        }
        return null;
    }
}
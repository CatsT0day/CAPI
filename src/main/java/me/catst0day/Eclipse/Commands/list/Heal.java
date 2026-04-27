package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Schedulers.EclipseScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.*;

public class Heal extends CommandTemplate implements Listener {

    private final Map<UUID, Integer> healingTasks = new HashMap<>();

    public Heal(Eclipse plugin) {
        super(plugin, "heal", List.of(), EclipsePermissionManager.CAPIPermissions.HEAL, true, 0, "Restore health over time");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        Player target = (args.length == 0) ? player : Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (target != player && !player.hasPermission("catapi.heal.others")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        stopHealing(target.getUniqueId());

        double maxHealth = target.getMaxHealth();
        if (target.getHealth() >= maxHealth) {
            sendSuccessMessages(player, target, "healSuccess", "healTargeted");
            return true;
        }

        int taskId = EclipseScheduler.scheduleSyncRepeatingTask(
                plugin,
                () -> {
                    if (!target.isOnline()) {
                        stopHealing(target.getUniqueId());
                        return;
                    }

                    double health = target.getHealth();
                    if (health >= maxHealth) {
                        stopHealing(target.getUniqueId());
                        sendSuccessMessages(player, target, "healSuccess", "healTargeted");
                        return;
                    }

                    target.setHealth(Math.min(health + 2.0, maxHealth));
                },
                0L,
                20L
        ).getTaskId();

        healingTasks.put(target.getUniqueId(), taskId);
        return true;
    }

    private void sendSuccessMessages(Player sender, Player target, String selfKey, String targetKey) {
        if (target == sender) {
            target.sendMessage(plugin.getMessage(selfKey));
        } else {
            target.sendMessage(plugin.getMessage(targetKey).replace("%s", sender.getName()));
            sender.sendMessage(plugin.getMessage(targetKey).replace("%s", target.getName()));
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player p) {
            if (healingTasks.containsKey(p.getUniqueId())) {
                stopHealing(p.getUniqueId());
                p.sendMessage(plugin.getMessage("healDamaged"));
            }
        }
    }

    private void stopHealing(UUID uuid) {
        Integer taskId = healingTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
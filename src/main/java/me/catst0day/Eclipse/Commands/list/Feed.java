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

public class Feed extends CommandTemplate implements Listener {

    private final Map<UUID, Integer> feedingTasks = new HashMap<>();

    public Feed(Eclipse plugin) {
        super(plugin, "feed", List.of("eat"), EclipsePermissionManager.CAPIPermissions.FEED, true, 0, "Restore hunger over time");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        Player target = (args.length == 0 || args[0].equalsIgnoreCase("self"))
                ? player : Bukkit.getPlayer(args[0]);

        if (target == null) {
            plugin.sendCFGmessage(player, plugin.getMessage("playerNotFound"));
            return true;
        }

        if (target != player && !player.hasPermission("catapi.feed.others")) {
            plugin.sendCFGmessage(player, plugin.getMessage("noPermission"));
            return true;
        }

        stopFeeding(target.getUniqueId());

        if (target.getFoodLevel() >= 20) {
            sendSuccessMessages(player, target, "feedSuccess", "feedTargeted");
            return true;
        }

        int taskId = EclipseScheduler.scheduleSyncRepeatingTask(
                plugin,
                () -> {
                    if (!target.isOnline()) {
                        stopFeeding(target.getUniqueId());
                        return;
                    }

                    int food = target.getFoodLevel();
                    if (food >= 20) {
                        stopFeeding(target.getUniqueId());
                        sendSuccessMessages(player, target, "feedSuccess", "feedTargeted");
                        return;
                    }

                    target.setFoodLevel(Math.min(food + 2, 20));
                },
                0L,
                20L
        ).getTaskId();

        feedingTasks.put(target.getUniqueId(), taskId);
        return true;
    }

    private void sendSuccessMessages(Player sender, Player target, String selfKey, String targetKey) {
        if (target == sender) {
            plugin.sendCFGmessage(target, plugin.getMessage(selfKey));
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
            if (feedingTasks.containsKey(p.getUniqueId())) {
                stopFeeding(p.getUniqueId());
                plugin.sendCFGmessage(p, plugin.getMessage("feedDamaged"));
            }
        }
    }

    private void stopFeeding(UUID uuid) {
        Integer taskId = feedingTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.getName().toLowerCase().startsWith(prefix)) completions.add(p.getName());
            });
            if ("self".startsWith(prefix)) completions.add("self");
            return completions;
        }
        return List.of();
    }
}
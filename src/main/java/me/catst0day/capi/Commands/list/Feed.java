package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Shedulers.CAPIMainScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.List;
import java.util.ArrayList;

@CAPICommandAnnotation(
        name = "feed",
        aliases = {"f"},
        permission = CAPIPermissionManager.CAPIPerm.FEED,
        requirePlayer = true,
        cooldownSeconds = 5000,
        description = "feed yourself or somebody else (1 feed point per second, if you get damaged the feeding will stop)"
)
public class Feed extends CAPICommandTemplate implements Listener {
    private String successMsg;
    private String targetMsg;
    private String notFoundMsg;
    private String noPermissionMsg;
    private String damagedMsg;

    private int feedingTaskId = -1; // ID задачи в планировщике

    public Feed(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of("self", "target"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void get(CAPI plugin) {
        successMsg = plugin.getMessage("feedSuccess");
        targetMsg = plugin.getMessage("feedTargeted");
        notFoundMsg = plugin.getMessage("playerNotFound");
        noPermissionMsg = plugin.getMessage("noPermission");
        damagedMsg = plugin.getMessage("feedDamaged");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (feedingTaskId != -1) {
            CAPIMainScheduler.runTaskLater(plugin, () -> {
                CAPIMainScheduler.scheduleSyncRepeatingTask(plugin, null, 0, 0);
            }, 0);
            feedingTaskId = -1;
        }

        Player target = player;
        if (args.length > 0 && !args[0].equalsIgnoreCase("self")) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                plugin.sendCFGmessage(player, notFoundMsg);
                return true;
            }
            if (!player.hasPermission("catapi.feed.others")) {
                plugin.sendCFGmessage(player, noPermissionMsg);
                return true;
            }
        }

        int currentFood = target.getFoodLevel();
        if (currentFood >= 20) {
            if (target == player) {
                plugin.sendCFGmessage(target, successMsg);
            } else {
                plugin.sendCFGmessage(target, targetMsg.replace("%s", player.getName()));
                plugin.sendCFGmessage(player, targetMsg.replace("%s", target.getName()));
            }
            return true;
        }

        Player finalTarget = target;
        feedingTaskId = CAPIMainScheduler.scheduleSyncRepeatingTask(
                plugin,
                () -> {
                    if (!finalTarget.isOnline()) {
                        stopFeeding();
                        return;
                    }

                    int food = finalTarget.getFoodLevel();
                    if (food >= 20) {
                        stopFeeding();
                        if (finalTarget == player) {
                            plugin.sendCFGmessage(finalTarget, successMsg);
                        } else {
                            plugin.sendCFGmessage(finalTarget, targetMsg.replace("%s", player.getName()));
                            plugin.sendCFGmessage(player, targetMsg.replace("%s", finalTarget.getName()));
                        }
                        return;
                    }

                    food = Math.min(food + 2, 20);
                    finalTarget.setFoodLevel(food);
                },
                0L,
                20L
        );

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String onlinePlayer : getOnlinePlayerNames()) {
                if (onlinePlayer.toLowerCase().startsWith(prefix)) {
                    completions.add(onlinePlayer);
                }
            }
            completions.add("self");
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return completions;
    }

    private List<String> getOnlinePlayerNames() {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damagedPlayer = (Player) event.getEntity();

        if (isPlayerFeeding(damagedPlayer)) {
            stopFeeding();
            plugin.sendCFGmessage(damagedPlayer, damagedMsg);
        }
    }

    private boolean isPlayerFeeding(Player player) {
        return feedingTaskId != -1;
    }

    private void stopFeeding() {
        if (feedingTaskId != -1) {
            CAPIMainScheduler.runTaskLater(plugin, () -> {
                CAPIMainScheduler.scheduleSyncRepeatingTask(plugin, null, 0, 0);
            }, 0);
            feedingTaskId = -1;
        }
    }
}
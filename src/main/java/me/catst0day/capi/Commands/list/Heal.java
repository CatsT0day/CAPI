package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Shedulers.CAPIMainScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.List;
import java.util.ArrayList;

@CAPICommandAnnotation(
        name = "heal",
        aliases = {"hl"},
        permission = CAPIPermissionManager.CAPIPerm.HEAL,
        requirePlayer = true,
        cooldownSeconds = 120,
        description = "heal yourself or somebody else (1 heal point per second, if you get damaged the healing will stop)"
)
public class Heal extends CAPICommandTemplate implements Listener {

    private int healingTaskId = -1;
    private String noPermissionMsg;
    private String playerNotFoundMsg;
    private String healSuccessMsg;
    private String healTargetedMsg;
    private String healDamagedMsg;
    private String playerOnlyMsg;

    public Heal(CAPI plugin) {
        super(plugin);
    }

    @Override
    public void get(CAPI plugin) {
        noPermissionMsg = plugin.getMessage("noPermission");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        healSuccessMsg = plugin.getMessage("healSuccess");
        healTargetedMsg = plugin.getMessage("healTargeted");
        healDamagedMsg = plugin.getMessage("healDamaged");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        // Отменяем предыдущую задачу, если она есть
        if (healingTaskId != -1) {
            CAPIMainScheduler.runTaskLater(plugin, () -> {
                CAPIMainScheduler.scheduleSyncRepeatingTask(plugin, null, 0, 0);
            }, 0);
            healingTaskId = -1;
        }

        Player target;
        if (args.length == 0) {
            target = player;
        } else {
            if (!player.hasPermission("catapi.heal.others")) {
                player.sendMessage(noPermissionMsg);
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(playerNotFoundMsg);
                return true;
            }
        }

        double currentHealth = target.getHealth();
        double maxHealth = target.getMaxHealth();

        if (currentHealth >= maxHealth) {
            if (target == player) {
                target.sendMessage(healSuccessMsg);
            } else {
                target.sendMessage(healTargetedMsg.replace("%s", player.getName()));
                player.sendMessage(healTargetedMsg.replace("%s", target.getName()));
            }
            return true;
        }

        // Запускаем новую задачу через CAPIMainScheduler
        healingTaskId = CAPIMainScheduler.scheduleSyncRepeatingTask(
                plugin,
                () -> {
                    if (!target.isOnline()) {
                        stopHealing();
                        return;
                    }

                    double health = target.getHealth();
                    if (health >= maxHealth) {
                        stopHealing();
                        if (target.isOnline()) {
                            if (target == player) {
                                target.sendMessage(healSuccessMsg);
                            } else {
                                target.sendMessage(healTargetedMsg.replace("%s", player.getName()));
                                player.sendMessage(healTargetedMsg.replace("%s", target.getName()));
                            }
                        }
                        return;
                    }

                    health = Math.min(health + 2.0, maxHealth);
                    target.setHealth(health);
                },
                0L,  // задержка перед первым выполнением (0 тиков)
                20L   // период между выполнениями (20 тиков = 1 секунда)
        );

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(playerOnlyMsg);
        return true;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player damagedPlayer)) return;

        if (isPlayerHealing(damagedPlayer)) {
            stopHealing();
            damagedPlayer.sendMessage(healDamagedMsg);
        }
    }

    private boolean isPlayerHealing(Player player) {
        return healingTaskId != -1;
    }

    private void stopHealing() {
        if (healingTaskId != -1) {
            CAPIMainScheduler.runTaskLater(plugin, () -> {
                CAPIMainScheduler.scheduleSyncRepeatingTask(plugin, null, 0, 0);
            }, 0);
            healingTaskId = -1;
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String name = onlinePlayer.getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    completions.add(name);
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return completions;
    }
}
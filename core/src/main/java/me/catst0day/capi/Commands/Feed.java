package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

public class Feed extends CommandTemplate implements Listener {

    private BukkitRunnable feedingTask;

    public Feed(CAPI plugin) {
        super(plugin, "feed",  List.of(),"catapi.feed", true, 5000L);
        // Устанавливаем аргументы для таб‑комплита
        setTabCompleteArguments(List.of("self", "target"));
        // Регистрируем слушатель событий в плагине
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (feedingTask != null) {
            feedingTask.cancel();
        }


        Player target = player;
        if (args.length > 0 && !args[0].equalsIgnoreCase("self")) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getMessage("playerNotFound"));
                return true;
            }
            if (!player.hasPermission("catapi.feed.others")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
        }

        int currentFood = target.getFoodLevel();
        if (currentFood >= 20) {
            if (target == player) {
                target.sendMessage(plugin.getMessage("feedSuccess"));
            } else {
                target.sendMessage(plugin.getMessage("feedTargeted")
                        .replace("%s", player.getName()));
                player.sendMessage(plugin.getMessage("feedTargeted")
                        .replace("%s", target.getName()));
            }
            return true;
        }

        Player finalTarget = target;
        feedingTask = new BukkitRunnable() {
            int food = currentFood;
            Player taskTarget = finalTarget;

            @Override
            public void run() {
                if (food >= 20 || !taskTarget.isOnline()) {
                    this.cancel();
                    return;
                }
                food = Math.min(food + 2, 20);
                taskTarget.setFoodLevel(food);
                if (food == 20) {
                    if (taskTarget == player) {
                        taskTarget.sendMessage(plugin.getMessage("feedSuccess"));
                    } else {
                        taskTarget.sendMessage(plugin.getMessage("feedTargeted")
                                .replace("%s", player.getName()));
                        player.sendMessage(plugin.getMessage("feedTargeted")
                                .replace("%s", taskTarget.getName()));
                    }
                }
            }
        };
        feedingTask.runTaskTimer(plugin, 0L, 20L); // 20 тиков = 1 секунда

        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            // Предлагаем варианты: self или имена онлайн‑игроков
            List<String> completions = getOnlinePlayerNames(args[0]);
            completions.add("self");
            return completions;
        }
        return null;
    }

    /**
     * Обрабатывает событие получения урона игроком
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damagedPlayer = (Player) event.getEntity();

        // Проверяем, находится ли игрок в процессе кормления
        if (isPlayerFeeding(damagedPlayer)) {
            stopFeeding(damagedPlayer);
            damagedPlayer.sendMessage(plugin.getMessage("feedDamaged"));
        }
    }

    /**
     * Проверяет, находится ли игрок в процессе кормления
     */
    private boolean isPlayerFeeding(Player player) {
        return feedingTask != null;
    }

    /**
     * Останавливает процесс кормления для игрока
     */
    private void stopFeeding(Player player) {
        if (feedingTask != null) {
            feedingTask.cancel();
            feedingTask = null;
        }
    }
}
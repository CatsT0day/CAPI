package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Schedulers.EclipseScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class ClearInventory extends CommandTemplate {

    public ClearInventory(Eclipse plugin) {
        super(plugin, "clearinventory", List.of("ci", "clear"), EclipsePermissionManager.CAPIPermissions.CLEAR, true, 0, "Clear items");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        Player target;

        if (args.length == 0) {
            target = player;
        } else {
            if (!player.hasPermission("catapi.clearinventory.others")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        executeClear(player, target);
        return true;
    }

    private void executeClear(Player sender, Player target) {
        final int[] slot = {0};
        final int totalSlots = target.getInventory().getSize();
        EclipseScheduler.scheduleSyncRepeatingTask(plugin, () -> {
            for (int i = 0; i < 4; i++) {
                if (slot[0] < totalSlots) {
                    target.getInventory().setItem(slot[0], null);
                    slot[0]++;
                }
            }

            if (slot[0] >= totalSlots) {
                target.playSound(target.getLocation(), Sound.BLOCK_SAND_BREAK, 1.0f, 1.2f);
                target.sendMessage(plugin.getMessage("ciSuccess"));
            }
        }, 0L, 1L);

        if (target != sender) {
            sender.sendMessage(plugin.getMessage("ciTarget").replace("%s", target.getName()));
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/ci <игрок>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            executeClear(null, target);
            sender.sendMessage(plugin.getMessage("ciTarget").replace("%s", target.getName()));
        } else {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
        }
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1 && player.hasPermission("catapi.clearinventory.others")) {
            return null;
        }
        return List.of();
    }
}
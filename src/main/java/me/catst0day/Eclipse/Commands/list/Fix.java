package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class Fix extends CommandTemplate {

    public Fix(Eclipse plugin) {
        super(plugin, "fix", List.of("repair"), EclipsePermissionManager.CAPIPermissions.FIX, true, 0, "Repair items in inventory");
        setTabCompleteArguments(List.of("all", "hand"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(String.format(plugin.getMessage("usage"), "/fix all | hand"));
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("all")) {
            if (!player.hasPermission("catapi.repair.all")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            fixAll(player);
        } else if (sub.equals("hand")) {
            if (!player.hasPermission("catapi.repair.hand")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            fixHand(player);
        } else {
            player.sendMessage(String.format(plugin.getMessage("usage"), "/fix all | hand"));
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    private void fixAll(Player player) {
        boolean fixed = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && isRepairable(item)) {
                repairItem(item);
                fixed = true;
            }
        }
        player.sendMessage(fixed ? plugin.getMessage("repairSuccessAll") : plugin.getMessage("repairNotSuccess"));
    }

    private void fixHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR && isRepairable(item)) {
            repairItem(item);
            player.sendMessage(plugin.getMessage("repairSuccessHand"));
        } else {
            player.sendMessage(plugin.getMessage("repairNotSuccess"));
        }
    }

    private boolean isRepairable(ItemStack item) {
        return item.getType().getMaxDurability() > 0;
    }

    private void repairItem(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable meta) {
            meta.setDamage(0);
            item.setItemMeta(meta);
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            getTabCompleteArguments().forEach(arg -> {
                if (arg.startsWith(prefix)) completions.add(arg);
            });
        }
        return completions;
    }
}
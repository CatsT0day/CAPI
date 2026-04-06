
package me.catst0day.capi.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.meta.Damageable;

public class Fix implements CommandExecutor {
    private final JavaPlugin plugin;

    public Fix(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private String getMessage(String key) {
        String message = plugin.getConfig().getString("messages." + key);
        if (message == null || message.isEmpty()) {
            return "Сообщение не найдено: " + key;
        }
        return colorize(message);
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(colorize("&cИспользуйте: /fix all или /fix hand"));
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("catapi.repair.all")) {
                player.sendMessage(getMessage("noPermission"));
                return true;
            }
            fixAll(player);
        } else if (args[0].equalsIgnoreCase("hand")) {
            if (!player.hasPermission("catapi.repair.hand")) {
                player.sendMessage(colorize("&cУ вас нет доступа к этой команде!"));
                return true;
            }
            fixHand(player);
        } else {
            player.sendMessage(colorize("&cИспользуйте: /fix all или /fix hand"));
        }
        return true;
    }

    private void fixAll(Player player) {
        boolean fixedAny = false;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && isRepairable(item)) {
                repairItem(item);
                fixedAny = true;
            }
        }

        if (fixedAny) {
            player.sendMessage(getMessage("repairSuccessAll"));
        } else {
            player.sendMessage(getMessage("repairNotSuccess"));
        }
    }

    private void fixHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() != Material.AIR && isRepairable(itemInHand)) {
            repairItem(itemInHand);
            player.sendMessage(getMessage(" repairSuccessHand"));
        } else {
            player.sendMessage(getMessage("repairNotSuccess"));
        }
    }

    private boolean isRepairable(ItemStack item) {
        Material type = item.getType();
        return type.isItem() && type.getMaxDurability() > 0;
    }

    private void repairItem(ItemStack item) {
        if (item.getItemMeta() instanceof Damageable) {
            Damageable meta = (Damageable) item.getItemMeta();
            if (meta != null) {
                meta.setDamage(0);
                try {
                    item.setItemMeta((ItemMeta) meta);
                } catch (IllegalStateException e) {
                    plugin.getLogger().warning("Cant set metadata for item: " + item.getType());
                }
            }
        }
    }
}
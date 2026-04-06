package me.catst0day.capi.Commands;

import me.catst0day.capi.CatAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

public class Enchant implements CommandExecutor {
    private final CatAPI plugin;

    public Enchant(CatAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (!player.hasPermission("catapi.enchant")) {
            player.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(plugin.getMessage("itemNoItemInHand"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("itemUsageEnchant"));
            return true;
        }

        String enchantName = args[0].toUpperCase();
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("itemLevelMustBeNumber"));
            return true;
        }

        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null) {
            player.sendMessage(plugin.getMessage("itemInvalidEnchantment").replace("%s", enchantName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(plugin.getMessage("itemEnchantSuccess")
                .replace("%s", enchantName)
                .replace("%d", String.valueOf(level)));
        return true;
    }
}
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Enchant extends CommandTemplate {

    public Enchant(Eclipse plugin) {
        super(plugin, "enchant", List.of(), EclipsePermissionManager.CAPIPermissions.ENCHANT, true, 0, "Enchant item in your hand");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemNoItemInHand"));
            return true;
        }

        if (args.length < 2) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemUsageEnchant"));
            return true;
        }

        String inputName = args[0].toLowerCase();
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemLevelMustBeNumber"));
            return true;
        }

        Enchantment enchantment = Arrays.stream(Enchantment.values()).filter(e -> e.getKey().getKey().equalsIgnoreCase(inputName)).findFirst().orElse(null);

        if (enchantment == null) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemInvalidEnchantment").replace("%s", inputName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        if (!enchantment.getKey().getNamespace().equals("minecraft")) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                String roman = (level == 1) ? "I" : (level == 2) ? "II" : (level == 3) ? "III" :
                                                                          (level == 4) ? "IV" : (level == 5) ? "V" : String.valueOf(level);

                String enchantLine = "§7" + enchantment.getName() + " " + roman;
                lore.removeIf(line -> line.contains(enchantment.getName()));
                lore.add(0, enchantLine);

                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        plugin.sendCFGmessage(player, plugin.getMessage("itemEnchantSuccess")
                .replace("%s", enchantment.getName())
                .replace("%d", String.valueOf(level)));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return perform(player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (Enchantment e : Enchantment.values()) {
                String key = e.getKey().getKey().toUpperCase();
                if (key.startsWith(prefix.toUpperCase())) {
                    completions.add(key);
                }
            }
        } else if (args.length == 2) {
            completions.addAll(List.of("1", "2", "3", "4", "5"));
        }
        return completions;
    }
}
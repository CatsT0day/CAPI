package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class Enchant extends CAPICommandTemplate {

    public Enchant(CAPI plugin) {
    super(plugin, "enchant", List.of(), CAPIPermissionManager.CAPIPerm.ENCHANT, true, 0, "Enchant itme in your hand");

        setTabCompleteArguments(List.of(
                "PROTECTION", "FIRE_PROTECTION", "FEATHER_FALLING", "BLAST_PROTECTION",
                "PROJECTILE_PROTECTION", "RESPIRATION", "AQUA_AFFINITY", "THORNS",
                "SHARPNESS", "SMITE", "BANE_OF_ARTHROPODS", "KNOCKBACK",
                "FIRE_ASPECT", "LOOTING", "EFFICIENCY", "SILK_TOUCH",
                "UNBREAKING", "FORTUNE", "POWER", "PUNCH", "FLAME", "INFINITY"
        ));
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

        String enchantName = args[0].toUpperCase();
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            plugin.sendCFGmessage(player, plugin.getMessage("itemLevelMustBeNumber"));
            return true;
        }

        @SuppressWarnings("deprecation")
        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null) {
            plugin.sendCFGmessage(player,
                    plugin.getMessage("itemInvalidEnchantment").replace("%s", enchantName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        plugin.sendCFGmessage(player,
                plugin.getMessage("itemEnchantSuccess")
                        .replace("%s", enchantName)
                        .replace("%d", String.valueOf(level)));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        // Так как requirePlayer = true, этот метод вызовется только если executeWithPlayer вернет false
        return perform(player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String enchant : getTabCompleteArguments()) {
                if (enchant.toLowerCase().startsWith(prefix)) {
                    completions.add(enchant);
                }
            }
        } else if (args.length == 2) {
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}
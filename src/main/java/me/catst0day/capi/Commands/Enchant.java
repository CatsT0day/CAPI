
package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import java.util.Arrays;
import java.util.List;

public class Enchant extends CommandTemplate {

    public Enchant(CAPI plugin) {
        super(plugin, "enchant", List.of("ench"), CAPIPermissionManager.CAPIPerm.ENCHANT, true, 10000L,"enchant item in your hand");
        setTabCompleteArguments(Arrays.asList(
                "PROTECTION",
                "FIRE_PROTECTION",
                "FEATHER_FALLING",
                "BLAST_PROTECTION",
                "PROJECTILE_PROTECTION",
                "RESPIRATION",
                "AQUA_AFFINITY",
                "THORNS",
                "SHARPNESS",
                "SMITE",
                "BANE_OF_ARTHROPODS",
                "KNOCKBACK",
                "FIRE_ASPECT",
                "LOOTING",
                "EFFICIENCY",
                "SILK_TOUCH",
                "UNBREAKING",
                "FORTUNE",
                "POWER",
                "PUNCH",
                "FLAME",
                "INFINITY"
        ));
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(plugin.getMessage("itemNoItemInHand"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("usage")
                    .replace("%s", "/enchant <ench> <lvl>"));
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
            player.sendMessage(plugin.getMessage("itemInvalidEnchantment")
                    .replace("%s", enchantName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(plugin.getMessage("itemEnchantSuccess")
                .replace("%s", enchantName)
                .replace("%d", String.valueOf(level)));
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return filterByInput(args[0], tabCompleteArguments);
        }
        return null;
    }
}
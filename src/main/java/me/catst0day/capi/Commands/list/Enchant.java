package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CAPICommandAnnotation(
        name = "enchant",
        aliases = {"ench"},
        permission = CAPIPermissionManager.CAPIPerm.ENCHANT,
        requirePlayer = true,
        cooldownSeconds = 10000,
        description = "enchant item in your hand"
)
public class Enchant extends CAPICommandTemplate {
    private String successMsg;
    private String errorMsg;
    private String usageMsg;

    public Enchant(CAPI plugin) {
        super(plugin);
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
    public void get(CAPI plugin) {
        successMsg = plugin.getMessage("itemEnchantSuccess");
        errorMsg = plugin.getMessage("commandError");
        usageMsg = plugin.getMessage("itemUsageEnchant");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            plugin.sendCFGmessage(player, "itemNoItemInHand");
            return true;
        }

        if (args.length < 2) {
            plugin.sendCFGmessage(player, usageMsg);
            return true;
        }

        String enchantName = args[0].toUpperCase();
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            plugin.sendCFGmessage(player, "itemLevelMustBeNumber");
            return true;
        }

        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null) {
            plugin.sendCFGmessage(player,
                    plugin.getMessage("itemInvalidEnchantment").replace("%s", enchantName));
            return true;
        }

        item.addUnsafeEnchantment(enchantment, level);
        plugin.sendCFGmessage(player,
                successMsg.replace("%s", enchantName).replace("%d", String.valueOf(level)));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendCFGmessage(sender, "playerOnlyCommand");
            return true;
        }
        return perform((Player) sender, args);
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
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        } else if (args.length == 2) {
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
        }

        return completions;
    }
}
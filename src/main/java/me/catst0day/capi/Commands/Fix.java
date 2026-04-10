package me.catst0day.capi.Commands;

import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class Fix extends CommandTemplate {

    public Fix(CAPI plugin) {
        super(plugin, "fix",  List.of("repair"), CAPIPermissionManager.CAPIPerm.FIX, true, 5000L, "fix all/inhand items");
        setTabCompleteArguments(Arrays.asList("all", "hand"));
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            String usageMessage = plugin.getMessage("usage");
            player.sendMessage(String.format(usageMessage, "/fix all или /fix hand"));
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("catapi.repair.all")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            fixAll(player);
        } else if (args[0].equalsIgnoreCase("hand")) {
            if (!player.hasPermission("catapi.repair.hand")) {
                player.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }
            fixHand(player);
        } else {
            String usageMessage = plugin.getMessage("usage");
            player.sendMessage(String.format(usageMessage, "/fix all или /fix hand"));
        }
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        return false;
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
            player.sendMessage(plugin.getMessage("repairSuccessAll"));
        } else {
            player.sendMessage(plugin.getMessage("repairNotSuccess"));
        }
    }

    private void fixHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() != Material.AIR && isRepairable(itemInHand)) {
            repairItem(itemInHand);
            player.sendMessage(plugin.getMessage("repairSuccessHand"));
        } else {
            player.sendMessage(plugin.getMessage("repairNotSuccess"));
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
                    plugin.getLogger().warning("Не удалось установить метаданные для предмета: " + item.getType());
                }
            }
        }
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return filterByInput(args[0], tabCompleteArguments);
        }
        return null;
    }
}
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CAPICommandAnnotation(
        name = "fix",
        aliases = {"repair"},
        permission = CAPIPerm.FIX,
        requirePlayer = true,
        cooldownSeconds = 5,
        description = "Fix all/in‑hand items"
)
public class Fix extends CAPICommandTemplate {

    private String usageMsg;
    private String noPermissionMsg;
    private String repairSuccessAllMsg;
    private String repairSuccessHandMsg;
    private String repairNotSuccessMsg;

    public Fix(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(Arrays.asList("all", "hand"));
    }

    @Override
    public void get(CAPI plugin) {
        usageMsg = plugin.getMessage("usage");
        noPermissionMsg = plugin.getMessage("noPermission");
        repairSuccessAllMsg = plugin.getMessage("repairSuccessAll");
        repairSuccessHandMsg = plugin.getMessage("repairSuccessHand");
        repairNotSuccessMsg = plugin.getMessage("repairNotSuccess");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(String.format(usageMsg, "/fix all или /fix hand"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if ("all".equals(subCommand)) {
            if (!player.hasPermission("catapi.repair.all")) {
                player.sendMessage(noPermissionMsg);
                return true;
            }
            fixAll(player);
        } else if ("hand".equals(subCommand)) {
            if (!player.hasPermission("catapi.repair.hand")) {
                player.sendMessage(noPermissionMsg);
                return true;
            }
            fixHand(player);
        } else {
            player.sendMessage(String.format(usageMsg, "/fix all или /fix hand"));
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
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
            player.sendMessage(repairSuccessAllMsg);
        } else {
            player.sendMessage(repairNotSuccessMsg);
        }
    }

    private void fixHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() != Material.AIR && isRepairable(itemInHand)) {
            repairItem(itemInHand);
            player.sendMessage(repairSuccessHandMsg);
        } else {
            player.sendMessage(repairNotSuccessMsg);
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
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String option : tabCompleteArguments) {
                if (option.startsWith(prefix)) {
                    completions.add(option);
                }
            }
        }
        return completions;
    }
}
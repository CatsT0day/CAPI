package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Entity.Player.EclipsePlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class EcSee extends CommandTemplate implements Listener {

    public EcSee(Eclipse plugin) {
        super(plugin, "ecsee", List.of("ec"), EclipsePermissionManager.CAPIPermissions.INVSEE, true, 0L, "View/Edit player enderchest");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/ecsee <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        player.openInventory(target.getEnderChest());
        player.sendMessage(plugin.getMessage("ecseeOpened").replace("%s", target.getName()));
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInv = EclipsePlayerInventory.getTopInventory(player);
        if (topInv == null || topInv.getType() != InventoryType.ENDER_CHEST) return;
        if (event.getRawSlot() < topInv.getSize()) {
            if (!player.hasPermission("catapi.ecsee.edit")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessage("noEditPerm"));
            }
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return false;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }
}
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
import org.bukkit.inventory.Inventory;

import java.util.List;

public class InvSee extends CommandTemplate implements Listener {

    public InvSee(Eclipse plugin) {
        super(plugin, "invsee", List.of("inventory"), EclipsePermissionManager.CAPIPermissions.INVSEE, true, 0L, "View/Edit player inventory");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("usage").replace("%s", "/invsee <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        player.openInventory(target.getInventory());
        player.sendMessage(plugin.getMessage("invseeOpened").replace("%s", target.getName()));
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInv = EclipsePlayerInventory.getTopInventory(player);
        if (topInv == null) return;
        if (topInv.getHolder() instanceof Player target && !target.getUniqueId().equals(player.getUniqueId())) {
            if (event.getRawSlot() < topInv.getSize()) {
                if (!player.hasPermission("catapi.invsee.edit")) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getMessage("noEditPerm"));
                }
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
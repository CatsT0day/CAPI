package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import java.util.List;

public class InvSee extends CAPICommandTemplate implements Listener {

    public InvSee(CAPI plugin) {
        super(plugin, "invsee", List.of("inventory"), CAPIPermissionManager.CAPIPerm.INVSEE, true, 0L, "View/Edit player inventory");
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

        // Открываем РЕАЛЬНЫЙ инвентарь для синхронизации
        player.openInventory(target.getInventory());
        player.sendMessage(plugin.getMessage("invseeOpened").replace("%s", target.getName()));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInv = event.getView().getTopInventory();

        // Проверяем, что это инвентарь игрока (не GUI и не сундук)
        if (topInv.getHolder() instanceof Player target && !target.equals(player)) {
            // Если клик в верхнем слоте (чужой инвентарь)
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
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }
}
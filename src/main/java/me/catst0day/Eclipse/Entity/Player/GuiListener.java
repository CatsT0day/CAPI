package me.catst0day.Eclipse.Entity.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Gui gui = GuiManager.getInstance().getGui(player);
        if (gui == null || event.getInventory() != gui.getInventory()) return;
        event.setCancelled(true);
        event.setCurrentItem(null);
        event.setCursor(null);
        if (event.isShiftClick()) return;
        int slot = event.getSlot();
        if (slot < 0 || slot >= gui.getInventory().getSize()) return;

        GuiButton button = gui.getButtons().get(slot);

        if (button != null) {
            boolean isRightClick = event.isRightClick();
            button.executeClick(player, isRightClick);
            gui.update();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Gui gui = GuiManager.getInstance().getGui(player);

        if (gui != null && event.getInventory() == gui.getInventory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        GuiManager.getInstance().unregisterGui(player);
    }
}
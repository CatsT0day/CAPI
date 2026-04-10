package me.catst0day.capi.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class CAPIGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        CAPIGui gui = CAPIGuiManager.getInstance().getGui(player);

        // Проверяем, что это наш GUI — сравниваем сам инвентарь
        if (gui == null || event.getInventory() != gui.getInventory()) return;

        // ПОЛНАЯ блокировка: отменяем все действия
        event.setCancelled(true);

        // Гарантированно блокируем перемещение предметов
        event.setCurrentItem(null);
        event.setCursor(null);

        // Блокируем все виды Shift‑кликов
        if (event.isShiftClick()) return;

        // Проверяем валидность слота
        int slot = event.getSlot();
        if (slot < 0 || slot >= gui.getInventory().getSize()) return;

        CAPIGuiButton button = gui.getButtons().get(slot);

        if (button != null) {
            boolean isRightClick = event.isRightClick();
            button.executeClick(player, isRightClick);
            gui.update(); // Обновляем GUI после клика
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        CAPIGui gui = CAPIGuiManager.getInstance().getGui(player);

        if (gui != null && event.getInventory() == gui.getInventory()) {
            // ПОЛНАЯ блокировка перетаскивания
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        CAPIGuiManager.getInstance().unregisterGui(player);
    }
}
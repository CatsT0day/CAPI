package me.catst0day.capi.API.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CAPIGui {
    private final Player player;
    private Inventory inventory;
    private final Map<Integer, CAPIGuiButton> buttons = new HashMap<>();
    private final LinkedHashSet<CAPIGuiButton> noSlotButtons = new LinkedHashSet<>();
    private String title = "CAPI GUI";
    private int rows = 3;

    public CAPIGui(Player player, String title, int rows) {
        this.player = player;
        this.title = title;
        this.rows = Math.min(rows, 6);
        // Создаём инвентарь без попытки установить holder
        this.inventory = Bukkit.createInventory(null, this.rows * 9, title);
    }

    public void open() {
        autoResize();
        inventory.clear(); // Очищаем перед заполнением
        for (Map.Entry<Integer, CAPIGuiButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        player.openInventory(inventory);
        CAPIGuiManager.getInstance().registerGui(player, this);
    }

    public void update() {
        if (inventory == null) return;
        inventory.clear();
        for (Map.Entry<Integer, CAPIGuiButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        player.updateInventory(); // Обязательно обновляем инвентарь игрока
    }

    // Остальные методы без изменений...
    public void addButton(int slot, CAPIGuiButton button) {
        button.setGui(this);
        buttons.put(slot, button);
    }

    public void addButtonWithoutSlot(CAPIGuiButton button) {
        button.setGui(this);
        noSlotButtons.add(button);
    }

    private void combineButtons() {
        for (CAPIGuiButton button : noSlotButtons) {
            for (int i = 0; i < 54; i++) {
                if (!buttons.containsKey(i)) {
                    button.setSlot(i);
                    buttons.put(i, button);
                    break;
                }
            }
        }
        noSlotButtons.clear();
    }

    public void autoResize() {
        combineButtons();
        Optional<Integer> maxSlotOpt = buttons.keySet().stream().max(Integer::compareTo);
        if (maxSlotOpt.isPresent()) {
            int maxSlot = maxSlotOpt.get();
            this.rows = (maxSlot / 9) + 1;
            if (rows > 6) rows = 6;
        } else {
            this.rows = 3; // дефолтный размер, если кнопок нет
        }
    }

    // Геттеры
    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }
    public Map<Integer, CAPIGuiButton> getButtons() { return buttons; }
    public void setTitle(String title) { this.title = title; }
    public void setRows(int rows) { this.rows = Math.min(rows, 6); }
}
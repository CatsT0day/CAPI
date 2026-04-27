package me.catst0day.Eclipse.Entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Optional;

public class Gui {
    private final Player player;
    private Inventory inventory;
    private final Map<Integer, GuiButton> buttons = new HashMap<>();
    private final LinkedHashSet<GuiButton> noSlotButtons = new LinkedHashSet<>();
    private String title = "guitestname";
    private int rows = 3;

    public Gui(Player player, String title, int rows) {
        this.player = player;
        this.title = title;
        this.rows = Math.min(rows, 6);
        this.inventory = Bukkit.createInventory(null, this.rows * 9, title);
    }

    public void open() {
        autoResize();
        inventory.clear();
        for (Map.Entry<Integer, GuiButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        player.openInventory(inventory);
        GuiManager.getInstance().registerGui(player, this);
    }

    public void update() {
        if (inventory == null) return;
        inventory.clear();
        for (Map.Entry<Integer, GuiButton> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        player.updateInventory();
    }

    public void addButton(int slot, GuiButton button) {
        button.setGui(this);
        buttons.put(slot, button);
    }

    public void addButtonWithoutSlot(GuiButton button) {
        button.setGui(this);
        noSlotButtons.add(button);
    }

    private void combineButtons() {
        for (GuiButton button : noSlotButtons) {
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
            this.rows = 3;
        }
    }

    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }
    public Map<Integer, GuiButton> getButtons() { return buttons; }
    public void setTitle(String title) { this.title = title; }
    public void setRows(int rows) { this.rows = Math.min(rows, 6); }
}
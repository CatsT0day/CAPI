package me.catst0day.capi.API.GUI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CAPIGuiButton {
    private ItemStack item;
    private Integer slot;
    private final List<String> lore = new ArrayList<>();
    private Consumer<Player> onClick;      // ЛКМ
    private Consumer<Player> onRightClick; // ПКМ
    private boolean closeOnClick = false;
    private CAPIGui gui;

    public CAPIGuiButton(Material material) {
        this.item = new ItemStack(material);
    }

    public CAPIGuiButton setName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return this;
    }

    public CAPIGuiButton addLore(String line) {
        lore.add(line);
        updateLore();
        return this;
    }

    private void updateLore() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    /** Действие при клике левой кнопкой мыши */
    public CAPIGuiButton onLeftClick(Consumer<Player> action) {
        this.onClick = action;
        return this;
    }

    /** Действие при клике правой кнопкой мыши */
    public CAPIGuiButton onRightClick(Consumer<Player> action) {
        this.onRightClick = action;
        return this;
    }

    /** Закрывать GUI после клика */
    public CAPIGuiButton closeOnClick() {
        this.closeOnClick = true;
        return this;
    }

    public ItemStack getItem() {
        return item;
    }


    public void executeClick(Player player, boolean isRightClick) {
        try {
            if (isRightClick && onRightClick != null) {
                onRightClick.accept(player);
            } else if (!isRightClick && onClick != null) {
                onClick.accept(player);
            }
            if (closeOnClick) player.closeInventory();
        } catch (Exception e) {
            player.sendMessage("§cОшибка при выполнении команды!");
            e.printStackTrace();
        }
    }

    // Сеттеры для GUI и слота
    public void setGui(CAPIGui gui) { this.gui = gui; }
    public void setSlot(Integer slot) { this.slot = slot; }
    public Integer getSlot() { return slot; }
}
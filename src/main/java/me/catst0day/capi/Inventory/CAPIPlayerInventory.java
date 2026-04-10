package me.catst0day.capi.Inventory;

import java.lang.reflect.Method;


import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.catst0day.capi.Enums.Version;

public class CAPIPlayerInventory {

    private static Method topInventory = null;

    public static ItemStack[] getTopInventoryContents(Player player) {
        Inventory inv = getTopInventory(player);
        if (inv == null)
            return new ItemStack[54];
        return inv.getContents();
    }

    public static Inventory getTopInventory(Player player) {
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R1))
            return player.getOpenInventory().getTopInventory();
        try {
            InventoryView inv = player.getOpenInventory();
            if (topInventory == null)
                topInventory = InventoryView.class.getMethod("getTopInventory");
            return ((Inventory) topInventory.invoke(inv));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

}


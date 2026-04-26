package me.catst0day.capi.GUI;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;


public class CAPIGuiManager {
    private static final CAPIGuiManager instance = new CAPIGuiManager();
    private final Map<Player, CAPIGui> openGuis = new HashMap<>();

    private CAPIGuiManager() {}

    public static CAPIGuiManager getInstance() {
        return instance;
    }



    public void registerGui(Player player, CAPIGui gui) {
        openGuis.put(player, gui);
    }


    public void unregisterGui(Player player) {
        openGuis.remove(player);
    }


    public CAPIGui getGui(Player player) {
        return openGuis.get(player);
    }


    public boolean isGuiOpen(Player player) {
        return openGuis.containsKey(player);
    }


    public void closeGui(Player player) {
        CAPIGui gui = getGui(player);
        if (gui != null) {
            player.closeInventory();
            unregisterGui(player);
        }
    }

    public void closeAllGuis() {
        for (Player player : openGuis.keySet()) {
            player.closeInventory();
        }
        openGuis.clear();
    }


    public int getOpenGuiCount() {
        return openGuis.size();
    }


    public Collection<CAPIGui> getAllOpenGuis() {
        return Collections.unmodifiableCollection(openGuis.values());
    }


    public <T extends CAPIGui> void updateAllGuisOfType(Class<T> guiClass) {
        for (CAPIGui gui : openGuis.values()) {
            if (guiClass.isInstance(gui)) {
                gui.update();
            }
        }
    }
}

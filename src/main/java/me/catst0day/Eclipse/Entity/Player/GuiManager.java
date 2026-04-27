package me.catst0day.Eclipse.Entity.Player;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;


public class GuiManager {
    private static final GuiManager instance = new GuiManager();
    private final Map<Player, Gui> openGuis = new HashMap<>();

    private GuiManager() {}

    public static GuiManager getInstance() {
        return instance;
    }



    public void registerGui(Player player, Gui gui) {
        openGuis.put(player, gui);
    }


    public void unregisterGui(Player player) {
        openGuis.remove(player);
    }


    public Gui getGui(Player player) {
        return openGuis.get(player);
    }


    public boolean isGuiOpen(Player player) {
        return openGuis.containsKey(player);
    }


    public void closeGui(Player player) {
        Gui gui = getGui(player);
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


    public Collection<Gui> getAllOpenGuis() {
        return Collections.unmodifiableCollection(openGuis.values());
    }


    public <T extends Gui> void updateAllGuisOfType(Class<T> guiClass) {
        for (Gui gui : openGuis.values()) {
            if (guiClass.isInstance(gui)) {
                gui.update();
            }
        }
    }
}

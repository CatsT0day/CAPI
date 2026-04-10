package me.catst0day.capi.EventListeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;

import static me.catst0day.capi.Utils.Util.log;

public class CAPIHideAchievements {
    private void hideAdvancementsFor(World world) {
        world.setGameRuleValue("announceAdvancements", "false");
        log("Achievements are hidden for world '" + world.getName() + "'.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        hideAdvancementsFor(event.getWorld());
    }
}

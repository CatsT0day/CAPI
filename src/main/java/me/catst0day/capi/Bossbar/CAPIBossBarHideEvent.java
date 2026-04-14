package me.catst0day.capi.Bossbar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.catst0day.capi.Annotations.Events.EventAnnotation;

public final class CAPIBossBarHideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private CAPIBossBarInfo bossBar = null;

    public CAPIBossBarHideEvent(CAPIBossBarInfo bossBar) {
        this.bossBar = bossBar;
    }

    @Override
    @EventAnnotation(info = "boss bar should be hidden. Can be canceled.")
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public final boolean isCancelled() {
        return cancel;
    }

    public CAPIBossBarInfo getBossBar() {
        return bossBar;
    }

    public void setBossBar(CAPIBossBarInfo bossBar) {
        this.bossBar = bossBar;
    }
}
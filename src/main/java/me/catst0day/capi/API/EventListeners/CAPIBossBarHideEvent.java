package me.catst0day.capi.EventListeners;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.catst0day.capi.Events.EventAnnotation;

public final class CAPIBossBarHideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private BossBarInfo bossBar = null;

    public CAPIBossBarHideEvent(BossBarInfo bossBar) {
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

    public BossBarInfo getBossBar() {
        return bossBar;
    }

    public void setBossBar(BossBarInfo bossBar) {
        this.bossBar = bossBar;
    }
}
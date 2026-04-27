package me.catst0day.Eclipse.Bossbar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CAPIBossBarHideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private CAPIBossBar bossBar = null;

    public CAPIBossBarHideEvent(CAPIBossBar bossBar) {
        this.bossBar = bossBar;
    }

    @Override
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

    public CAPIBossBar getBossBar() {
        return bossBar;
    }

    public void setBossBar(CAPIBossBar bossBar) {
        this.bossBar = bossBar;
    }
}
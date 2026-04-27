package me.catst0day.Eclipse.Bossbar;

import org.bukkit.boss.BarStyle;

public enum CAPIBarStyle {
    SOLID,
    SEGMENTED_6,
    SEGMENTED_10,
    SEGMENTED_12,
    SEGMENTED_20;

    private CAPIBarStyle() {
    }

    public BarStyle toBukkitStyle() {
        switch (this) {
            case SOLID: return BarStyle.SOLID;
            case SEGMENTED_6: return BarStyle.SEGMENTED_6;
            case SEGMENTED_10: return BarStyle.SEGMENTED_10;
            case SEGMENTED_12: return BarStyle.SEGMENTED_12;
            case SEGMENTED_20: return BarStyle.SEGMENTED_20;
            default: return BarStyle.SOLID;
        }
    }
}
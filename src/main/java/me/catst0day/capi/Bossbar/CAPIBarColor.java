
package me.catst0day.capi.Bossbar;

import org.bukkit.boss.BarColor;

public enum CAPIBarColor {
    PINK,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    WHITE;

    private CAPIBarColor() {
    }
    public BarColor toBukkitColor() {
        switch (this) {
            case PINK: return BarColor.PINK;
            case BLUE: return BarColor.BLUE;
            case RED: return BarColor.RED;
            case GREEN: return BarColor.GREEN;
            case YELLOW: return BarColor.YELLOW;
            case PURPLE: return BarColor.PURPLE;
            case WHITE: default: return BarColor.WHITE;
        }
    }
}
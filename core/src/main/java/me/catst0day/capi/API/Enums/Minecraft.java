
package me.catst0day.capi.API.Enums;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum Minecraft {
    craftbukkit(false),
    spigot(false),
    paper(true),
    folia(true),
    purpur(true),
    tuinity(true),
    yatopia(true),
    tacospigot(true),
    glowstone(false),
    pufferfish(true),
    airplane(true),
    magma(true),
    fabric(false),
    arclight(true),
    mohist(true);

    private final boolean isAsync;

    Minecraft(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }
}

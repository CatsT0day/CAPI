
package me.catst0day.capi.Enums;


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

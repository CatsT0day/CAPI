package me.catst0day.capi.API.Enums;

import org.bukkit.potion.PotionEffect;

public class CAPIPotionEffect {
    private final CAPIPotionEffectType type;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    public CAPIPotionEffect(CAPIPotionEffectType type, int duration, int amplifier,
                            boolean ambient, boolean particles, boolean icon) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particles = particles;
        this.icon = icon;
    }

    public PotionEffect toBukkitEffect() {
        return new PotionEffect(
                type.getBukkitEffect(),
                duration,
                amplifier,
                ambient,
                particles,
                icon
        );
    }

    public CAPIPotionEffectType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean hasParticles() {
        return particles;
    }

    public boolean hasIcon() {
        return icon;
    }
}

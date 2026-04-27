package me.catst0day.Eclipse.Enums;

import org.bukkit.potion.PotionEffect;

public class EclipsePotionEffect {
    private final EclipsePotionEffectType type;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    public EclipsePotionEffect(EclipsePotionEffectType type, int duration, int amplifier,
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

    public EclipsePotionEffectType getType() {
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

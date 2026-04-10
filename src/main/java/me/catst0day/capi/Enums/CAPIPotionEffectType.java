
package me.catst0day.capi.Enums;

import org.bukkit.potion.PotionEffectType;

public enum CAPIPotionEffectType {
    SPEED(PotionEffectType.SPEED),
    SLOWNESS(PotionEffectType.SLOW),
    FAST_DIGGING(PotionEffectType.FAST_DIGGING),
    SLOW_DIGGING(PotionEffectType.SLOW_DIGGING),
    INCREASE_DAMAGE(PotionEffectType.INCREASE_DAMAGE),
    HEAL(PotionEffectType.HEAL),
    HARM(PotionEffectType.HARM),
    JUMP(PotionEffectType.JUMP),
    CONFUSION(PotionEffectType.CONFUSION),
    REGENERATION(PotionEffectType.REGENERATION),
    DAMAGE_RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING),
    INVISIBILITY(PotionEffectType.INVISIBILITY),
    BLINDNESS(PotionEffectType.BLINDNESS),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION),
    HUNGER(PotionEffectType.HUNGER),
    WEAKNESS(PotionEffectType.WEAKNESS),
    POISON(PotionEffectType.POISON),
    WITHER(PotionEffectType.WITHER),
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST),
    ABSORPTION(PotionEffectType.ABSORPTION),
    SATURATION(PotionEffectType.SATURATION),
    GLOWING(PotionEffectType.GLOWING);

    private final PotionEffectType potionEffectType;

    CAPIPotionEffectType(PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
    }

    public PotionEffectType getBukkitEffect() {
        return potionEffectType;
    }

    public static CAPIPotionEffectType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

package me.catst0day.capi.API.Classes.Entities;

import org.bukkit.entity.EntityType;

public enum CAPIEntityType {
    ITEM("DROPPED_ITEM"),
    EXPERIENCE_ORB(),
    AREA_EFFECT_CLOUD(),
    ELDER_GUARDIAN(),
    WITHER_SKELETON(),
    STRAY(),
    EGG("THROWN_EGG"),
    LEASH_KNOT("LEASH_HITCH"),
    PAINTING(),
    ARROW(),
    SNOWBALL(),
    FIREBALL(),
    SMALL_FIREBALL(),
    ENDER_PEARL("THROWN_ENDER_PEARL"),
    ENDER_SIGNAL("END_SIGNAL"),
    EYE_OF_ENDER(),
    POTION(),
    LIGHTNING_BOLT(),
    SPLASH_POTION(),
    EXPERIENCE_BOTTLE("THROWN_EXP_BOTTLE"),
    ITEM_FRAME(),
    WITHER_SKULL(),
    TNT("PRIMED_TNT"),
    FALLING_BLOCK(),
    FIREWORK_ROCKET("FIREWORK"),
    HUSK(),
    SPECTRAL_ARROW(),
    SHULKER_BULLET(),
    DRAGON_FIREBALL(),
    ZOMBIE_VILLAGER(),
    SKELETON_HORSE(),
    ZOMBIE_HORSE(),
    ARMOR_STAND(),
    DONKEY(),
    MULE(),
    EVOKER_FANGS(),
    EVOKER(),
    VEX(),
    VINDICATOR(),
    ILLUSIONER(),
    COMMAND_BLOCK_MINECART("COMMAND_MINECART", "MINECART_COMMAND"),
    BOAT(),
    MINECART(),
    CHEST_MINECART("MINECART_CHEST"),
    FURNACE_MINECART("MINECART_FURNACE"),
    TNT_MINECART("MINECART_TNT"),
    HOPPER_MINECART("MINECART_HOPPER"),
    SPAWNER_MINECART("MINECART_MOB_SPAWNER"),
    CREEPER(),
    SKELETON(),
    SPIDER(),
    GIANT(),
    ZOMBIE(),
    SLIME(),
    GHAST(),
    PIG_ZOMBIE("ZOMBIE_PIGMAN"),
    ENDERMAN(),
    CAVE_SPIDER(),
    SILVERFISH(),
    BLAZE(),
    MAGMA_CUBE(),
    ENDER_DRAGON(),
    WITHER(),
    BAT(),
    WITCH(),
    ENDERMITE(),
    GUARDIAN(),
    SHULKER(),
    PIG();

    private final String[] secondaryNames;
    private final EntityType type;

    CAPIEntityType(String... secondaryNames) {
        this.secondaryNames = secondaryNames;
        this.type = EntityType.valueOf(this.name());
    }

    public static CAPIEntityType getByType(EntityType type) {
        for (CAPIEntityType one : values()) {
            if (one.type == type) return one;
        }
        return null;
    }

    public static CAPIEntityType getByName(String name) {
        if (name == null) return null;
        name = name.replace("_", "").toUpperCase();
        for (CAPIEntityType one : values()) {
            if (one.name().replace("_", "").equals(name)) return one;
            for (String secondary : one.secondaryNames) {
                if (secondary != null && secondary.replace("_", "").equals(name)) return one;
            }
        }
        return null;
    }

    public EntityType getType() {
        return type;
    }
}

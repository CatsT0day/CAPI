
package me.catst0day.capi.API.Classes.Entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;

public enum CAPIEntitySubType {
    TAMED,
    UNTAMED,
    BABY,
    ADULT,
    NOAI,
    AI,
    ANGRY,
    PASSIVE,
    RED_CAT,
    SIAMESE_CAT,
    WILD_OCELOT,
    BLACK_CAT,

    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK,
    RAINBOW,

    CHESTNUT,
    CREAMY,
    DARK_BROWN,

    // Rabbit
    BLACK_AND_WHITE,
    GOLD,
    SALT_AND_PEPPER,
    THE_KILLER_BUNNY,

    // Fox
    SNOW,

    // Goat
    SCREAMING,

    // Axolotl
    LUCY, WILD,

    // Panda
    AGGRESSIVE, LAZY, PLAYFUL, WEAK, WORRIED,

    // Cat
    ALL_BLACK, BRITISH_SHORTHAIR, CALICO, JELLIE, PERSIAN, RAGDOLL, SIAMESE, TABBY,

    // Slimes
    SIZE1,
    SIZE2,
    SIZE3,
    SIZE4,
    SIZE5,
    SIZE6,
    SIZE7,
    SIZE8,
    SIZE9,
    SIZE10,

    // Creeper
    POWERED,
    UNPOWERED,

    // Villagers
    NORMAL, FARMER, LIBRARIAN, PRIEST, BLACKSMITH, BUTCHER, NITWIT, HUSK,
    ARMORER, CARTOGRAPHER, CLERIC, FISHERMAN, FLETCHER, LEATHERWORKER, MASON, SHEPHERD, TOOLSMITH, WEAPONSMITH,
    DESERT, JUNGLE, PLAINS, SAVANNA, SWAMP, TAIGA;

    /**
     * Получение подтипа по имени (регистронезависимо, с игнорированием подчёркиваний)
     */
    public static CAPIEntitySubType getByName(String name) {
        if (name == null) return null;
        name = name.replace("_", "").toUpperCase();
        for (CAPIEntitySubType one : CAPIEntitySubType.values()) {
            if (one.toString().replace("_", "").equals(name)) {
                return one;
            }
        }
        return null;
    }

    /**
     * Получение списка подтипов для конкретной сущности
     */
    public static List<CAPIEntitySubType> getSubTypes(Entity ent) {
        List<CAPIEntitySubType> types = new ArrayList<>();
        try {
            if (ent instanceof LivingEntity lentity) {

                // Tameable сущности (кошки, волки и т. д.)
                if (lentity instanceof Tameable) {
                    Tameable tamable = (Tameable) ent;
                    types.add(tamable.isTamed() ? CAPIEntitySubType.TAMED : CAPIEntitySubType.UNTAMED);
                }

                // Возрастные сущности (взрослые/детёныши)
                if (lentity instanceof Ageable) {
                    Ageable ageable = (Ageable) ent;
                    types.add(ageable.isAdult() ? CAPIEntitySubType.ADULT : CAPIEntitySubType.BABY);
                }

                // Волки: агрессивные/пассивные
                if (ent instanceof Wolf wolf) {
                    types.add(wolf.isAngry() ? CAPIEntitySubType.ANGRY : CAPIEntitySubType.PASSIVE);
                }

                // Овцы: цвет + радужная овца
                if (ent instanceof Sheep sheep) {
                    if (sheep.getCustomName() != null && sheep.getCustomName().equalsIgnoreCase("jeb_")) {
                        types.add(CAPIEntitySubType.RAINBOW);
                    }
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(sheep.getColor().toString());
                    if (c != null) types.add(c);
                }

                // Лошади: цвет
                if (ent instanceof Horse horse) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(horse.getColor().toString());
                    if (c != null) types.add(c);
                }

                // Слизни: размер
                if (ent instanceof Slime slime) {
                    try {
                        CAPIEntitySubType c = CAPIEntitySubType.getByName("SIZE" + slime.getSize());
                        if (c != null) types.add(c);
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибки размера
                    }
                }

                // Криперы: заряженные/незаряженные
                if (ent instanceof Creeper creeper) {
                    types.add(creeper.isPowered() ? CAPIEntitySubType.POWERED : CAPIEntitySubType.UNPOWERED);
                }

                // Магмовые кубы: размер
                if (ent instanceof MagmaCube magmaCube) {
                    try {
                        CAPIEntitySubType c = CAPIEntitySubType.getByName("SIZE" + magmaCube.getSize());
                        if (c != null) types.add(c);
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибки размера
                    }
                }

                // Кролики: тип
                if (ent instanceof Rabbit rabbit) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(rabbit.getRabbitType().toString());
                    if (c != null) types.add(c);
                }

                // Жители: профессия и тип деревни
                if (ent instanceof Villager villager) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(villager.getProfession().toString());
                    if (c != null) types.add(c);

                    // Тип деревни (доступно с 1.14)
                    try {
                        c = CAPIEntitySubType.getByName(villager.getVillagerType().toString());
                        if (c != null) types.add(c);
                    } catch (NoSuchMethodError e) {
                        // Игнорируем, если метод недоступен
                    }
                }

                // Кошки: тип
                if (ent instanceof org.bukkit.entity.Cat cat) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(cat.getCatType().toString());
                    if (c != null) types.add(c);
                }

                // Лисы: тип
                if (ent instanceof org.bukkit.entity.Fox fox) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(fox.getFoxType().toString());
                    if (c != null) types.add(c);
                }
                // Панды: гены
                if (ent instanceof org.bukkit.entity.Panda panda) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(panda.getMainGene().toString());
                    if (c != null) types.add(c);

                    c = CAPIEntitySubType.getByName(panda.getHiddenGene().toString());
                    if (c != null) types.add(c);
                }


                // Пчёлы: агрессивность
                if (ent instanceof org.bukkit.entity.Bee bee) {
                    if (bee.getAnger() > 0) {
                        types.add(CAPIEntitySubType.ANGRY);
                    } else {
                        types.add(CAPIEntitySubType.PASSIVE);
                    }
                }

                // Ламы: цвет
                if (ent instanceof org.bukkit.entity.Llama llama) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(llama.getColor().toString());
                    if (c != null) types.add(c);
                }

                // Торговцы‑ламы: цвет
                if (ent instanceof org.bukkit.entity.TraderLlama traderLlama) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(traderLlama.getColor().toString());
                    if (c != null) types.add(c);
                }

                // Зомби‑жители: профессия
                if (ent instanceof org.bukkit.entity.ZombieVillager zombieVillager) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(zombieVillager.getVillagerProfession().toString());
                    if (c != null) types.add(c);
                }

                // Шулеры: цвет
                if (ent instanceof org.bukkit.entity.Shulker shulker) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(shulker.getColor().toString());
                    if (c != null) types.add(c);
                }

                // Попугаи: вариант
                if (ent instanceof org.bukkit.entity.Parrot parrot) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(parrot.getVariant().toString());
                    if (c != null) types.add(c);
                }

                // Грибные коровы: вариант
                if (ent instanceof org.bukkit.entity.MushroomCow mushroomCow) {
                    CAPIEntitySubType c = CAPIEntitySubType.getByName(mushroomCow.getVariant().toString());
                    if (c != null) types.add(c);
                }
            }
        } catch (Exception e) {
            // Игнорируем исключения (например, NumberFormatException)
        } catch (Error e) {
            // Игнорируем ошибки (например, NoSuchMethodError на старых версиях Bukkit)
        }

        return types;
    }
}

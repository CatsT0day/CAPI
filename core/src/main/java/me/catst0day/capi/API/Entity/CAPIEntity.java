package me.catst0day.capi.API.Entity;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CAPIEntity {
    private Entity ent;

    // Кэширование локализованных имён
    private static final HashMap<EntityType, String> translatedNames = new HashMap<>();

    static {
        initialize();
    }

    private static void initialize() {
        Arrays.stream(EntityType.values())
                .filter(type -> type != null)
                .map(EntityType::name)
                .sorted()
                .forEach(name -> {
                    EntityType ent = EntityType.valueOf(name);
                    // Простая локализация: заменяем _ на пробел и делаем первую букву заглавной
                    String localized = name.toLowerCase().replace("_", " ");
                    localized = localized.substring(0, 1).toUpperCase() + localized.substring(1);
                    translatedNames.put(ent, localized);
                });
    }

    public CAPIEntity(Entity ent) {
        this.ent = ent;
    }

    public Entity getEnt() {
        return ent;
    }

    public void setEnt(Entity ent) {
        this.ent = ent;
    }

    public String getName() {
        String name = translatedNames.get(this.getEnt().getType());
        if (name == null) name = this.getEnt().getCustomName();
        if (name == null) name = this.getEnt().getName();
        return name == null ? this.getEnt().getType().name() : name;
    }

    public String getCustomName() {
        String name;
        if (this.getEnt() instanceof Player) {
            Player player = (Player) this.getEnt();
            name = player.getDisplayName() != null ? player.getDisplayName() : player.getName();
        } else {
            name = translatedNames.get(this.getEnt().getType());
            if (this.getEnt().getCustomName() != null) name = this.getEnt().getCustomName();
            if (name == null) name = this.getEnt().getName();
        }
        return name == null ? this.getEnt().getType().name() : name;
    }

    public Inventory getInventory() {
        if (ent instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) ent;
            return holder.getInventory();
        }
        return null;
    }

    /**
     * Проверяет, является ли сущность рамкой предмета
     */
    public static boolean isItemFrame(Entity entity) {
        if (entity == null) return false;
        return entity.getType() == EntityType.ITEM_FRAME;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack setEntityType(ItemStack is, EntityType type) {
        try {
            BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
            CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();
            bs.setSpawnedType(type);
            bsm.setBlockState(bs);

            // Простая установка имени предмета
            String displayName = type.name().toLowerCase().replace("_", " ");
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1) + " Spawner";
            bsm.setDisplayName(displayName);
            is.setItemMeta(bsm);
        } catch (Throwable e) {
            // В случае ошибки просто возвращаем исходный предмет
        }
        return is;
    }

    /**
     * Сериализация сущности в строку
     */
    public static String serialize(Entity ent) {
        if (ent == null) return null;
        StringBuilder str = new StringBuilder();
        CAPIEntityType type = CAPIEntityType.getByType(ent.getType());
        str.append(type != null ? type.name() : ent.getType().name());

        if (ent.getCustomName() != null && !ent.getCustomName().isEmpty()) {
            str.append(";n{");
            str.append(ent.getCustomName().replace(" ", "_"));
            str.append("}");
        }

        return str.toString();
    }

    /**
     * Десериализация строки в сущность
     */
    public static Entity deserialize(String data) {
        if (data == null || data.isEmpty()) return null;

        String[] parts = data.split(";", 2);
        if (parts.length == 0) return null;

        CAPIEntityType type = CAPIEntityType.getByName(parts[0]);
        if (type == null) return null;

        // Здесь должна быть логика создания сущности по типу
        // В реальной реализации потребуется дополнительная логика для создания сущностей
        return null; // Заглушка — реализация зависит от контекста использования
    }

    /**
     * Получение максимального здоровья сущности
     */
    public static double getMaxHealth(Entity entity) {
        if (!(entity instanceof LivingEntity)) return 0d;

        LivingEntity lentity = (LivingEntity) entity;

        try {
            AttributeInstance attr = lentity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            return attr != null ? attr.getValue() : lentity.getMaxHealth();
        } catch (NoSuchMethodError | NoSuchFieldError e) {
            return lentity.getMaxHealth();
        }
    }

    /**
     * Проверка, является ли сущность живой
     */
    public boolean isLiving() {
        return ent instanceof LivingEntity;
    }

    /**
     * Проверка, является ли сущность игроком
     */
    public boolean isPlayer() {
        return ent instanceof Player;
    }

    /**
     * Получение типа сущности в виде CAPIEntityType
     */
    public CAPIEntityType getCAPIEntityType() {
        return CAPIEntityType.getByType(ent.getType());
    }

    /**
     * Получение подтипов сущности (использует CAPIEntitySubType)
     */
    public List<CAPIEntitySubType> getSubTypes() {
        return CAPIEntitySubType.getSubTypes(ent);
    }

    /**
     * Форматирование информации об сущности для отображения
     */
    public String formatInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Type: ").append(getName());

        if (getCustomName() != null && !getCustomName().equals(getName())) {
            info.append(" (Custom name: ").append(getCustomName()).append(")");
        }

        if (isLiving()) {
            info.append(", Health: ").append(String.format("%.1f", getMaxHealth(ent)));
        }

        List<CAPIEntitySubType> subTypes = getSubTypes();
        if (!subTypes.isEmpty()) {
            info.append(", Subtypes: ");
            for (int i = 0; i < subTypes.size(); i++) {
                if (i > 0) info.append(", ");
                info.append(subTypes.get(i).name());
            }
        }

        return info.toString();
    }
}
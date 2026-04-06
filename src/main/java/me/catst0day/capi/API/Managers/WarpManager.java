package me.catst0day.capi.Managers;

import me.catst0day.capi.CatAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {
    private final CatAPI plugin;
    private final File warpsFolder;

    public WarpManager(CatAPI plugin) {
        this.plugin = plugin;
        warpsFolder = new File(plugin.getDataFolder(), "warps");
        if (!warpsFolder.exists()) {
            warpsFolder.mkdirs();
        }
    }

    // Сохраняет варп в файл
    public boolean saveWarp(String name, Location location) {
        File warpFile = new File(warpsFolder, name + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("world", location.getWorld().getName());
        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());
        config.set("yaw", location.getYaw());
        config.set("pitch", location.getPitch());
        config.set("name", name);

        try {
            config.save(warpFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения варпа '" + name + "': " + e.getMessage());
            return false;
        }
    }

    // Загружает варп из файла
    public Location loadWarp(String name) {
        File warpFile = new File(warpsFolder, name + ".yml");
        if (!warpFile.exists()) return null;

        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);

        String worldName = config.getString("world");
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;

        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = (float) config.getDouble("yaw");
        float pitch = (float) config.getDouble("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    // Получает список всех варпов
    public List<String> getWarpList() {
        List<String> warps = new ArrayList<>();
        File[] files = warpsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    warps.add(file.getName().replace(".yml", ""));
                }
            }
        }
        return warps;
    }

    // Проверяет существование варпа
    public boolean warpExists(String name) {
        return new File(warpsFolder, name + ".yml").exists();
    }
}
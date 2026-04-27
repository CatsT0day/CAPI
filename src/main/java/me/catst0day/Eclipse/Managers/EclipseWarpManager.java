package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.catst0day.Eclipse.Utils.Util.log;

public class EclipseWarpManager {
    private final Eclipse plugin;
    private final File warpsFolder;
    private final Map<String, Location> warpCache = new ConcurrentHashMap<>();

    public EclipseWarpManager(Eclipse plugin) {
        this.plugin = plugin;
        this.warpsFolder = new File(plugin.getDataFolder(), "warps");
        if (!warpsFolder.exists()) {
            warpsFolder.mkdirs();
        }
        loadAllToCache();
    }

    private void loadAllToCache() {
        warpCache.clear();
        File[] files = warpsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            Location loc = loadWarpFromFile(name);
            if (loc != null) {
                warpCache.put(name.toLowerCase(), loc);
            }
        }
        log("Loaded &6" + warpCache.size() + " #3B1757warps into cache.");
    }

    public boolean saveWarp(String name, Location location) {
        File warpFile = new File(warpsFolder, name + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("world", location.getWorld().getName());
        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());
        config.set("yaw", (float) location.getYaw());
        config.set("pitch", (float) location.getPitch());
        config.set("name", name);

        try {
            config.save(warpFile);
            warpCache.put(name.toLowerCase(), location.clone());
            return true;
        } catch (IOException e) {
            log("&4Cant save warp &8(&5" + e.getMessage() + "&8)");
            return false;
        }
    }

    public boolean deleteWarp(String name) {
        File warpFile = new File(warpsFolder, name + ".yml");
        if (warpFile.exists() && warpFile.delete()) {
            warpCache.remove(name.toLowerCase());
            return true;
        }
        return false;
    }

    public Location getWarp(String name) {
        return warpCache.get(name.toLowerCase());
    }

    private Location loadWarpFromFile(String name) {
        File warpFile = new File(warpsFolder, name + ".yml");
        if (!warpFile.exists()) return null;

        FileConfiguration config = YamlConfiguration.loadConfiguration(warpFile);
        String worldName = config.getString("world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(
                world,
                config.getDouble("x"),
                config.getDouble("y"),
                config.getDouble("z"),
                (float) config.getDouble("yaw"),
                (float) config.getDouble("pitch")
        );
    }

    public List<String> getWarpList() {
        List<String> list = new ArrayList<>(warpCache.keySet());
        Collections.sort(list);
        return list;
    }

    public boolean warpExists(String name) {
        return warpCache.containsKey(name.toLowerCase());
    }

    public File getWarpsFolder() {
        return warpsFolder;
    }
}
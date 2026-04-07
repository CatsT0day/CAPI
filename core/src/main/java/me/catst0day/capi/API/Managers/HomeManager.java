package me.catst0day.capi.API.Managers;

import me.catst0day.capi.CAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

public class HomeManager {
    private final CAPI plugin;
    private final Map<UUID, Map<String, Location>> homes;

    public HomeManager(CAPI plugin) {
        this.plugin = plugin;
        this.homes = new HashMap<>();
        loadHomes(); // Загружаем дома при создании менеджера
    }

    public void loadHomes() {
        homes.clear();

        ConfigurationSection homesSection = plugin.getConfig().getConfigurationSection("homes");
        if (homesSection == null) {
            plugin.getLogger().info("Нет сохранённых домов для загрузки.");
            return;
        }

        for (String uuidString : homesSection.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuidString);
                Map<String, Location> playerHomes = new HashMap<>();

                ConfigurationSection playerHomesSection = homesSection.getConfigurationSection(uuidString);
                if (playerHomesSection != null) {
                    for (String homeName : playerHomesSection.getKeys(false)) {
                        Location location = loadLocation(playerHomesSection, homeName);
                        if (location != null) {
                            playerHomes.put(homeName, location);
                        }
                    }
                }

                if (!playerHomes.isEmpty()) {
                    homes.put(playerUUID, playerHomes);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Некорректный UUID в конфигурации: " + uuidString);
            }
        }
    }

    private Location loadLocation(ConfigurationSection section, String homeName) {
        try {
            String worldName = section.getString(homeName + ".world");
            double x = section.getDouble(homeName + ".x");
            double y = section.getDouble(homeName + ".y");
            double z = section.getDouble(homeName + ".z");
            float yaw = (float) section.getDouble(homeName + ".yaw", 0.0);
            float pitch = (float) section.getDouble(homeName + ".pitch", 0.0);

            if (worldName == null) return null;

            World world = plugin.getServer().getWorld(worldName);
            if (world == null) return null;

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка загрузки локации дома '" + homeName + "': " + e.getMessage());
            return null;
        }
    }

    public void saveHomes() {
        plugin.getConfig().set("homes", null); // Очищаем старую секцию

        for (Map.Entry<UUID, Map<String, Location>> playerEntry : homes.entrySet()) {
            UUID playerUUID = playerEntry.getKey();
            Map<String, Location> playerHomes = playerEntry.getValue();

            for (Map.Entry<String, Location> homeEntry : playerHomes.entrySet()) {
                String homeName = homeEntry.getKey();
                Location location = homeEntry.getValue();

                String path = "homes." + playerUUID.toString() + "." + homeName;
                plugin.getConfig().set(path + ".world", location.getWorld().getName());
                plugin.getConfig().set(path + ".x", location.getX());
                plugin.getConfig().set(path + ".y", location.getY());
                plugin.getConfig().set(path + ".z", location.getZ());
                plugin.getConfig().set(path + ".yaw", location.getYaw());
                plugin.getConfig().set(path + ".pitch", location.getPitch());
            }
        }

        plugin.saveConfig();
    }

    public boolean setHome(UUID playerUUID, String homeName, Location location) {
        if (!homes.containsKey(playerUUID)) {
            homes.put(playerUUID, new HashMap<>());
        }
        homes.get(playerUUID).put(homeName, location);
        saveHomes();
        return true;
    }

    public Location getHome(UUID playerUUID, String homeName) {
        Map<String, Location> playerHomes = homes.get(playerUUID);
        if (playerHomes == null) return null;
        return playerHomes.get(homeName);
    }

    public boolean deleteHome(UUID playerUUID, String homeName) {
        Map<String, Location> playerHomes = homes.get(playerUUID);
        if (playerHomes == null) return false;

        boolean removed = playerHomes.remove(homeName) != null;
        if (removed) saveHomes();
        return removed;
    }

    public List<String> getPlayerHomes(UUID playerUUID) {
        Map<String, Location> playerHomes = homes.get(playerUUID);
        if (playerHomes == null) return new ArrayList<>();
        return new ArrayList<>(playerHomes.keySet());
    }
}
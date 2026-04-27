package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.util.*;

public class EclipseAliasManager {
    private final Eclipse plugin;
    private final Map<String, List<String>> aliases;
    private File aliasesFile;
    private FileConfiguration aliasesConfig;

    public EclipseAliasManager(Eclipse plugin) {
        this.plugin = plugin;
        this.aliases = new HashMap<>();
        this.aliasesFile = new File(plugin.getDataFolder(), "aliases.yml");
        this.aliasesConfig = plugin.getConfig();
        loadAliases();
    }

    private void loadAliases() {
        if (aliasesConfig.contains("aliases")) {
            for (String aliasName : aliasesConfig.getConfigurationSection("aliases").getKeys(false)) {
                List<String> commands = aliasesConfig.getStringList("aliases." + aliasName);
                aliases.put(aliasName, new ArrayList<>(commands));
            }
        } else {
            aliasesConfig.createSection("aliases");
        }
    }

    private void saveAliases() {
        aliasesConfig.set("aliases", null);
        for (Map.Entry<String, List<String>> entry : aliases.entrySet()) {
            aliasesConfig.set("aliases." + entry.getKey(), entry.getValue());
        }
        try {
            aliasesConfig.save(aliasesFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Не удалось сохранить алиасы: " + e.getMessage());
        }
    }

    public void createAlias(String aliasName, List<String> commands) {
        aliases.put(aliasName, new ArrayList<>(commands));
        saveAliases();
    }

    public boolean removeAlias(String aliasName) {
        if (aliases.remove(aliasName) != null) {
            saveAliases();
            return true;
        }
        return false;
    }

    public List<String> getCommandsForAlias(String aliasName) {
        return aliases.get(aliasName);
    }

    public Map<String, List<String>> getAllAliases() {
        return new HashMap<>(aliases);
    }

    public List<String> getAllAliasNames() {
        return new ArrayList<>(aliases.keySet());
    }

    public void addCommandToAlias(String aliasName, String command) {
        List<String> commands = aliases.get(aliasName);
        if (commands != null) {
            commands.add(command);
            saveAliases();
        }
    }

    public void removeCommandFromAlias(String aliasName, int index) {
        List<String> commands = aliases.get(aliasName);
        if (commands != null && index >= 0 && index < commands.size()) {
            commands.remove(index);
            saveAliases();
        }
    }
}
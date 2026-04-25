package me.catst0day.capi;

import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Entity.Listeners.CAPIOnEntityHitEventListener;
import me.catst0day.capi.EventListeners.*;
import me.catst0day.capi.GUI.CAPIGuiListener;
import me.catst0day.capi.Managers.CAPIHomeManager;
import me.catst0day.capi.Managers.CAPIWarpManager;
import me.catst0day.capi.Utils.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static me.catst0day.capi.Utils.Util.log;
import static org.bukkit.Bukkit.getCommandMap;

public class CAPIOnEnableInitter {
    private static CAPI instance;
    private final Map<UUID, Boolean> godMode = new HashMap<>();
    private final Map<UUID, Boolean> flyMode = new HashMap<>();
    private final Map<UUID, Boolean> inArena = new HashMap<>();
    private final Map<UUID, UUID> tpaRequests = new HashMap<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    private VersionChecker versionChecker;
    public static YamlConfiguration langConfig;
    public static String currentLang;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private static ResourceDownloader fileDownloader;

    public void OnEnable(CAPI plugin) {
        instance = plugin;
        if (versionChecker == null) {
            versionChecker = new VersionChecker(plugin, "CatsT0day", "CAPI");
        }
        versionChecker.checkForUpdates();
        Util.printStartupBanner(plugin);

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        fileDownloader = new ResourceDownloader(plugin);
        loadTranslations();

        registerAllCommandsFromPackage(plugin, "me.catst0day.capi.Commands.list");
        setupMain(plugin);

        this.homeManager = new CAPIHomeManager(plugin);
        this.warpManager = new CAPIWarpManager(plugin);

        PluginManager pm = plugin.getServer().getPluginManager();
        if (plugin.getConfig().getBoolean("DisableAchievements")) {
            pm.registerEvents((Listener) new CAPIHideAchievements(), plugin);
        }

        pm.registerEvents(new CAPIOnEntityHitEventListener(plugin), plugin);
        pm.registerEvents(new CAPIGuiListener(), plugin);
        pm.registerEvents(new CAPIOnEntityDamageEvent(plugin), plugin);
        pm.registerEvents(new CAPIOnItemPickupEvent(plugin), plugin);

        plugin.fullyLoaded = true;
    }

    public static void registerAllCommandsFromPackage(CAPI plugin, String packageName) {
        String path = packageName.replace('.', '/');
        URL packageURL = plugin.getClass().getClassLoader().getResource(path);

        if (packageURL == null) {
            log("&cPackage " + packageName + " not found!");
            return;
        }

        try {
            if ("jar".equals(packageURL.getProtocol())) {
                scanJar(plugin, packageURL, path);
            } else {
                scanDirectory(plugin, new File(packageURL.toURI()), packageName);
            }
        } catch (Exception e) {
            log("&cCritical error scanning package: " + e.getMessage());
        }
    }

    private static void scanJar(CAPI plugin, URL packageURL, String path) throws IOException {
        JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
        try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    attemptRegister(plugin, className);
                }
            }
        }
    }

    private static void scanDirectory(CAPI plugin, File dir, String packageName) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".class") && !file.getName().contains("$")) {
                attemptRegister(plugin, packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private static void attemptRegister(CAPI plugin, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (CAPICommandTemplate.class.isAssignableFrom(clazz) &&
                    !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {

                CAPICommandTemplate cmd = (CAPICommandTemplate) clazz.getConstructor(CAPI.class).newInstance(plugin);
                registerCommand(plugin, cmd);
            }
        } catch (Exception e) {
            log("&cError registering " + className + ": " + e.getMessage());
        }
    }

    private static void registerCommand(CAPI plugin, CAPICommandTemplate command) {
        SimpleCommandMap commandMap = (SimpleCommandMap) getCommandMap();
        if (commandMap == null) return;

        Command bukkitCommand = createBukkitCommand(command.getName(), command);
        commandMap.register(plugin.getName(), bukkitCommand);

        registerInternal(command.getName(), command);

        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                commandMap.register(plugin.getName(), createBukkitCommand(alias, command));
                registerInternal(alias, command);
            }
        }
    }

    private static Command createBukkitCommand(String name, CAPICommandTemplate template) {
        return new Command(name) {
            @Override
            public boolean execute(CommandSender s, String l, String[] a) {
                return template.onCommand(s, this, l, a);
            }
            @Override
            public List<String> tabComplete(CommandSender s, String al, String[] a) {
                return template.onTabComplete(s, this, al, a);
            }
        };
    }

    private static void registerInternal(String name, CommandExecutor executor) {
        CAPICommandTemplate.getRegisteredCommands().put(name, executor);
    }

    public static void loadTranslations() {
        currentLang = instance.getConfig().getString("lang", "EN").toUpperCase();
        File langFile = new File(instance.getDataFolder(), "Translations/" + currentLang + ".yml");

        if (!langFile.exists()) {
            instance.saveResource("Translations/" + currentLang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        log("Loaded translation: " + langFile.getName());
    }

    private void setupMain(CAPI plugin) {
        PluginCommand main = plugin.getCommand("capi");
        if (main == null) return;

        main.setExecutor((sender, command, label, args) -> {
            if (args.length == 0) {
                sender.sendMessage("=== CAPI Commands ===");
                CAPICommandTemplate.getRegisteredCommands().forEach((name, exec) -> {
                    if (exec instanceof CAPICommandTemplate cmd)
                        sender.sendMessage("/capi " + name + " - " + cmd.getDescription());
                });
                return true;
            }

            String sub = args[0].toLowerCase();
            CommandExecutor exec = CAPICommandTemplate.getRegisteredCommands().get(sub);
            if (exec == null) {
                sender.sendMessage(plugin.getMessage("unknownCommand").replace("%command%", sub));
                return true;
            }
            return exec.onCommand(sender, command, sub, Arrays.copyOfRange(args, 1, args.length));
        });

        main.setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return CAPICommandTemplate.getRegisteredCommands().keySet().stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .toList();
            }
            CommandExecutor exec = CAPICommandTemplate.getRegisteredCommands().get(args[0].toLowerCase());
            if (exec instanceof CAPICommandTemplate cmd) {
                return cmd.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
            return Collections.emptyList();
        });
    }
}
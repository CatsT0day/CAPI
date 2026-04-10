
package me.catst0day.capi;

import me.catst0day.capi.Commands.*;
import me.catst0day.capi.Entity.Listeners.CAPIOnEntityHitEventListener;
import me.catst0day.capi.EventListeners.CAPIHideAchievements;
import me.catst0day.capi.EventListeners.CAPIOnEntityDamageEvent;
import me.catst0day.capi.EventListeners.CAPIOnItemPickupEvent;
import me.catst0day.capi.GUI.CAPIGuiListener;
import me.catst0day.capi.Managers.CAPIHomeManager;
import me.catst0day.capi.Managers.CAPIWarpManager;
import me.catst0day.capi.Utils.Util;
import me.catst0day.capi.Utils.VersionChecker;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static me.catst0day.capi.Utils.Util.log;

public class CAPIOnEnableInitter {
    private static me.catst0day.capi.CAPI instance;
    private final HashMap<UUID, Boolean> godMode = new HashMap<>();
    private final HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private final HashMap<UUID, Boolean> inArena = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    public static YamlConfiguration langConfig;
    public static String currentLang;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private static FileDownloader fileDownloader;

    public void OnEnable(CAPI plugin) {
        instance = plugin;
        VersionChecker versionChecker = new VersionChecker(plugin, "CatsT0day", "CAPI");
        versionChecker.checkForUpdates();
        Util.printStartupBanner(plugin);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        fileDownloader = new FileDownloader(plugin);
        loadTranslations();

        registerAllCommandsFromPackage(plugin, "me.catst0day.capi.Commands");
        setupMain(plugin);
        this.homeManager = new CAPIHomeManager(plugin);
        this.warpManager = new CAPIWarpManager(plugin);


        if (plugin.getConfig().getBoolean("DisableAchievements")) {
            plugin.getServer().getPluginManager().registerEvents((Listener) new CAPIHideAchievements(), plugin);
        }

        plugin.getServer().getPluginManager().registerEvents(new CAPIOnEntityHitEventListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CAPIGuiListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CAPIOnEntityDamageEvent(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CAPIOnItemPickupEvent(plugin), plugin);
        plugin.fullyLoaded = true;
    }


    public static void registerAllCommandsFromPackage(CAPI plugin, String packageName) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        String path = packageName.replace('.', '/');
        URL packageURL;

        try {
            packageURL = classLoader.getResource(path);
        } catch (Exception e) {
            log("Error getting package resource: " + e.getMessage());
            return;
        }

        if (packageURL == null) {
            log("Package " + packageName + " not found!");
            return;
        }

        try {
            if ("jar".equals(packageURL.getProtocol())) {
                // Processing JAR archive
                log("Scanning JAR archive for package: " + packageName);

                JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
                JarFile jarFile = connection.getJarFile();

                try {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();

                        // Check if file is in the correct package and is a .class file
                        if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                            String className = name.substring(0, name.length() - 6).replace('/', '.');

                            try {
                                Class<?> clazz = Class.forName(className);

                                // Check conditions: inherits CommandTemplate, not an interface, not abstract
                                if (CommandTemplate.class.isAssignableFrom(clazz) &&
                                        !clazz.isInterface() &&
                                        !Modifier.isAbstract(clazz.getModifiers())) {

                                    Constructor<?> constructor = clazz.getConstructor(CAPI.class);
                                    CommandTemplate commandInstance = (CommandTemplate) constructor.newInstance(plugin);
                                    registerCommand(commandInstance.getName(), commandInstance);

                                    // Register aliases
                                    if (commandInstance.getAliases() != null) {
                                        for (String alias : commandInstance.getAliases()) {
                                            registerCommand(alias, commandInstance);
                                        }
                                    }
                                }
                            } catch (ClassNotFoundException | NoSuchMethodException |
                                     IllegalAccessException | InstantiationException e) {
                                log("Error registering command " + className + ": " + e.getMessage());
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } finally {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        log("Failed to close JAR file: " + e.getMessage());
                    }
                }
            } else {
                log("Scanning directory (" + packageName + ")");
                File packageDir = new File(packageURL.toURI());

                if (!packageDir.exists() || !packageDir.isDirectory()) {
                    log("Directory (" + packageName + ") does not exist");
                    return;
                }

                for (File file : packageDir.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".class") && !file.getName().contains("$")) {
                        String className = packageName + "." + file.getName().replace(".class", "");

                        try {
                            Class<?> clazz = Class.forName(className);

                            if (CommandTemplate.class.isAssignableFrom(clazz) &&
                                    !clazz.isInterface() &&
                                    !Modifier.isAbstract(clazz.getModifiers())) {

                                Constructor<?> constructor = clazz.getConstructor(CAPI.class);
                                CommandTemplate commandInstance = (CommandTemplate) constructor.newInstance(plugin);
                                registerCommand(commandInstance.getName(), commandInstance);

                                // Register aliases
                                if (commandInstance.getAliases() != null) {
                                    for (String alias : commandInstance.getAliases()) {
                                        registerCommand(alias, commandInstance);
                                    }
                                }
                            }
                        } catch (ClassNotFoundException | NoSuchMethodException |
                                 IllegalAccessException | InstantiationException e) {
                            log("Error registering command " + className + ": " + e.getMessage());
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            log("Error working with package resources: " + e.getMessage());
        }
    }


    public static void loadTranslations() {
        currentLang = instance.getConfig().getString("lang", "EN").toUpperCase();
        File translationsFolder = new File(instance.getDataFolder(), "Translations");
        File langFile = new File(translationsFolder, currentLang + ".yml");

        if (!translationsFolder.exists()) {
            if (!translationsFolder.mkdirs()) {
                log("Failed to create Translations folder!");
                langConfig = new YamlConfiguration();
                return;
            }
        }

        if (!langFile.exists()) {
            instance.saveResource("Translations/" + currentLang + ".yml", false);

            if (!langFile.exists()) {
                currentLang = "EN";
                langFile = new File(translationsFolder, "EN.yml");
                instance.saveResource("Translations/EN.yml", false);
            }
        }

        try {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            log("Successfully loaded translation: " + langFile.getName());
        } catch (Exception e) {
            log("Error while loading translation: " + e.getMessage());
            langConfig = new YamlConfiguration();
        }
    }


    public static void registerCommand(String commandName, CommandExecutor executor) {
        if (executor == null) {
            log("Executor for command '%s' is null!".formatted(commandName));
            return;
        }
        CommandTemplate.getRegisteredCommands().put(commandName, executor);
        log("Subcommand '%s' loaded and available via /capi.".formatted(commandName));
    }

    private void setupMain(CAPI plugin) {
        PluginCommand mainCommand = plugin.getCommand("capi");
        if (mainCommand == null) {
            log("Main command 'capi' not found in plugin.yml!");
            return;
        }

        mainCommand.setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length == 0) {
                    // Show help for available commands
                    sender.sendMessage("=== CAPI Commands ===");
                    List<String> subCommands = new ArrayList<>(CommandTemplate.getRegisteredCommands().keySet());
                    for (String subCommand : subCommands) {
                        CommandTemplate cmd = (CommandTemplate) CommandTemplate.getRegisteredCommands().get(subCommand);
                        if (cmd != null) {
                            sender.sendMessage("/capi " + subCommand + " - " + cmd.getDescription());
                        }
                    }
                    return true;
                }

                String subCommandName = args[0].toLowerCase();
                CommandExecutor subCommandExecutor = CommandTemplate.getRegisteredCommands().get(subCommandName);

                if (subCommandExecutor == null) {
                    sender.sendMessage(plugin.getMessage("unknownCommand")
                            .replace("%command%", subCommandName));
                    return true;
                }

                // Prepare arguments for subcommand (remove first part)
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);

                return subCommandExecutor.onCommand(sender, command, subCommandName, subArgs);
            }
        });

        mainCommand.setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                List<String> completions = new ArrayList<>();

                if (args.length == 1) {
                    // Complete with available subcommands
                    completions.addAll(CommandTemplate.getRegisteredCommands().keySet().stream()
                            .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList()));
                    return completions;
                } else if (args.length > 1) {
                    // Try to get tab completion from the subcommand
                    String subCommandName = args[0].toLowerCase();
                    CommandExecutor subCommand = CommandTemplate.getRegisteredCommands().get(subCommandName);

                    if (subCommand instanceof CommandTemplate cmdTemplate) {
                        String[] subArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                        List<String> subCompletions = cmdTemplate.onTabComplete(sender, command, alias, subArgs);
                        if (subCompletions != null) {
                            return subCompletions;
                        }
                    }
                }

                // Return empty list if no completions found
                return completions;
            }
        });
    }
}
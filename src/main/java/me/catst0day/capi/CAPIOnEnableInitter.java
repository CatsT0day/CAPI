
package me.catst0day.capi;

import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Entity.Listeners.CAPIOnEntityHitEventListener;
import me.catst0day.capi.EventListeners.CAPIHideAchievements;
import me.catst0day.capi.EventListeners.CAPIOnEntityDamageEvent;
import me.catst0day.capi.EventListeners.CAPIOnItemPickupEvent;
import me.catst0day.capi.GUI.CAPIGuiListener;
import me.catst0day.capi.Managers.CAPIHomeManager;
import me.catst0day.capi.Managers.CAPIWarpManager;
import me.catst0day.capi.Utils.ResourceDownloader;
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

import static me.catst0day.capi.Utils.Util.loadWithMessage;
import static me.catst0day.capi.Utils.Util.log;
import static org.bukkit.Bukkit.getCommandMap;

public class CAPIOnEnableInitter {
    private static me.catst0day.capi.CAPI instance;
    private final HashMap<UUID, Boolean> godMode = new HashMap<>();
    private final HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private final HashMap<UUID, Boolean> inArena = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private VersionChecker versionChecker;
    public static YamlConfiguration langConfig;
    public static String currentLang;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private static ResourceDownloader fileDownloader;

    public void OnEnable(CAPI plugin) {
        instance = plugin;
        if (this.versionChecker == null) {
            this.versionChecker = new VersionChecker(plugin, "CatsT0day", "CAPI");
        }
        this.versionChecker.checkForUpdates();
        Util.printStartupBanner(plugin);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        fileDownloader = new ResourceDownloader(plugin);
        loadTranslations();

        registerAllCommandsFromPackage(plugin, "me.catst0day.capi.Commands.list");
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
            log("&cError getting package resource: " + e.getMessage());
            return;
        }

        if (packageURL == null) {
            log("&cPackage " + packageName + " not found!");
            return;
        }

        try {
            if ("jar".equals(packageURL.getProtocol())) {
                log("&bScanning JAR archive for package: " + packageName);

                JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
                JarFile jarFile = connection.getJarFile();

                try {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                            String className = name.substring(0, name.length() - 6).replace('/', '.');

                            try {
                                Class<?> clazz = Class.forName(className);
                                if (CAPICommandTemplate.class.isAssignableFrom(clazz) &&
                                        !clazz.isInterface() &&
                                        !Modifier.isAbstract(clazz.getModifiers())) {

                                    Constructor<?> constructor = clazz.getConstructor(CAPI.class);
                                    CAPICommandTemplate commandInstance = (CAPICommandTemplate) constructor.newInstance(plugin);
                                    registerCommand(plugin, commandInstance);
                                }
                            } catch (ClassNotFoundException | NoSuchMethodException |
                                     IllegalAccessException | InstantiationException e) {
                                log("&cError registering command " + className + ": " + e.getMessage());
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } finally {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        log("&cFailed to close JAR file: " + e.getMessage());
                    }
                }
            } else {
                log("&eScanning directory (" + packageName + ")");
                File packageDir = new File(packageURL.toURI());

                if (!packageDir.exists() || !packageDir.isDirectory()) {
                    log("&4Directory &8(&e" + packageName + "&8)&4 does not exist");
                    return;
                }

                for (File file : packageDir.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".class") && !file.getName().contains("$")) {
                        String className = packageName + "." + file.getName().replace(".class", "");

                        try {
                            Class<?> clazz = Class.forName(className);

                            if (CAPICommandTemplate.class.isAssignableFrom(clazz) &&
                                    !clazz.isInterface() &&
                                    !Modifier.isAbstract(clazz.getModifiers())) {

                                Constructor<?> constructor = clazz.getConstructor(CAPI.class);
                                CAPICommandTemplate commandInstance = (CAPICommandTemplate) constructor.newInstance(plugin);

                                registerCommand(plugin, commandInstance);
                            }
                        } catch (ClassNotFoundException | NoSuchMethodException |
                                 IllegalAccessException | InstantiationException e) {
                            log("&cError registering command " + className + ": " + e.getMessage());
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    private static void registerCommand(CAPI plugin, CAPICommandTemplate command) {
        String commandName = command.getName();

        SimpleCommandMap commandMap = (SimpleCommandMap) getCommandMap();
        if (commandMap == null) {
            log("&cFailed to get command map for command: " + commandName);
            return;
        }

        Command bukkitCommand = new Command(commandName) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                return command.onCommand(sender, this, label, args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                return Objects.requireNonNull(((TabCompleter) command).onTabComplete(sender, this, alias, args));
            }
        };

        commandMap.register(plugin.getName(), bukkitCommand);
        long var1 = System.currentTimeMillis();
        loadWithMessage("1", "command", System.currentTimeMillis() - var1);
        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                Command aliasCommand = new Command(alias) {
                    @Override
                    public boolean execute(CommandSender sender, String label, String[] args) {
                        return command.onCommand(sender, this, label, args);
                    }

                    @Override
                    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                        if (command instanceof TabCompleter) {
                            return ((TabCompleter) command).onTabComplete(sender, this, alias, args);
                        }
                        return super.tabComplete(sender, alias, args);
                    }
                };
                commandMap.register(plugin.getName(), aliasCommand);
                loadWithMessage("1", "basic plugin alias", System.currentTimeMillis() - var1);
            }
        }

        registerCommand(commandName, command);
        if (command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                registerCommand(alias, command);
            }
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
            String msg = "Successfully loaded translation (&7" + langFile.getName() + ")";
            log(msg);
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
        CAPICommandTemplate.getRegisteredCommands().put(commandName, executor);
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
                    sender.sendMessage("=== CAPI Commands ===");
                    List<String> subCommands = new ArrayList<>(CAPICommandTemplate.getRegisteredCommands().keySet());
                    for (String subCommand : subCommands) {
                        CAPICommandTemplate cmd = (CAPICommandTemplate) CAPICommandTemplate.getRegisteredCommands().get(subCommand);
                        if (cmd != null) {
                            sender.sendMessage("/capi " + subCommand + " - " + cmd.getDescription());
                        }
                    }
                    return true;
                }

                String subCommandName = args[0].toLowerCase();
                CommandExecutor subCommandExecutor = CAPICommandTemplate.getRegisteredCommands().get(subCommandName);

                if (subCommandExecutor == null) {
                    sender.sendMessage(plugin.getMessage("unknownCommand")
                            .replace("%command%", subCommandName));
                    return true;
                }
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
                    completions.addAll(CAPICommandTemplate.getRegisteredCommands().keySet().stream()
                            .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                            .toList());
                    return completions;
                } else if (args.length > 1) {
                    String subCommandName = args[0].toLowerCase();
                    CommandExecutor subCommand = CAPICommandTemplate.getRegisteredCommands().get(subCommandName);

                    if (subCommand instanceof CAPICommandTemplate cmdTemplate) {
                        String[] subArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                        List<String> subCompletions = cmdTemplate.onTabComplete(sender, command, alias, subArgs);
                        if (subCompletions != null) {
                            return subCompletions;
                        }
                    }
                }

                return completions;
            }
        });
    }
}
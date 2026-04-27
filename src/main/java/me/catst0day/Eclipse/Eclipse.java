package me.catst0day.Eclipse;

import me.catst0day.Eclipse.Bossbar.CAPIBarColor;
import me.catst0day.Eclipse.Bossbar.CAPIBarStyle;
import me.catst0day.Eclipse.Bossbar.CAPIBossBar;
import me.catst0day.Eclipse.EventListeners.CAPIHideAchievements;
import me.catst0day.Eclipse.EventListeners.CAPIOnEntityDamageEvent;
import me.catst0day.Eclipse.EventListeners.CAPIOnItemPickupEvent;
import me.catst0day.Eclipse.Managers.EclipseAliasManager;
import me.catst0day.Eclipse.Managers.EclipseHomeManager;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import me.catst0day.Eclipse.Managers.EclipseWarpManager;
import me.catst0day.Eclipse.Utils.HexUtil;
import me.catst0day.Eclipse.Utils.ResourceDownloader;
import me.catst0day.Eclipse.Utils.Util;
import me.catst0day.Eclipse.Utils.VersionChecker;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Listeners.OnEntityHitEventListener;
import me.catst0day.Eclipse.Entity.Player.GuiListener;
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Schedulers.EclipseScheduler;
import org.bukkit.Location;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static me.catst0day.Eclipse.Utils.Util.log;
import static org.bukkit.Bukkit.getCommandMap;

public class Eclipse extends JavaPlugin {
    private static Eclipse instance;
    private final Map<UUID, Boolean> godMode = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> flyMode = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();
    public static boolean fullyLoaded = false;
    public static YamlConfiguration langConfig;
    public static String currentLang;

    private EclipseHomeManager homeManager;
    private EclipseWarpManager warpManager;
    private EclipsePermissionManager permManager;
    private EclipseAliasManager aliasManager;
    private VersionChecker versionCheckManager;
    private ResourceDownloader fileDownloader;

    @Override
    public void onEnable() {
        instance = this;
        Util.printStartupBanner(this);
        saveDefaultConfig();
        reloadConfig();
        this.fileDownloader = new ResourceDownloader(this);
        loadTranslations();
        this.homeManager = new EclipseHomeManager(this);
        this.warpManager = new EclipseWarpManager(this);

        registerAllCommandsFromPackage("me.catst0day.capi.Commands.list");
        setupMainCommand();
        registerEvents();
        getVersionCheckManager().checkForUpdates();

        fullyLoaded = true;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
    }

    public static Eclipse getI() {
        return instance == null ? JavaPlugin.getPlugin(Eclipse.class) : instance;
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        if (getConfig().getBoolean("DisableAchievements")) {
            pm.registerEvents((Listener) new CAPIHideAchievements(), this);
        }
        pm.registerEvents(new OnEntityHitEventListener(this), this);
        pm.registerEvents(new GuiListener(), this);
        pm.registerEvents(new CAPIOnEntityDamageEvent(this), this);
        pm.registerEvents(new CAPIOnItemPickupEvent(this), this);
    }

    // --- Msg System ---

    public void loadTranslations() {
        currentLang = getConfig().getString("lang", "EN").toUpperCase();
        File langFile = new File(getDataFolder(), "Translations/" + currentLang + ".yml");

        if (!langFile.exists()) {
            saveResource("Translations/" + currentLang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        log("Loaded translation: " + langFile.getName());
    }

    public String getMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";
        String raw = langConfig.getString("messages." + key);
        if (raw == null) {
            getLogger().warning("Missing translation key: " + key);
            return "Msg '" + key + "' missing";
        }
        return HexUtil.translateHexAndAlternateColorCodes(raw);
    }

    public String getGameModeMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";
        String raw = langConfig.getString("messages.gamemodes." + key);
        if (raw == null) return getMessage(key);
        return HexUtil.translateHexAndAlternateColorCodes(raw);
    }

    public String sendCFGmessage(CommandSender sender, String key) {
        String msg = getMessage(key);
        if (sender instanceof Player player) {
            return getPlayer(player).sendMsg(msg);
        }
        sender.sendMessage(msg);
        return msg;
    }

    // --- Cmd reg ---

    private void registerAllCommandsFromPackage(String packageName) {
        long startTime = System.currentTimeMillis();
        String path = packageName.replace('.', '/');
        URL packageURL = getClassLoader().getResource(path);

        if (packageURL == null) {
            log("&cPackage " + packageName + " not found!");
            return;
        }

        int count = 0;
        try {
            if ("jar".equals(packageURL.getProtocol())) {
                JarURLConnection connection = (JarURLConnection) packageURL.openConnection();
                try (JarFile jarFile = connection.getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(path) && name.endsWith(".class") && !name.contains("$")) {
                            String className = name.substring(0, name.length() - 6).replace('/', '.');
                            if (attemptRegister(className)) {
                                count++;
                            }
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
            Util.loadWithMessage(count, "commands", endTime);

        } catch (Exception e) {
            log("&cCritical error scanning package: " + e.getMessage());
        }
    }

    private boolean attemptRegister(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (CommandTemplate.class.isAssignableFrom(clazz) &&
                    !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {

                CommandTemplate cmd = (CommandTemplate) clazz.getConstructor(Eclipse.class).newInstance(this);

                SimpleCommandMap commandMap = (SimpleCommandMap) getCommandMap();
                if (commandMap == null) return false;
                Command bukkitCmd = createBukkitCommand(cmd.getName(), cmd);
                commandMap.register(getName(), bukkitCmd);
                CommandTemplate.getRegisteredCommands().put(cmd.getName(), cmd);
                if (cmd.getAliases() != null) {
                    for (String alias : cmd.getAliases()) {
                        commandMap.register(getName(), createBukkitCommand(alias, cmd));
                        CommandTemplate.getRegisteredCommands().put(alias, cmd);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log("&cError registering " + className + ": " + e.getMessage());
        }
        return false;
    }

    private Command createBukkitCommand(String name, CommandTemplate template) {
        return new Command(name) {
            @Override public boolean execute(CommandSender s, String l, String[] a) { return template.onCommand(s, this, l, a); }
            @Override public List<String> tabComplete(CommandSender s, String al, String[] a) { return template.onTabComplete(s, this, al, a); }
        };
    }

    private void setupMainCommand() {
        PluginCommand main = getCommand("capi");
        if (main == null) return;

        main.setExecutor((sender, command, label, args) -> {
            if (args.length == 0) {
                sender.sendMessage("=== CAPI Commands ===");
                CommandTemplate.getRegisteredCommands().forEach((name, exec) -> {
                    if (exec instanceof CommandTemplate cmd)
                        sender.sendMessage("/capi " + name + " - " + cmd.getDescription());
                });
                return true;
            }

            String sub = args[0].toLowerCase();
            CommandExecutor exec = CommandTemplate.getRegisteredCommands().get(sub);
            if (exec == null) {
                sender.sendMessage(getMessage("unknownCommand").replace("%command%", sub));
                return true;
            }
            return exec.onCommand(sender, command, sub, Arrays.copyOfRange(args, 1, args.length));
        });

        main.setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return CommandTemplate.getRegisteredCommands().keySet().stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .toList();
            }
            CommandExecutor exec = CommandTemplate.getRegisteredCommands().get(args[0].toLowerCase());
            if (exec instanceof CommandTemplate cmd) {
                return cmd.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
            return Collections.emptyList();
        });
    }

    // --- Teleport System ---

    public void teleport(Player player, Location target) {
        if (target == null || target.getWorld() == null) {
            player.sendMessage(getMessage("invalidLocation"));
            return;
        }

        int delaySeconds = player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(p -> p.startsWith("catapi.teleport.delay."))
                .map(p -> p.substring(22))
                .filter(s -> s.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(7);

        if (delaySeconds <= 0) {
            player.teleport(target);
            player.sendMessage(getMessage("teleportSuccess"));
            return;
        }

        CAPIBossBar barInfo = new CAPIBossBar(this, player, "teleport_delay");
        barInfo.setColor(CAPIBarColor.BLUE);
        barInfo.setStyle(CAPIBarStyle.SOLID);
        barInfo.setSeconds(delaySeconds);
        bossBars.put(player.getUniqueId(), barInfo.getBar());

        EclipseScheduler.runTaskTimer(this, task -> {
            if (!player.isOnline() || !barInfo.stillRunning()) {
                barInfo.remove();
                bossBars.remove(player.getUniqueId());
                task.cancel();
                return;
            }

            int remaining = (int) (barInfo.getLeftDuration() / 1000);
            if (remaining <= 0) {
                player.teleport(target);
                player.sendMessage(getMessage("teleportSuccess"));
                barInfo.remove();
                bossBars.remove(player.getUniqueId());
                task.cancel();
            } else {
                barInfo.setTitleOfBar(getMessage("teleportWithDelay").replace("%seconds%", String.valueOf(remaining)));
            }
        }, 0L, 20L);
    }

    // --- Getters & API ---

    public EclipseHomeManager getHomeManager() { return homeManager == null ? (homeManager = new EclipseHomeManager(this)) : homeManager; }
    public EclipsePermissionManager getPermissionManager() { return permManager == null ? (permManager = new EclipsePermissionManager(this)) : permManager; }
    public EclipseWarpManager getWarpManager() { return warpManager == null ? (warpManager = new EclipseWarpManager(this)) : warpManager; }
    public EclipseAliasManager getAliasManager() { return aliasManager == null ? (aliasManager = new EclipseAliasManager(this)) : aliasManager; }
    public VersionChecker getVersionCheckManager() { return versionCheckManager == null ? (versionCheckManager = new VersionChecker(this, "CatsT0day", "CAPI")) : versionCheckManager; }

    public boolean isGodMode(UUID uuid) { return godMode.getOrDefault(uuid, false); }
    public boolean isFlyMode(UUID uuid) { return flyMode.getOrDefault(uuid, false); }
    public void setFlyMode(UUID uuid, boolean enabled) { flyMode.put(uuid, enabled); }
    public Map<UUID, UUID> getTpaRequests() { return tpaRequests; }

    public void toggleGodMode(Player sender, String[] args) {
        Player target = (args.length == 1) ? Bukkit.getPlayer(args[0]) : sender;
        if (target == null) {
            if (sender != null) sender.sendMessage(getMessage("playerNotFound"));
            return;
        }

        UUID uuid = target.getUniqueId();
        boolean newState = !isGodMode(uuid);
        godMode.put(uuid, newState);

        String status = newState ? getMessage("enabled") : getMessage("disabled");
        target.sendMessage(getMessage("godToggled").replace("%status%", status));
    }

    public EclipsePlr getPlayer(UUID uuid) { return uuid == null ? null : new EclipsePlr(uuid); }
    public EclipsePlr getPlayer(Player player) { return player == null ? null : new EclipsePlr(player.getUniqueId()); }
}
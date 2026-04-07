package me.catst0day.capi;

import me.catst0day.capi.API.EventListeners.CAPIOnEntityDamageEvent;
import me.catst0day.capi.API.EventListeners.CAPIOnItemPickupEvent;
import me.catst0day.capi.API.GUI.CAPIGuiListener;
import me.catst0day.capi.API.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.API.EventListeners.BossBarInfo;
import me.catst0day.capi.API.Managers.HomeManager;
import me.catst0day.capi.API.Managers.WarpManager;
import me.catst0day.capi.API.Utils.Util;
import me.catst0day.capi.Commands.*;
import me.catst0day.capi.API.Entity.Listeners.CAPIOnEntityHitEventListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static me.catst0day.capi.API.Utils.Util.log;

public class CAPI extends JavaPlugin {
    private static me.catst0day.capi.CAPI instance;
    private final HashMap<UUID, Boolean> godMode = new HashMap<>();
    private final HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private final HashMap<UUID, Boolean> inArena = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private int teleportDelay;

    private YamlConfiguration langConfig;
    private String currentLang;

    private Home home;
    private Sethome sethome;
    private Delhome delhome;
    private Tp tp;
    private Tphere tphere;
    private SetSpawn setSpawn;
    private Spawn spawn;
    private Spec spec;
    private Gm gm;
    private God god;
    private Heal heal;
    private Feed feed;
    private Fix fix;
    private Near near;
    private Vanish v;
    private Enchant enchant;
    private Tpa tpa;
    private Tpaaccept tpaccept;
    private Tpdeny tpdeny;
    private Help help;
    private Reload CAPI;
    private Sudo s;
    private Warp warp;
    private SetWarp setwarp;
    private HomeManager homeManager;
    private WarpManager warpManager;
    public String prefix;
    private FileDownloader fileDownloader;

    @Override
    public void onEnable() {
        Util.printStartupBanner(this);
        saveDefaultConfig();
        reloadConfig();
        fileDownloader = new FileDownloader(this);
        loadTranslations();
        this.homeManager = new HomeManager(this);
        this.home = new Home(this);
        this.sethome = new Sethome(this);
        this.delhome = new Delhome(this);
        this.tp = new Tp(this);
        this.tphere = new Tphere(this);
        this.setSpawn = new SetSpawn(this);
        this.spawn = new Spawn(this);
        this.spec = new Spec(this);
        this.gm = new Gm(this);
        this.god = new God(this);
        this.heal = new Heal(this);
        this.feed = new Feed(this);
        this.fix = new Fix(this);
        this.near = new Near(this);
        this.v = new Vanish(this);
        this.enchant = new Enchant(this);
        this.tpa = new Tpa(this);
        this.tpaccept = new Tpaaccept(this);
        this.tpdeny = new Tpdeny(this);
        this.help = new Help(this);
        this.CAPI = new Reload(this);
        this.s = new Sudo(this);
        this.warp = new Warp(this);
        this.setwarp = new SetWarp(this);
        this.prefix = ChatColor.GOLD + "[CAPI] " + ChatColor.DARK_AQUA;
        registerCommand("warp", warp);
        registerCommand("setwarp", setwarp);
        registerCommand("tp", tp);
        registerCommand("teleport", tp);
        registerCommand("tphere", tphere);
        registerCommand("setspawn", setSpawn);
        registerCommand("Spawn", spawn);
        registerCommand("spec", spec);
        registerCommand("gm", gm);
        registerCommand("god", god);
        registerCommand("heal", heal);
        registerCommand("feed", feed);
        registerCommand("eat", feed);
        registerCommand("fix", fix);
        registerCommand("repair", fix);
        registerCommand("near", near);
        registerCommand("vanish", v);
        registerCommand("enchant", enchant);
        registerCommand("tpa", tpa);
        registerCommand("tpaccept", tpaccept);
        registerCommand("tpdeny", tpdeny);
        registerCommand("help", help);
        registerCommand("CAPI", CAPI);
        registerCommand("Sudo", s);
        registerCommand("home", home);
        registerCommand("sethome", sethome);
        registerCommand("delhome", delhome);

        getServer().getPluginManager().registerEvents(new CAPIOnEntityHitEventListener(this), this);
        getServer().getPluginManager().registerEvents(new CAPIGuiListener(), this);
        getServer().getPluginManager().registerEvents((Listener) new CAPIOnEntityDamageEvent(this), this);
        getServer().getPluginManager().registerEvents((Listener) new CAPIOnItemPickupEvent(this), this);
    }

    public CAPI getInstance() {
        if (instance == null) {
            instance = JavaPlugin.getPlugin(CAPI.class);
        }
        return instance;
    }

    public HomeManager getHomeManager() {
        if (this.homeManager == null) {
            this.homeManager = new HomeManager(this);
        }
        return this.homeManager;
    }

    public WarpManager getWarpManager() {
        if (this.warpManager == null) {
            this.warpManager = new WarpManager(this);
        }
        return this.warpManager;
    }


    public void loadTranslations() {
        currentLang = getConfig().getString("lang", "EN").toUpperCase();
        File translationsFolder = new File(getDataFolder(), "Translations");
        File langFile = new File(translationsFolder, currentLang + ".yml");

        if (!translationsFolder.exists() && !translationsFolder.mkdirs()) {
            log("Failed to create Translations folder!");
            langConfig = new YamlConfiguration();
            return;
        }

// Если файла перевода нет — загружаем из ресурсов
        if (!langFile.exists()) {
            String resourcePath = "Translations/" + currentLang + ".yml";
            fileDownloader.downloadFromResources(
                    resourcePath,
                    "plugins/CatAPI/Translations/" + currentLang + ".yml",
                    true
            );

            if (!langFile.exists()) {
                currentLang = "EN";
                langFile = new File(translationsFolder, "EN.yml");
                fileDownloader.downloadFromResources(
                        "Translations/EN.yml",
                        "plugins/CatAPI/Translations/EN.yml",
                        true
                );
            }
        }

        try {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            log("Successfully loaded translation: " + langFile);
        } catch (Exception e) {
            log("Error while loading translation: " + e.getMessage());
            langConfig = new YamlConfiguration();
        }
    }

    public void registerCommand(String commandName, CommandExecutor executor) {
        if (executor == null) {
            log("Executor for command '%s' is null!".formatted(commandName));
            return;
        }

        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            log("Command '%s' registered.".formatted(commandName));
        } else {
            log("Command '%s' was not registered.".formatted(commandName));
        }
    }

    // API
    public boolean isGodMode(UUID uuid) {
        return godMode.getOrDefault(uuid, false);
    }

    public void toggleGodMode(Player player, String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : player;

        if (target == null) {
            player.sendMessage(getMessage("playerNotFound"));
            return;
        }

        togglePlayerGodMode(target, player);
    }

    private void togglePlayerGodMode(Player target, Player sender) {
        UUID targetUUID = target.getUniqueId();
        boolean isGodMode = godMode.getOrDefault(targetUUID, false);
        godMode.put(targetUUID, !isGodMode);

        String status = isGodMode ? getMessage("godDisabled") : getMessage("godEnabled");
        target.sendMessage(getMessage("godToggled").replace("%status%", status));

        if (!target.equals(sender)) {
            sender.sendMessage(getMessage("godToggleSuccess")
                    .replace("%player%", target.getName()));
        }
    }


    public boolean isFlyMode(UUID uuid) {
        return flyMode.getOrDefault(uuid, false);
    }

    public void setFlyMode(UUID uuid, boolean enabled) {
        flyMode.put(uuid, enabled);
    }

    public HashMap<UUID, UUID> getTpaRequests() {
        return tpaRequests;
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public String getMessage(String key) {
        String messagePath = "messages." + key;
        String defaultValue = "Msg '" + key + "' not loaded";

        if (langConfig == null) {
            getLogger().severe("Translation not loaded!");
            return defaultValue;
        }

        if (langConfig.contains(messagePath)) {
            String message = langConfig.getString(messagePath, defaultValue);
            return ChatColor.translateAlternateColorCodes('&', message);
        }

        getLogger().warning("No key for translation: " + key);
        return defaultValue;
    }

    public String getGameModeMessage(GameMode mode) {
        String messagePath = "messages.gameModeMessages." + mode.name();
        String defaultValue = "Msg '" + mode + "' not loaded";

        if (langConfig == null || !langConfig.contains(messagePath)) {
            getLogger().warning("No translation: " + mode.name());
            return defaultValue;
        }

        String message = langConfig.getString(messagePath, defaultValue);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void teleport(Player player, Location target) {
// Определяем задержку по пермишенам
        long delayTicks = 0L;
        int delaySeconds = 0;

        for (String permission : player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(perm -> perm.startsWith("catapi.teleport.delay."))
                .toArray(String[]::new)) {
            String suffix = permission.substring("catapi.teleport.delay.".length());
            try {
                delaySeconds = Integer.parseInt(suffix);
                delayTicks = delaySeconds * 20L; // конвертируем в тики
                break; // берём первый подходящий пермишен
            } catch (NumberFormatException e) {
                continue; // пропускаем некорректные значения
            }
        }

// Если нет подходящего пермишена — используем задержку по умолчанию (3 секунды = 60 тиков)
        if (delayTicks == 0) {
            delaySeconds = 7;
            delayTicks = 140L;
        }

// Если задержка нулевая — телепортируем сразу
        if (delaySeconds == 0) {
            if (target != null && target.getWorld() != null) {
                player.teleport(target);
                player.sendMessage(getMessage("teleportSuccess"));
            } else {
                player.sendMessage(getMessage("invalidLocation"));
            }
            return;
        }

// Создаём боссбар
        BossBarInfo bossBarInfo = new BossBarInfo(this, player, "teleport_delay");
        bossBarInfo.setTitleOfBar(getMessage("teleportWithDelay")
                .replace("%seconds%", String.valueOf(delaySeconds)));
        bossBarInfo.setColor(BarColor.BLUE);
        bossBarInfo.setStyle(BarStyle.SOLID);
        bossBarInfo.setSeconds(delaySeconds);

// Сохраняем боссбар для возможного управления
        bossBars.put(player.getUniqueId(), bossBarInfo.getBar());

// Запускаем таймер обратного отсчёта
        final int[] taskIdHolder = new int[1];

        taskIdHolder[0] = CAPIMainScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
// Проверяем валидность игрока и активность боссбара
                if (!player.isValid() || !bossBarInfo.stillRunning()) {
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                    bossBarInfo.remove();
                    bossBars.remove(player.getUniqueId());
                    return;
                }

// Обновляем текст боссбара с оставшимся временем
                long leftDuration = bossBarInfo.getLeftDuration();
                int secondsRemaining = (int) (leftDuration / 1000);

                if (secondsRemaining <= 0) {
// Выполняем телепорт, если чанк загружен
                    if (target != null && target.getWorld() != null &&
                            target.getWorld().isChunkLoaded(
                                    target.getBlockX() >> 4,
                                    target.getBlockZ() >> 4)) {
                        player.teleport(target);
                        player.sendMessage(getMessage("teleportSuccess"));
                    } else {
                        player.sendMessage(getMessage("chunkNotLoaded"));
                    }

// Убираем боссбар и отменяем задачу
                    bossBarInfo.remove();
                    bossBars.remove(player.getUniqueId());
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                } else {
// Обновляем заголовок боссбара
                    bossBarInfo.setTitleOfBar(
                            getMessage("teleportWithDelay").replace("%seconds%",
                                    String.valueOf(secondsRemaining))
                    );
                }
            }
        }, 0L, 20L); // Обновление каждую секунду (20 тиков)
    }
}
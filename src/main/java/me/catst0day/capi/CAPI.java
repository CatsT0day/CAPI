package me.catst0day.capi;

import me.catst0day.capi.API.Util;
import me.catst0day.capi.Commands.*;
import me.catst0day.capi.API.Managers.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BossBar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static me.catst0day.capi.API.Util.log;

public class CatAPI extends JavaPlugin {
    private static CatAPI instance;
    private HashMap<UUID, Boolean> godMode = new HashMap<>();
    private HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private HashMap<UUID, Boolean> inArena = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private int teleportDelay;

    private FileConfiguration langConfig;
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
    public String prefix;
    @Override
    public void onEnable() {
        Util.printStartupBanner(this);
        saveDefaultConfig();
        reloadConfig();
        loadTranslations();
        teleportDelay = getConfig().getInt("tpa.teleport-delay", 10);
        this.homeManager = new HomeManager(this);
        this.home = new Home(this, this.homeManager);
        this.sethome = new Sethome(this, this.homeManager);
        this.delhome = new Delhome(this, this.homeManager);
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
    }

    public static CatAPI getInstance() {
        if (instance == null) {
            instance = (CatAPI) JavaPlugin.getPlugin(CatAPI.class);
        }
        return instance;
    }
        public void loadTranslations() {
        currentLang = getConfig().getString("lang", "EN").toUpperCase();
        File translationsFolder = new File(getDataFolder(), "Translations");
        File langFile = new File(translationsFolder, currentLang + ".yml");

        if (!translationsFolder.exists() && !translationsFolder.mkdirs()) {
            log("failed to create Translations!");
            langConfig = new YamlConfiguration();
            return;
        }

        if (!langFile.exists()) {
            saveResource("Translations/" + currentLang + ".yml", false);
            if (!langFile.exists()) {
                currentLang = "EN";
                langFile = new File(translationsFolder, "EN.yml");
                saveResource("Translations/EN.yml", false);
            }
        }

        try {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            log("successfully Loaded translation:" + langFile);
        } catch (Exception e) {
            log("error while loading translation!: " + e.getMessage());
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

        String status = isGodMode ? "выключен" : "включен";
        target.sendMessage(getMessage("godToggled").replace("%s", status));

        if (!target.equals(sender)) {
            sender.sendMessage(getMessage("godToggleSuccess").replace("%s", target.getName()));
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
            getLogger().severe("Not loaded translation!");
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

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        if (isGodMode(damager.getUniqueId())) {
            event.setCancelled(true);
            damager.sendMessage(getMessage("godDamageDenied"));
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (isGodMode(player.getUniqueId()) && event.getRightClicked() instanceof LivingEntity) {
            event.setCancelled(true);
            player.sendMessage(getMessage("godDamageDenied"));
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (isGodMode(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(getMessage("godModeItemPickupDenied"));
        }
    }
    }

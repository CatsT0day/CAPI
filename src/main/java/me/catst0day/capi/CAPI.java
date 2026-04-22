package me.catst0day.capi;

import me.catst0day.capi.Bossbar.CAPIBarStyle;
import me.catst0day.capi.Chat.CAPIChatColor;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIAliasManager;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.Bossbar.CAPIBossBarInfo;
import me.catst0day.capi.Managers.CAPIHomeManager;
import me.catst0day.capi.Managers.CAPIWarpManager;
import me.catst0day.capi.User.CAPIUser;
import me.catst0day.capi.Utils.VersionChecker;
import org.bukkit.*;
import me.catst0day.capi.Bossbar.CAPIBarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.catst0day.capi.CAPIOnEnableInitter.langConfig;
import static me.catst0day.capi.Utils.Util.log;

public class CAPI extends JavaPlugin {
    private static me.catst0day.capi.CAPI instance;
    private final HashMap<UUID, Boolean> godMode = new HashMap<>();
    private final HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static boolean fullyLoaded = false;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private CAPIPermissionManager permManager;
    private CAPIAliasManager aliasManager;
    private VersionChecker versionCheckManager;
    private CAPICommandTemplate CmdTemplate;
    public static String prefix;

    @Override
    public void onEnable() {

        CAPIOnEnableInitter initializer = new CAPIOnEnableInitter();
        initializer.OnEnable(this);
    }


    public static CAPI getInstance() {
        if (instance == null) {
            instance = JavaPlugin.getPlugin(CAPI.class);
        }
        return instance;
    }

    public CAPIHomeManager getHomeManager() {
        if (this.homeManager == null) {
            this.homeManager = new CAPIHomeManager(this);
        }
        return this.homeManager;
    }

    public CAPIPermissionManager getPermissionManager() {
     if (this.permManager == null) {
     this.permManager = new CAPIPermissionManager(this);
           }
         return this.permManager;
    }


    public CAPIWarpManager getWarpManager() {
        if (this.warpManager == null) {
            this.warpManager = new CAPIWarpManager(this);
        }
        return this.warpManager;
    }


    public VersionChecker getVersionCheckManager() {
        if (versionCheckManager == null) {
            this.versionCheckManager = new VersionChecker(
                    CAPI.getInstance(),
                    "CatsT0day", "CAPI"
            );
        }
        return this.versionCheckManager;
    }

    // API
    public boolean isGodMode(UUID uuid) {
        return godMode.getOrDefault(uuid, false);
    }

    public void toggleGodMode(Player player, String[] args) {
        Player target = args.length == 1 ? Bukkit.getPlayer(args[0]) : player;

        if (target == null) {
            Objects.requireNonNull(player).sendMessage(getMessage("playerNotFound"));
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

    public String getMessage(String key) {
        String messagePath = "messages." + key;
        String defaultValue = "Msg '" + key + "' not loaded";

        if (langConfig == null) {
            getLogger().severe("Translation not loaded!");
            return defaultValue;
        }

        if (langConfig.contains(messagePath)) {
            String message = langConfig.getString(messagePath, defaultValue);
            return CAPIChatColor.translate(message);
        }

        getLogger().warning("No key for translation: " + key);
        return defaultValue;
    }

    public String sendCFGmessage(Player plr, String key) {
        String messagePath = "messages." + key;
        String defaultValue = "Msg '" + key + "' not loaded";

        if (langConfig == null) {
            log("Translation not loaded!");
            return CAPI.getInstance().getUser(plr).sendMsg(defaultValue);
        }

        if (langConfig.contains(messagePath)) {
            String message = langConfig.getString(messagePath, defaultValue);
            return CAPI.getInstance().getUser(plr).sendMsg(message);
        }

        log("No key for translation: " + key);
        return defaultValue;
    }
    public String sendCFGmessage(CommandSender plr, String key) {
        String messagePath = "messages." + key;
        String defaultValue = "Msg '" + key + "' not loaded";

        if (langConfig == null) {
            log("Translation not loaded!");
            return getUser((Player) plr).sendMsg(defaultValue);
        }

        if (langConfig.contains(messagePath)) {
            String message = langConfig.getString(messagePath, defaultValue);
            return getUser((Player) plr).sendMsg(message);
        }

        log("No key for translation: " + key);
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
        return CAPIChatColor.translate(message);
    }



    public void teleport(Player player, Location target) {
        long delayTicks = 0L;
        int delaySeconds = 0;

        for (String permission : player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(perm -> perm.startsWith("catapi.teleport.delay."))
                .toArray(String[]::new)) {
            String suffix = permission.substring("catapi.teleport.delay.".length());
            try {
                delaySeconds = Integer.parseInt(suffix);
                delayTicks = delaySeconds * 20L;
                break;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        if (delayTicks == 0) {
            delaySeconds = 7;
            delayTicks = 140L;
        }

        if (delaySeconds == 0) {
            if (target != null && target.getWorld() != null) {
                player.teleport(target);
                player.sendMessage(getMessage("teleportSuccess"));
            } else {
                player.sendMessage(getMessage("invalidLocation"));
            }
            return;
        }


        CAPIBossBarInfo bossBarInfo = new CAPIBossBarInfo(this, player, "teleport_delay");
        bossBarInfo.setTitleOfBar(getMessage("teleportWithDelay")
                .replace("%seconds%", String.valueOf(delaySeconds)));
        bossBarInfo.setColor(CAPIBarColor.BLUE);
        bossBarInfo.setStyle(CAPIBarStyle.SOLID);
        bossBarInfo.setSeconds(delaySeconds);


        bossBars.put(player.getUniqueId(), bossBarInfo.getBar());


        final int[] taskIdHolder = new int[1];

        taskIdHolder[0] = CAPIMainScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {

                if (!player.isValid() || !bossBarInfo.stillRunning()) {
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                    bossBarInfo.remove();
                    bossBars.remove(player.getUniqueId());
                    return;
                }

                long leftDuration = bossBarInfo.getLeftDuration();
                int secondsRemaining = (int) (leftDuration / 1000);

                if (secondsRemaining <= 0) {
                    if (target != null && target.getWorld() != null &&
                            target.getWorld().isChunkLoaded(
                                    target.getBlockX() >> 4,
                                    target.getBlockZ() >> 4)) {
                        player.teleport(target);
                        player.sendMessage(getMessage("teleportSuccess"));
                    } else {
                        player.sendMessage(getMessage("chunkNotLoaded"));
                    }

                    bossBarInfo.remove();
                    bossBars.remove(player.getUniqueId());
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                } else {
                    bossBarInfo.setTitleOfBar(
                            getMessage("teleportWithDelay").replace("%seconds%",
                                    String.valueOf(secondsRemaining))
                    );
                }
            }
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {

    }
    public CAPIUser getUser(CommandSender sender, String playerName) {
        return getUser(playerName);
    }

    public CAPIUser getUser(Player player) {
        return new CAPIUser(player.getUniqueId());
    }


    public CAPIUser getUser(String playerName) {
        if (playerName == null) {
            return null;
        }

        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            return new CAPIUser(onlinePlayer.getUniqueId());
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return new CAPIUser(offlinePlayer.getUniqueId());
        }

        return null;
    }

    public CAPIUser getUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new CAPIUser(uuid);
    }

    public CAPIAliasManager getAliasManager() {
        return aliasManager;
    }
}



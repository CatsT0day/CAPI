package me.catst0day.capi;

import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.EventListeners.BossBarInfo;
import me.catst0day.capi.Managers.CAPIHomeManager;
import me.catst0day.capi.Managers.CAPIWarpManager;
import me.catst0day.capi.User.CAPIUser;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.HashMap;
import java.util.UUID;

import static me.catst0day.capi.CAPIOnEnableInitter.langConfig;

public class CAPI extends JavaPlugin {
    private static me.catst0day.capi.CAPI instance;
    private CAPIOnEnableInitter initializer;
    private final HashMap<UUID, Boolean> godMode = new HashMap<>();
    private final HashMap<UUID, Boolean> flyMode = new HashMap<>();
    private final HashMap<UUID, UUID> tpaRequests = new HashMap<>();
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();

    public static boolean fullyLoaded = false;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private CAPIPermissionManager permManager;
    private CommandTemplate CmdTemplate;
    public static String prefix;

    @Override
    public void onEnable() {
        this.initializer = new CAPIOnEnableInitter();
        this.initializer.OnEnable(this);
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
            this.permManager = new CAPIPermissionManager();
        }
        return this.permManager;
    }


    public CAPIWarpManager getWarpManager() {
        if (this.warpManager == null) {
            this.warpManager = new CAPIWarpManager(this);
        }
        return this.warpManager;
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


        BossBarInfo bossBarInfo = new BossBarInfo(this, player, "teleport_delay");
        bossBarInfo.setTitleOfBar(getMessage("teleportWithDelay")
                .replace("%seconds%", String.valueOf(delaySeconds)));
        bossBarInfo.setColor(BarColor.BLUE);
        bossBarInfo.setStyle(BarStyle.SOLID);
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

    // Основной метод — получает CAPIUser по имени игрока
    public CAPIUser getUser(String playerName) {
        if (playerName == null) {
            return null;
        }

        // Сначала ищем онлайн‑игрока
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            return new CAPIUser(onlinePlayer.getUniqueId());
        }

        // Если онлайн не найден, ищем оффлайн‑игрока по имени
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        // Проверяем, что игрок существует (играл хотя бы раз)
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return new CAPIUser(offlinePlayer.getUniqueId());
        }

        return null; // Игрок не найден
    }

    // Дополнительный вариант — по UUID (самый быстрый, если UUID уже известен)
    public CAPIUser getUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new CAPIUser(uuid);
    }
}



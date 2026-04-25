package me.catst0day.capi;

import me.catst0day.capi.Bossbar.*;
import me.catst0day.capi.Chat.CAPIChatColor;
import me.catst0day.capi.Managers.*;
import me.catst0day.capi.Schedulers.CAPIMainScheduler;
import me.catst0day.capi.User.CAPIUser;
import me.catst0day.capi.Utils.VersionChecker;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.catst0day.capi.CAPIOnEnableInitter.langConfig;

import static me.catst0day.capi.Utils.Util.log;

public class CAPI extends JavaPlugin {
    private static CAPI instance;

    private final Map<UUID, Boolean> godMode = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> flyMode = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public static boolean fullyLoaded = false;
    public static String prefix;

    private CAPIHomeManager homeManager;
    private CAPIWarpManager warpManager;
    private CAPIPermissionManager permManager;
    private CAPIAliasManager aliasManager;
    private VersionChecker versionCheckManager;

    @Override
    public void onEnable() {
        instance = this;
        new CAPIOnEnableInitter().OnEnable(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
    }

    public static CAPI getInstance() {
        return instance == null ? JavaPlugin.getPlugin(CAPI.class) : instance;
    }

    // --- some getters here btw---

    public CAPIHomeManager getHomeManager() {
        return homeManager == null ? (homeManager = new CAPIHomeManager(this)) : homeManager;
    }

    public CAPIPermissionManager getPermissionManager() {
        return permManager == null ? (permManager = new CAPIPermissionManager(this)) : permManager;
    }

    public CAPIWarpManager getWarpManager() {
        return warpManager == null ? (warpManager = new CAPIWarpManager(this)) : warpManager;
    }

    public CAPIAliasManager getAliasManager() {
        return aliasManager == null ? (aliasManager = new CAPIAliasManager(this)) : aliasManager;
    }

    public VersionChecker getVersionCheckManager() {
        return versionCheckManager == null ? (versionCheckManager = new VersionChecker(this, "CatsT0day", "CAPI")) : versionCheckManager;
    }

    // --- API Methods ---

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

        String status = newState ? getMessage("godEnabled") : getMessage("godDisabled");
        target.sendMessage(getMessage("godToggled").replace("%status%", status));

        if (sender != null && !target.equals(sender)) {
            sender.sendMessage(getMessage("godToggleSuccess").replace("%player%", target.getName()));
        }
    }

    // --- Msg sys ---

    public String getMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";

        String path = "messages." + key;
        String raw = langConfig.getString(path);

        if (raw == null) {
            getLogger().warning("Missing translation key: " + key);
            return "Msg '" + key + "' missing";
        }
        return CAPIChatColor.translate(raw);
    }
    public String getGameModeMessage(String key) {
        if (langConfig == null) return "§cLang not loaded";

        String path = "messages.gamemodes" + key;
        String raw = langConfig.getString(path);

        if (raw == null) {
            getLogger().warning("Missing translation key: " + key);
            return "Msg '" + key + "' missing";
        }
        return CAPIChatColor.translate(raw);
    }


    public String sendCFGmessage(CommandSender sender, String key) {
        String msg = getMessage(key);
        if (sender instanceof Player player) {
            return getUser(player).sendMsg(msg);
        }
        sender.sendMessage(msg);
        return msg;
    }

    // --- TP here btw ---

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

        startTeleportTask(player, target, delaySeconds);
    }

    private void startTeleportTask(Player player, Location target, int seconds) {
        CAPIBossBarInfo barInfo = new CAPIBossBarInfo(this, player, "teleport_delay");
        barInfo.setColor(CAPIBarColor.BLUE);
        barInfo.setStyle(CAPIBarStyle.SOLID);
        barInfo.setSeconds(seconds);

        bossBars.put(player.getUniqueId(), barInfo.getBar());

        CAPIMainScheduler.runTaskTimer(this, task -> {
            if (!player.isOnline() || !barInfo.stillRunning()) {
                cleanupTeleport(player, barInfo, task.getTaskId());
                return;
            }

            int remaining = (int) (barInfo.getLeftDuration() / 1000);
            if (remaining <= 0) {
                player.teleport(target);
                player.sendMessage(getMessage("teleportSuccess"));
                cleanupTeleport(player, barInfo, task.getTaskId());
            } else {
                barInfo.setTitleOfBar(getMessage("teleportWithDelay").replace("%seconds%", String.valueOf(remaining)));
            }
        }, 0L, 20L);
    }

    private void cleanupTeleport(Player p, CAPIBossBarInfo bar, int taskId) {
        bar.remove();
        bossBars.remove(p.getUniqueId());
        Bukkit.getScheduler().cancelTask(taskId);
    }

    // --- User Things ---

    public CAPIUser getUser(UUID uuid) { return uuid == null ? null : new CAPIUser(uuid); }
    public CAPIUser getUser(Player player) { return player == null ? null : new CAPIUser(player.getUniqueId()); }

    public CAPIUser getUser(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null) return getUser(p);

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return (op.hasPlayedBefore()) ? new CAPIUser(op.getUniqueId()) : null;
    }
}
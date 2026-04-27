package me.catst0day.Eclipse.Entity.Player;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Utils.HexUtil;
import me.catst0day.Eclipse.Entity.EclipseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EclipsePlr {
    private final UUID uuid;
    private Player player;
    private EclipseEntity entity;

    private String name;
    private String displayName;
    private Location logOutLocation;
    private Location deathLoc;
    private Location lastTeleportLocation;

    private final Set<UUID> ignores = new HashSet<>();
    private final Map<String, Long> kitCooldowns = new HashMap<>();
    private final Map<String, Object> metadata = new HashMap<>();
    private final List<Long> voteTimestamps = new ArrayList<>();

    private boolean silenceMode, allowFlight, flying, vanishMode, invulnerable;
    private double health = 20.0;
    private int foodLevel = 20, level = 0;
    private float exp = 0.0f, saturation = 5.0f;
    private long lastLogin, lastLogoff, totalPlayTime, invulnerabilityEndTime, firstJoinTime;

    private static final Map<UUID, Player> onlinePlayersCache = new ConcurrentHashMap<>();

    public EclipsePlr(UUID uuid) {
        this.uuid = uuid;
        this.player = refreshOnlineStatus();
        if (this.player != null) {
            this.name = this.player.getName();
            syncWithPlayer(this.player);
        }
    }

    private void syncWithPlayer(Player p) {
        this.health = p.getHealth();
        this.foodLevel = p.getFoodLevel();
        this.saturation = p.getSaturation();
        this.level = p.getLevel();
        this.exp = p.getExp();
        this.allowFlight = p.getAllowFlight();
        this.flying = p.isFlying();
    }


    public boolean isOnline() {
        return refreshOnlineStatus() != null;
    }

    private Player refreshOnlineStatus() {
        if (player != null && player.isOnline()) return player;

        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            this.player = online;
            onlinePlayersCache.put(uuid, online);
        } else {
            this.player = null;
            onlinePlayersCache.remove(uuid);
        }
        return this.player;
    }

    public Player getPlayer() {
        return refreshOnlineStatus();
    }


    public EclipseEntity getEntity() {
        if (entity == null) {
            Player p = getPlayer();
            if (p != null) entity = new EclipseEntity(p);
        }
        return entity;
    }


    public String getName() {
        if (name == null) {
            Player p = getPlayer();
            name = (p != null) ? p.getName() : Bukkit.getOfflinePlayer(uuid).getName();
        }
        return name;
    }

    public String sendMsg(String message) {
        if (message == null) return null;
        Player p = getPlayer();
        if (p == null) return message;

        p.sendMessage(HexUtil.translateHexAndAlternateColorCodes(message));
        return message;
    }


    public Location getLocation() {
        Player p = getPlayer();
        return (p != null) ? p.getLocation() : logOutLocation;
    }

    public boolean setHome(String name, Location loc) {
        return Eclipse.getI().getHomeManager().setHome(uuid, name, loc);
    }
    public void teleportAsynchronously(@NotNull Location loc) {
        teleportAsynchronously(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    public void teleportAsynchronously(@NotNull Entity entity) {
        this.teleportAsynchronously(entity.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    public void teleportAsynchronously(@NotNull Entity entity, PlayerTeleportEvent.TeleportCause cause) {
        this.teleportAsynchronously(entity.getLocation(), cause);
    }

    public void teleportAsynchronously(@NotNull Location loc, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        java.util.concurrent.CompletableFuture<Boolean> future = new java.util.concurrent.CompletableFuture<>();
        loc.getWorld().getChunkAtAsyncUrgently(loc).thenAccept((chunk) -> future.complete(teleport(loc, cause))).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
        });
    }
    public boolean teleport(@NotNull Location loc, PlayerTeleportEvent.TeleportCause cause) {
        player.teleport(loc, cause);
        return false;
    }

    public void setGameMode(GameMode mode) {
        this.player = getPlayer();
        if (player != null) player.setGameMode(mode);
    }

    public void setHealth(double health) {
        this.health = health;
        Player p = getPlayer();
        if (p != null) p.setHealth(health);
    }

    public void setAllowFlight(boolean allow) {
        this.allowFlight = allow;
        Player p = getPlayer();
        if (p != null) p.setAllowFlight(allow);
    }


    public void addIgnore(UUID other) { ignores.add(other); }
    public void removeIgnore(UUID other) { ignores.remove(other); }
    public boolean isIgnoring(UUID other) { return ignores.contains(other); }

    public void setMetadata(String key, Object val) { metadata.put(key, val); }
    public Object getMetadata(String key) { return metadata.get(key); }

    public UUID getUniqueId() { return uuid; }
    public Location getDeathLoc() { return deathLoc; }
    public void setDeathLoc(Location loc) { this.deathLoc = loc; }
    public long getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(long time) { this.totalPlayTime = time; }
}
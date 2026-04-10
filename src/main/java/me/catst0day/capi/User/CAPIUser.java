
package me.catst0day.capi.User;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Entity.CAPIEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CAPIUser {
    private UUID uuid;
    private Player player;
    private CAPIEntity entity;
    private String name;
    private String displayName;
    private Location logOutLocation;
    private long lastLogin = 0L;
    private long lastLogoff = 0L;
    private long totalPlayTime = 0L;
    private Location deathLoc;
    private Location lastTeleportLocation;
    private boolean isFakeAccount = false;
    private Set<UUID> ignores;
    private boolean silenceMode = false;
    private boolean allowFlight = false;
    private boolean flying = false;
    private Map<String, Long> kitCooldowns;
    private int voteCount = 0;
    private List<Long> voteTimestamps;
    private String customSkin;
    private GameMode gameMode;
    private double health = 20.0;
    private int foodLevel = 20;
    private float saturation = 5.0f;
    private Map<String, Object> metadata;
    private List<PotionEffect> activeEffects;
    private boolean invulnerable = false;
    private long invulnerabilityEndTime = 0L;
    private boolean vanishMode = false;
    private Location firstJoinLocation;
    private int level = 0;
    private float exp = 0.0f;
    private long firstJoinTime = 0L;

    private static final Map<UUID, Player> onlinePlayersCache = new ConcurrentHashMap<>();

    public CAPIUser(UUID uuid) {
        this.uuid = uuid;
        this.player = getOnlinePlayer(uuid);
        if (this.player != null) {
            this.name = this.player.getName();
            this.entity = new CAPIEntity(player);
            loadFromPlayer(player);
        } else {
            this.entity = null;
        }
        this.ignores = new HashSet<>();
        this.kitCooldowns = new HashMap<>();
        this.voteTimestamps = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.activeEffects = new ArrayList<>();
    }

    private void loadFromPlayer(Player player) {
        this.gameMode = player.getGameMode();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.level = player.getLevel();
        this.exp = player.getExp();
    }


    public CAPIEntity getEntity() {
        if (entity == null && isOnline()) {
            entity = new CAPIEntity(getPlayer());
        }
        return entity;
    }


    public String getEntityName() {
        CAPIEntity ent = getEntity();
        return ent != null ? ent.getName() : getName();
    }

    public String getEntityCustomName() {
        CAPIEntity ent = getEntity();
        return ent != null ? ent.getCustomName() : getDisplayName();
    }

    public org.bukkit.inventory.Inventory getEntityInventory() {
        CAPIEntity ent = getEntity();
        return ent != null ? ent.getInventory() : null;
    }

    public boolean isEntityLiving() {
        CAPIEntity ent = getEntity();
        return ent != null && ent.isLiving();
    }

    public boolean isEntityPlayer() {
        CAPIEntity ent = getEntity();
        return ent != null && ent.isPlayer();
    }

    public double getEntityMaxHealth() {
        CAPIEntity ent = getEntity();
        return ent != null ? CAPIEntity.getMaxHealth(ent.getEnt()) : health;
    }

    public String formatEntityInfo() {
        CAPIEntity ent = getEntity();
        return ent != null ? ent.formatInfo() : "No entity data";
    }

    public static boolean isItemFrame(Entity entity) {
        return CAPIEntity.isItemFrame(entity);
    }

    public ItemStack setEntityType(ItemStack itemStack, EntityType type) {
        return CAPIEntity.setEntityType(itemStack, type);
    }

    public String serializeEntity(Entity entity) {
        return CAPIEntity.serialize(entity);
    }

    public Entity deserializeEntity(String data) {
        return CAPIEntity.deserialize(data);
    }

    public boolean setHome(String homeName, Location location) {
        return CAPI.getInstance().getHomeManager().setHome(uuid, homeName, location);
    }

    public Location getHome(String homeName) {
        return CAPI.getInstance().getHomeManager().getHome(uuid, homeName);
    }

    public boolean deleteHome(String homeName) {
        return CAPI.getInstance().getHomeManager().deleteHome(uuid, homeName);
    }

    public List<String> getPlayerHomes() {
        return CAPI.getInstance().getHomeManager().getPlayerHomes(uuid);
    }

    public boolean isOnline() {
        if (player != null && player.isOnline()) {
            return true;
        }
        player = getOnlinePlayer(uuid);
        if (player != null) {
            entity = new CAPIEntity(player);
        }
        return player != null;
    }

    public Location getLogOutLocation() {
        if (logOutLocation == null && isOnline()) {
            setLogOutLocation(getPlayer().getLocation());
        }
        return logOutLocation;
    }

    public void setLogOutLocation(Location location) {
        this.logOutLocation = location;
    }

    public Location getLocation() {
        return isOnline() ? getPlayer().getLocation() : getLogOutLocation();
    }

    public Player getPlayer() {
        Player online = getOnlinePlayer(uuid);
        if (online != null) {
            player = online;
            // Обновляем CAPIEntity при получении онлайн-игрока
            if (entity == null) {
                entity = new CAPIEntity(player);
            }
        }
        return player;
    }

    public String getName() {
        if (isOnline()) {
            name = getPlayer().getName();
        } else if (name == null) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            name = offline.getName();
        }
        return name;
    }

    public long getLastLogin() {
        if (lastLogin == 0L) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            lastLogin = offline.getLastPlayed();
        }
        return lastLogin;
    }

    public void setLastLogin(long time) {
        this.lastLogin = time;
    }

    public long getLastLogoff() {
        if (lastLogoff == 0L) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            lastLogoff = offline.getLastPlayed();
        }
        return lastLogoff;
    }

    public void setLastLogoff(long time) {
        this.lastLogoff = time;
    }

    public Location getDeathLoc() {
        return deathLoc;
    }

    public void setDeathLoc(Location location) {
        this.deathLoc = location;
    }

    public Location getLastTeleportLocation() {
        return lastTeleportLocation;
    }

    public void setLastTeleportLocation(Location location) {
        this.lastTeleportLocation = location;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        this.player = getOnlinePlayer(uuid);
        if (this.player != null) {
            this.name = this.player.getName();
            this.entity = new CAPIEntity(player);
        }
    }

    public String getDisplayName() {
        if (isOnline()) {
            displayName = getPlayer().getDisplayName();
        } else if (displayName == null) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            displayName = offline.getName();
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isFakeAccount() {
        return isFakeAccount;
    }

    public void setFakeAccount(boolean fakeAccount) {
        isFakeAccount = fakeAccount;
    }


    public Set<UUID> getIgnores() {
        return ignores;
    }

    public void addIgnore(UUID playerUUID) {
        ignores.add(playerUUID);
    }

    public void removeIgnore(UUID playerUUID) {
        ignores.remove(playerUUID);
    }

    public boolean isSilenceMode() {
        return silenceMode;
    }

    public void setSilenceMode(boolean silenceMode) {
        this.silenceMode = silenceMode;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
        if (isOnline()) {
            getPlayer().setAllowFlight(allowFlight);
        }
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
        if (isOnline()) {
            getPlayer().setFlying(flying);
        }
    }

    public Map<String, Long> getKitCooldowns() {
        return kitCooldowns;
    }

    public void setKitCooldown(String kitName, long cooldownEndTime) {
        kitCooldowns.put(kitName, cooldownEndTime);
    }

    public long getKitCooldown(String kitName) {
        return kitCooldowns.getOrDefault(kitName, 0L);
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void incrementVoteCount() {
        voteCount++;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public List<Long> getVoteTimestamps() {
        return voteTimestamps;
    }

    public void addVoteTimestamp(long timestamp) {
        voteTimestamps.add(timestamp);
    }

    public String getCustomSkin() {
        return customSkin;
    }

    public void setCustomSkin(String customSkin) {
        this.customSkin = customSkin;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        if (isOnline()) {
            getPlayer().setGameMode(gameMode);
        }
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
        if (isOnline()) {
            getPlayer().setHealth(health);
        }
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
        if (isOnline()) {
            getPlayer().setFoodLevel(foodLevel);
        }
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        if (isOnline()) {
            getPlayer().setSaturation(saturation);
        }
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public void removeMetadata(String key) {
        metadata.remove(key);
    }

    public List<PotionEffect> getActiveEffects() {
        return activeEffects;
    }

    public void addPotionEffect(PotionEffect effect) {
        activeEffects.add(effect);
        if (isOnline()) {
            getPlayer().addPotionEffect(effect);
        }
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public long getInvulnerabilityEndTime() {
        return invulnerabilityEndTime;
    }

    public void setInvulnerabilityEndTime(long invulnerabilityEndTime) {
        this.invulnerabilityEndTime = invulnerabilityEndTime;
    }

    public boolean isVanishMode() {
        return vanishMode;
    }

    public void setVanishMode(boolean vanishMode) {
        this.vanishMode = vanishMode;
    }

    public Location getFirstJoinLocation() {
        return firstJoinLocation;
    }

    public void setFirstJoinLocation(Location firstJoinLocation) {
        this.firstJoinLocation = firstJoinLocation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        if (isOnline()) {
            getPlayer().setLevel(level);
        }
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
        if (isOnline()) {
            getPlayer().setExp(exp);
        }
    }

    public long getFirstJoinTime() {
        return firstJoinTime;
    }

    public void setFirstJoinTime(long firstJoinTime) {
        this.firstJoinTime = firstJoinTime;
    }

    public long getTotalPlayTime() {
        return totalPlayTime;
    }

    public void setTotalPlayTime(long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    private Player getOnlinePlayer(UUID uuid) {
        Player online = onlinePlayersCache.get(uuid);
        if (online == null || !online.isOnline()) {
            online = Bukkit.getPlayer(uuid);
            if (online != null) {
                onlinePlayersCache.put(uuid, online);
            }
        }
        return online;
    }
}

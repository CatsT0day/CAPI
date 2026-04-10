package me.catst0day.capi.Managers;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class CAPIPermissionManager {
    private final HashMap<UUID, HashMap<String, Long>> permissionCache = new HashMap<>();
    private static final long CACHE_DURATION = 200L; // мс

    public boolean hasPermission(Player player, CAPIPerm perm, String... args) {
        if (player == null) return false;

        UUID playerId = player.getUniqueId();
        String fullPermission = perm.getPermission(args);

        if (isCached(playerId, fullPermission)) {
            return getCachedPermission(playerId, fullPermission);
        }

        boolean hasPerm = player.hasPermission(fullPermission);
        cachePermission(playerId, fullPermission, hasPerm);

        return hasPerm;
    }


    public boolean hasPermission(Player player, CAPIPerm perm) {
        return hasPermission(player, perm, new String[0]);
    }

    private boolean isCached(UUID playerId, String permName) {
        HashMap<String, Long> playerCache = permissionCache.get(playerId);
        if (playerCache == null) return false;

        Long timestamp = playerCache.get(permName);
        if (timestamp == null) return false;

        return System.currentTimeMillis() - timestamp < CACHE_DURATION;
    }

    private boolean getCachedPermission(UUID playerId, String permName) {
        return true;
    }

    private void cachePermission(UUID playerId, String permName, boolean hasPerm) {
        if (!permissionCache.containsKey(playerId)) {
            permissionCache.put(playerId, new HashMap<>());
        }
        permissionCache.get(playerId).put(permName, System.currentTimeMillis());
    }


    public void clearPlayerCache(Player player) {
        permissionCache.remove(player.getUniqueId());
    }


    public void clearCache() {
        permissionCache.clear();
    }



    public enum CAPIPerm {
        RELOAD("catapi.reload"),
        DAY("catapi.day"),
        NIGHT("catapi.night"),
        SUDO("catapi.sudo"),
        TP("catapi.tp"),
        TPA("catapi.tpa"),
        VANISH("catapi.vanish"),
        ENCHANT("catapi.enchant"),
        TPHERE("catapi.tphere"),
        SETSPAWN("catapi.setspawn"),
        SPAWN("catapi.spawn"),
        SPEC("catapi.spec"),
        GM("catapi.gm"),
        GOD("catapi.god"),
        FLY("catapi.fly"),
        HEAL("catapi.heal"),
        FEED("catapi.feed"),
        FIX("catapi.fix"),
        NEAR("catapi.near"),
        MAXHOMES("catapi.home.max.$1"),
        NEAR_RADIUS("catapi.near.radius.$1"),
        WARP_USE("catapi.warp.use"),
        WARP_SET("catapi.warp.set"),
        COOLDOWN_BYPASS("catapi.cooldown.bypass"),
        UPDATE_NOTIFY("capi.update.notify"),
        HELP("catapi.help"),
        ALL("catapi.*"),
        HOME("catapi.home");

        private final String permission;

        CAPIPerm(String permission) {
            this.permission = permission;
        }

        public String getPermission(String... args) {
            String result = permission;
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    result = result.replace("$" + (i + 1), args[i]);
                }
            }
            return result;
        }
    }
}
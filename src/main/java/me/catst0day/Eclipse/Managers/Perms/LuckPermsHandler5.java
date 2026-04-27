
package me.catst0day.Eclipse.Managers.Perms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.catst0day.Eclipse.Utils.Util.log;


public class LuckPermsHandler5 implements PermissionInterface {
    private LuckPerms api;
    private boolean isEnabled = false;

    public LuckPermsHandler5() {
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("LuckPerms");
        if (plugin != null && plugin.isEnabled()) {
            try {
                this.api = Bukkit.getServicesManager().load(LuckPerms.class);
                if (this.api != null) {
                    this.isEnabled = true;
                    log("&aSuccessfully initialized LuckPerms API v5");
                } else {
                    log("&cFailed to load LuckPerms API — service not found");
                    this.isEnabled = false;
                }
            } catch (Exception e) {
                log("&cError loading LuckPerms API v5: " + e.getMessage());
                this.isEnabled = false;
            }
        } else {
            log("&eLuckPerms not found or not enabled");
            this.isEnabled = false;
        }
    }

    @Override
    public String getMainGroup(Player player) {
        if (!isEnabled || api == null) {
            log("&cAttempted to get main group but LuckPerms API is not available");
            return null;
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = api.getUserManager().loadUser(player.getUniqueId()).join();
                if (user != null) {
                    String primaryGroup = user.getPrimaryGroup();
                    if (primaryGroup != null && !primaryGroup.isEmpty()) {
                        log("&7Retrieved main group '" + primaryGroup + "' for player " + player.getName());
                        return primaryGroup;
                    } else {
                        log("&eNo primary group found for player " + player.getName());
                    }
                }
            } catch (Exception e) {
                log("&cError retrieving main group for player " + player.getName() + ": " + e.getMessage());
            }
            return null;
        }).join();
    }

    @Override
    public String getPrefix(UUID uuid) {
        if (!isEnabled || api == null) {
            log("&cAttempted to get prefix but LuckPerms API is not available");
            return "";
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = api.getUserManager().loadUser(uuid).join();
                if (user != null) {
                    String prefix = user.getCachedData().getMetaData().getPrefix();
                    // Проверка на null вместо orElse()
                    if (prefix != null && !prefix.isEmpty()) {
                        log("&7Retrieved prefix '" + prefix + "' for UUID " + uuid);
                        return prefix;
                    } else {
                        log("&eNo prefix found for UUID " + uuid);
                    }
                }
            } catch (Exception e) {
                log("&cError retrieving prefix for UUID " + uuid + ": " + e.getMessage());
            }
            return "";
        }).join();
    }

    @Override
    public String getSufix(UUID uuid) {
        if (!isEnabled || api == null) {
            log("&cAttempted to get suffix but LuckPerms API is not available");
            return "";
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = api.getUserManager().loadUser(uuid).join();
                if (user != null) {
                    String suffix = user.getCachedData().getMetaData().getSuffix();
                    // Проверка на null вместо orElse()
                    if (suffix != null && !suffix.isEmpty()) {
                        log("&7Retrieved suffix '" + suffix + "' for UUID " + uuid);
                        return suffix;
                    } else {
                        log("&eNo suffix found for UUID " + uuid);
                    }
                }
            } catch (Exception e) {
                log("&cError retrieving suffix for UUID " + uuid + ": " + e.getMessage());
            }
            return "";
        }).join();
    }

    @Override
    public String getNameColor(Player player) {
        if (!isEnabled || api == null) {
            log("&cAttempted to get name color but LuckPerms API is not available");
            return null;
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = api.getUserManager().loadUser(player.getUniqueId()).join();
                if (user != null) {
                    String color = user.getCachedData().getMetaData().getMetaValue("name-color");
                    // Проверка на null вместо orElse()
                    if (color != null && !color.isEmpty()) {
                        log("&7Retrieved name color '" + color + "' for player " + player.getName());
                        return color;
                    } else {
                        log("&eNo name color found for player " + player.getName());
                    }
                }
            } catch (Exception e) {
                log("&cError retrieving name color for player " + player.getName() + ": " + e.getMessage());
            }
            return null;
        }).join();
    }

    @Override
    public String getPrefix(Player player) {
        String prefix = getPrefix(player.getUniqueId());
        log("&7Getting prefix for player " + player.getName() + " (UUID: " + player.getUniqueId() + ") -> '" + prefix + "'");
        return prefix;
    }

    @Override
    public String getSufix(Player player) {
        String suffix = getSufix(player.getUniqueId());
        log("&7Getting suffix for player " + player.getName() + " (UUID: " + player.getUniqueId() + ") -> '" + suffix + "'");
        return suffix;
    }

    @Override
    public String getMainGroup(String var1, UUID var2) {
        return "";
    }
}
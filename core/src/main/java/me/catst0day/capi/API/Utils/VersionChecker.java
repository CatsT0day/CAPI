package me.catst0day.capi.API.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

import static me.catst0day.capi.API.Utils.Util.log;

public class VersionChecker {
    private final int resourceId;
    private final Plugin plugin;
    private final PluginDescriptionFile description;

    public VersionChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
        this.description = plugin.getDescription();
    }

    public Integer convertVersion(String version) {
        version = version.replaceAll("[^\\d.]", "");

        if (version.contains(".")) {
            String numericParts = "";
            String[] parts = version.split("\\.");

            for (String part : parts) {
                if (part.length() == 1) {
                    part = "0" + part;
                }
                numericParts += part;
            }

            try {
                return Integer.parseInt(numericParts);
            } catch (NumberFormatException e) {
                log("Failed to convert version: " + version);
                return 0;
            }
        } else {
            try {
                return Integer.parseInt(version);
            } catch (NumberFormatException e) {
                log("Failed to convert version: " + version);
                return 0;
            }
        }
    }

    public String deconvertVersion(Integer numericVersion) {
        if (numericVersion == null) return "unknown";

        String versionStr = String.valueOf(numericVersion);
        StringBuilder result = new StringBuilder();

        for (int i = versionStr.length(); i > 0; i -= 2) {
            int start = Math.max(0, i - 2);
            String part = versionStr.substring(start, i);
            part = part.replaceFirst("^0+(?!$)", "");

            if (result.length() > 0) {
                result.insert(0, "." + part);
            } else {
                result.insert(0, part);
            }
        }

        return result.toString();
    }

    public void checkForUpdates() {
        CompletableFuture.runAsync(() -> {
            String currentVersion = description.getVersion();
            String latestVersion = getOfficialVersion(resourceId);

            if (latestVersion != null) {
                Integer currentNumeric = convertVersion(currentVersion);
                Integer latestNumeric = convertVersion(latestVersion);

                if (latestNumeric > currentNumeric) {
                    sendUpdateNotifications(currentVersion, latestVersion);
                } else {
                    log("Plugin is up to date. Current version: " + currentVersion);
                }
            } else {
                log("Could not retrieve latest version information");
            }
        });
    }

    public String getOfficialVersion() {
        return getOfficialVersion(resourceId);
    }

    public String getOfficialVersion(int resourceId) {
        BufferedReader reader = null;

        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            URLConnection connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String response = reader.readLine();

            if (response != null && response.length() <= 11) {
                return response;
            }

        } catch (IOException e) {
            log("Error checking for updates: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log("Error closing connection: " + e.getMessage());
                }
            }
        }

        return null;
    }

    private void sendUpdateNotifications(String currentVersion, String latestVersion) {
        String message = ChatColor.GRAY + "_________________/ " +
                description.getName() + " \\_________________\n" +
                ChatColor.GRAY + "| " +
                ChatColor.YELLOW + latestVersion + ChatColor.GRAY +
                " is now available! Your version: " +
                ChatColor.RED + currentVersion + "\n";

        if (description.getWebsite() != null) {
            message += ChatColor.GRAY + "| Website: " +
                    ChatColor.AQUA + description.getWebsite() + "\n";
        }

        message += ChatColor.GRAY + "----------------------------------------";

        log("Update available: " + latestVersion + " (current: " + currentVersion + ")");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("capi.update.notify")) {
                player.sendMessage(message);
            }
        }
    }
}
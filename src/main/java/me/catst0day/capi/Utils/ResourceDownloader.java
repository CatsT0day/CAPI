package me.catst0day.capi.Utils;

import me.catst0day.capi.Schedulers.CAPIMainScheduler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Set;

import static me.catst0day.capi.Utils.Util.log;

public class ResourceDownloader {
    private final Plugin plugin;
    private static final Set<String> valid = Set.of("yml", "txt", "jar");

    public ResourceDownloader(Plugin plugin) {
        this.plugin = plugin;
    }

    public void downloadFromResources(String resourcePath, String targetPath, boolean inform) {
        String extension = getFileExtension(resourcePath);

        if (!valid.contains(extension)) {
            if (inform) log("Unsupported file extension: " + extension);
            return;
        }

        CAPIMainScheduler.runTask(plugin, () -> {
            try {
                File targetFile = new File(plugin.getDataFolder(), resourcePath);
                plugin.saveResource(resourcePath, false);

                if (targetFile.exists()) {
                    if (inform) log("Successfully loaded resource: " + resourcePath);
                    afterDownload();
                } else {
                    log("Failed to load resource: " + resourcePath);
                    failedDownload();
                }
            } catch (Exception e) {
                if (inform) log("Error loading resource '" + resourcePath + "': " + e.getMessage());
                failedDownload();
            }
        });
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1).toLowerCase();
    }

    public void afterDownload() {}
    public void failedDownload() {}
}
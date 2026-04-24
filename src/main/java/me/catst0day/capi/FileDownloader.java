package me.catst0day.capi;

import me.catst0day.capi.Schedulers.CAPIMainScheduler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.catst0day.capi.Utils.Util.log;

public class FileDownloader {
    private final Plugin plugin;
    private static final List<String> VALID_TYPES = new ArrayList<>(Arrays.asList("yml", "txt", "jar"));


    public FileDownloader(Plugin plugin) {
        this.plugin = plugin;
    }


    public void downloadFromResources(String resourcePath, String targetPath, boolean inform) {
        String fileExtension = getFileExtension(resourcePath);
        if (!VALID_TYPES.contains(fileExtension)) {
            if (inform) log("Unsupported file extension: " + fileExtension);
            return;
        }

        CAPIMainScheduler.runTask(plugin, () -> {
            try {
                File targetFile = new File(targetPath);
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }

                plugin.saveResource(resourcePath, false);

                if (targetFile.exists()) {
                    log("Successfully loaded resource: " + resourcePath + " -> " + targetPath);
                    afterDownload();
                } else {
                    log("Failed to load resource: " + resourcePath);
                    failedDownload();
                }
            } catch (Exception e) {
                if (inform) {
                    log("Error loading resource '" + resourcePath + "': " + e.getMessage());
                }
                failedDownload();
            }
        });
    }

    private String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.", -1);
        if (parts.length == 0) return "";
        return parts[parts.length - 1].toLowerCase();
    }

    public void afterDownload() {
    }

    public void failedDownload() {
    }
}
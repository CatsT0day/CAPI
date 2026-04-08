package me.catst0day.capi;

import me.catst0day.capi.API.Shedulers.CAPIMainScheduler;
import me.catst0day.capi.API.Utils.Util;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDownloader {
    private final Plugin plugin;
    private static final List<String> VALID_TYPES = new ArrayList<>(Arrays.asList("yml", "txt", "jar"));


    public FileDownloader(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Загружает файл из ресурсов плагина.
     *
     * @param resourcePath путь к файлу в ресурсах плагина (например, "Translations/EN.yml")
     * @param targetPath путь для сохранения файла на диске
     * @param inform выводить ли сообщения в консоль?
     */
    public void downloadFromResources(String resourcePath, String targetPath, boolean inform) {
        // Проверка расширения файла
        String fileExtension = getFileExtension(resourcePath);
        if (!VALID_TYPES.contains(fileExtension)) {
            if (inform) Util.log("Unsupported file extension: " + fileExtension);
            return;
        }

        CAPIMainScheduler.runTask(plugin, () -> {
            try {
                // Создаём директорию, если её нет
                File targetFile = new File(targetPath);
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }

                // Загружаем файл из ресурсов плагина
                plugin.saveResource(resourcePath, false);

                // Проверяем, что файл действительно создан
                if (targetFile.exists()) {
                    Util.log("Successfully loaded resource: " + resourcePath + " -> " + targetPath);
                    afterDownload();
                } else {
                    Util.log("Failed to load resource: " + resourcePath);
                    failedDownload();
                }
            } catch (Exception e) {
                if (inform) {
                    Util.log("Error loading resource '" + resourcePath + "': " + e.getMessage());
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
        // Реализация по умолчанию — ничего не делает
    }

    public void failedDownload() {
        // Реализация по умолчанию — ничего не делает
    }
}
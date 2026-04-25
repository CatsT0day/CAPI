package me.catst0day.capi.Utils;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Schedulers.CAPIMainScheduler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import static me.catst0day.capi.Utils.Util.log;

public class FileDownloader {

    public void download(String urlString, String destinationPath) {
        CAPIMainScheduler.runTaskAsync(CAPI.getInstance(), () -> {
            File targetFile = new File(destinationPath);

            try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }

                CAPIMainScheduler.runTask(CAPI.getInstance(), this::afterDownload);

            } catch (Exception e) {
                log("&4Failed to download file from: " + urlString);
                log("Target destination: " + targetFile.getAbsolutePath());
                log("Error: " + e.getMessage());

                CAPIMainScheduler.runTask(CAPI.getInstance(), this::failedDownload);
            }
        });
    }

    public void afterDownload() {}
    public void failedDownload() {}
}
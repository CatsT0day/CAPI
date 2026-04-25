//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package me.catst0day.capi.Utils;

import me.catst0day.capi.CAPI;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.catst0day.capi.Schedulers.CAPIMainScheduler;

import static me.catst0day.capi.Utils.Util.log;

public class FileDownloader {
    static final List<String> VALID_TYPES = new ArrayList<>(Arrays.asList("dat", "yml", "txt", "jar"));

    public FileDownloader() {
    }

    public void download(String var1, String var2) {

        CAPIMainScheduler.runTaskAsync(CAPI.getInstance(), () -> {
            try {
                BufferedInputStream var3 = new BufferedInputStream((new URL(var1)).openStream());
                FileOutputStream var8 = new FileOutputStream(var2);
                byte[] var5 = new byte[1024];
                int var6 = 0;

                while((var6 = var3.read(var5, 0, 1024)) != -1) {
                    var8.write(var5, 0, var6);
                }

                var8.close();
                var3.close();
                CAPIMainScheduler.runTask(CAPI.getInstance(), this::afterDownload);
            } catch (Throwable var7) {
                File var4 = new File(var2);
                log("Failed to download " + var1 + " file into " + var4.getParent() + File.separator + " folder ");
                log("Download it manually: ");
                this.failedDownload();
            }

        });
    }

    public void afterDownload() {
    }

    public void failedDownload() {
    }
}

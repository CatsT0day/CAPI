package me.catst0day.capi.Utils;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Chat.CAPICol;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Util {
    private static final String PREFIX = CAPICol.YELLOW + "[CAPI] ";
    private static final String PURPLE = "#3B1757";

    public static void printStartupBanner(JavaPlugin plugin) {
        String version = (plugin != null) ? plugin.getDescription().getVersion() : "Unknown";
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        String[] banner = {
                "",
                "&b ██████  █████  ████████  █████  ██████  ██",
                "&b██      ██   ██    ██    ██   ██ ██   ██ ██",
                "&b██      ███████    ██    ███████ ██████  ██",
                "&b██      ██   ██    ██    ██   ██ ██      ██",
                "&b ██████ ██   ██    ██    ██   ██ ██      ██",
                "",
                "&b╔══════════════════════════════════════╗",
                "&b║                                      ║",
                "&b║  CatAPI &bv" + version + "  is loading...     ║",
                "&b║                                      ║",
                "&b╚══════════════════════════════════════╝",
                "",
                "&bVersion: &f" + version,
                "&bAuthor: &fCatsT0day (aka WhiteCat)",
                ""
        };

        for (String line : banner) {
            console.sendMessage(PREFIX + CAPICol.translate(line));
        }
    }

    public static void log(String text) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        boolean monochrome = CAPI.getInstance().getConfig().getBoolean("monochromeMode", false);

        if (monochrome) {
            console.sendMessage(PREFIX + CAPICol.stripColor(CAPICol.translate(text)));
        } else {
            console.sendMessage(PREFIX + CAPICol.translate(PURPLE + text));
        }
    }

    public static void loadWithMessage(Object count, String msg, long time) {
        log("Loaded (&f" + count + "&" + PURPLE + ") &7" + msg + " " + PURPLE + "into memory. &6Took &e" + time + "&6ms");
    }

    public static String color(String message) {
        return CAPICol.translate(message);
    }
}
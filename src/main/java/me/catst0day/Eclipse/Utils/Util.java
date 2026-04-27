package me.catst0day.Eclipse.Utils;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Util {
    private static final String PREFIX = HexUtil.YELLOW + "[CAPI] ";
    private static final String PURPLE = "#3B1757";

    public static void printStartupBanner(JavaPlugin plugin) {
        String version = (plugin != null) ? plugin.getDescription().getVersion() : "Unknown";
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        String[] banner = {
                "",
              "░██████████            ░██ ░██",
              "░██                    ░██",
              "░██          ░███████  ░██ ░██░████████   ░███████   ░███████",
              "░█████████  ░██    ░██ ░██ ░██░██    ░██ ░██        ░██    ░██",
              "░██         ░██        ░██ ░██░██    ░██  ░███████  ░█████████",
               "██         ░██    ░██ ░██ ░██░███   ░██        ░██ ░██",
              "░██████████  ░███████  ░██ ░██░██░█████   ░███████   ░███████",
                                            "░██",
                                            "░██",
        "",
                "&b╔══════════════════════════════════════╗",
                "&b║                                      ║",
                "&b║  Eclipse &bv" + version + "  succefully loaded     ║",
                "&b║                                      ║",
                "&b╚══════════════════════════════════════╝",
                "",
                "&bVersion: &f" + version,
                "&bAuthor: &fCatsT0day (aka WhiteCat)",
                ""
        };

        for (String line : banner) {
            console.sendMessage(PREFIX + HexUtil.translateHexAndAlternateColorCodes(line));
        }
    }

    public static void log(String text) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        boolean monochrome = Eclipse.getI().getConfig().getBoolean("monochromeMode", false);

        if (monochrome) {
            console.sendMessage(PREFIX + HexUtil.stripColor(HexUtil.translateHexAndAlternateColorCodes(text)));
        } else {
            console.sendMessage(PREFIX + HexUtil.translateHexAndAlternateColorCodes(PURPLE + text));
        }
    }

    public static void loadWithMessage(Object count, String msg, long time) {
        log("Loaded (&f" + count + "&" + PURPLE + ") &7" + msg + " " + PURPLE + "into cache. &6Took &e" + time + "&6ms");
    }

    public static String color(String message) {
        return HexUtil.translateHexAndAlternateColorCodes(message);
    }
}
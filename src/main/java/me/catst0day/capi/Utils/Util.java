
package me.catst0day.capi.Utils;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Chat.CAPIChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class Util {
    private static final String PREFIX = CAPIChatColor.DARK_PURPLE + "[CAPI] " + CAPIChatColor.AQUA;

    public static void printStartupBanner(JavaPlugin plugin) {
        String pluginVersion = plugin != null && plugin.getDescription() != null
                ? plugin.getDescription().getVersion()
                : "Unknown";

        String[] banner = {
                "",
                CAPIChatColor.AQUA + " ██████  █████  ████████  █████  ██████  ██" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "██      ██   ██    ██    ██   ██ ██   ██ ██" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "██      ███████    ██    ███████ ██████  ██" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "██      ██   ██    ██    ██   ██ ██      ██" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + " ██████ ██   ██    ██    ██   ██ ██      ██" + CAPIChatColor.AQUA,
                "",
                CAPIChatColor.AQUA + "╔" + CAPIChatColor.AQUA + "══════════════════════════════════════" + CAPIChatColor.AQUA + "╗" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "║" + CAPIChatColor.AQUA + "                                      " + CAPIChatColor.AQUA + "║" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "║" + CAPIChatColor.AQUA + "  CatAPI " + CAPIChatColor.AQUA + "v" + pluginVersion + "  is loading...     ║" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "║" + CAPIChatColor.AQUA + "                                      " + CAPIChatColor.AQUA + "║" + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "╚" + CAPIChatColor.AQUA + "══════════════════════════════════════" + CAPIChatColor.AQUA + "╝" + CAPIChatColor.AQUA,
                "",
                CAPIChatColor.AQUA + "Version: " + CAPIChatColor.WHITE + pluginVersion + CAPIChatColor.AQUA,
                CAPIChatColor.AQUA + "Author: " + CAPIChatColor.WHITE + "CatsT0day (aka WhiteCat)" + CAPIChatColor.AQUA,
                ""
        };

        for (String line : banner) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(PREFIX + line);
        }
    }

    public static void log(String text) {
        ConsoleCommandSender var2 = Bukkit.getConsoleSender();
        if (CAPI.getInstance().getConfig().getBoolean("monochromeMode")) {
            text = CAPIChatColor.stripColor(text);
            if (CAPI.fullyLoaded) {
                var2.sendMessage(CAPIChatColor.stripColor(PREFIX) + (text == null ? null : text));
            } else {
                var2.sendMessage(text);
            }
        } else {
            if (CAPI.fullyLoaded) {
                var2.sendMessage(PREFIX + (text == null ? null : CAPIChatColor.translate(text)));
            } else {
                var2.sendMessage(CAPIChatColor.translate("&b" + text));
            }
        }
    }

    public static void loadWithMessage(Object objectCount, String stringMsg, Long TimeToLoad) {
        log("&3Loaded (&f" + objectCount + "&3) &7" + stringMsg + " &3into memory. &6Took &e" + TimeToLoad + "&6ms");
    }

    public static String color(String message) {
        return CAPIChatColor.translate(message);
    }
}
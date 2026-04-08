package me.catst0day.capi.API.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.ChatColor.*;

public class Util {
    private static final String PREFIX = GOLD + "[CAPI] " + DARK_AQUA;

    public static void printStartupBanner(JavaPlugin plugin) {
        String pluginVersion = plugin != null && plugin.getDescription() != null
                ? plugin.getDescription().getVersion()
                : "Unknown";

        String[] banner = {
                "",
                AQUA + " ██████  █████  ████████  █████  ██████  ██" + AQUA,
                AQUA + "██      ██   ██    ██    ██   ██ ██   ██ ██" + AQUA,
                AQUA + "██      ███████    ██    ███████ ██████  ██" + AQUA,
                AQUA + "██      ██   ██    ██    ██   ██ ██      ██" + AQUA,
                AQUA + " ██████ ██   ██    ██    ██   ██ ██      ██" + AQUA,
                "",
                DARK_AQUA + "╔" + DARK_AQUA + "══════════════════════════════════════" + DARK_AQUA + "╗" + AQUA,
                DARK_AQUA + "║" + DARK_AQUA + "                                      " + DARK_AQUA + "║" + AQUA,
                DARK_AQUA + "║" + DARK_BLUE + "  CatAPI " + DARK_AQUA + "v" + pluginVersion + "  is loading...     ║" + AQUA,
                DARK_AQUA + "║" + DARK_AQUA + "                                      " + DARK_AQUA + "║" + AQUA,
                DARK_AQUA + "╚" + DARK_AQUA + "══════════════════════════════════════" + DARK_AQUA + "╝" + AQUA,
                "",
                AQUA + "Version: " + WHITE + pluginVersion + AQUA,
                AQUA + "Author: " + WHITE + "CatsT0day (aka WhiteCat)" + AQUA,
                ""
        };

        for (String line : banner) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(PREFIX + line);
        }
    }

    public static void log(String message) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(PREFIX + message);
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
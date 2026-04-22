package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

@CAPICommandAnnotation(
        name = "pweather",
        aliases = {"pw"},
        permission = CAPIPerm.PWEATHER,
        requirePlayer = false,
        cooldownSeconds = 0,
        description = "Controls player weather"
)
public class PWeather extends CAPICommandTemplate {

    private String noPermissionMsg;
    private String playerOnlyMsg;
    private String playerNotFoundMsg;
    private String currentWeatherMsg;
    private String weatherResetMsg;
    private String yourWeatherResetMsg;
    private String weatherSetMsg;
    private String usageMsg;

    public PWeather(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(List.of("sun", "rain", "reset", "playername", "-s"));
    }

    @Override
    public void get(CAPI plugin) {
        noPermissionMsg = plugin.getMessage("noPermission");
        playerOnlyMsg = plugin.getMessage("playerOnlyCommand");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        currentWeatherMsg = plugin.getMessage("currentWeather");
        weatherResetMsg = plugin.getMessage("weatherReset");
        yourWeatherResetMsg = plugin.getMessage("yourWeatherReset");
        weatherSetMsg = plugin.getMessage("weatherSet");
        usageMsg = plugin.getMessage("usage");
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (requirePlayer && !(sender instanceof Player)) {
            sender.sendMessage(playerOnlyMsg);
            return true;
        }

        WeatherType weather = null;
        boolean silent = false;
        String targetName = null;
        boolean reset = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                if (sender.hasPermission("capi.silent")) {
                    silent = true;
                }
            } else if (arg.equalsIgnoreCase("sun") || arg.equalsIgnoreCase("clear")) {
                weather = WeatherType.CLEAR;
            } else if (arg.equalsIgnoreCase("rain") || arg.equalsIgnoreCase("rainy")) {
                weather = WeatherType.DOWNFALL;
            } else if (arg.equalsIgnoreCase("reset")) {
                reset = true;
            } else {
                targetName = arg;
            }
        }

        Player target = targetName != null
                ? plugin.getServer().getPlayer(targetName)
                : (player != null ? player : null);

        if (target == null) {
            sender.sendMessage(playerNotFoundMsg);
            return true;
        }

        if (weather == null && !reset) {
            WeatherType currentWeather = target.getPlayerWeather();
            String weatherStr = currentWeather == null
                    ? "default"
                    : (currentWeather == WeatherType.CLEAR
                       ? "sunny"
                       : "rainy");
            sender.sendMessage(currentWeatherMsg
                    .replace("%weather%", weatherStr)
                    .replace("%player%", target.getName()));
            return true;
        }

        if (reset) {
            target.resetPlayerWeather();
            if (!silent) {
                sender.sendMessage(weatherResetMsg.replace("%player%", target.getName()));
                if (!target.equals(sender)) {
                    target.sendMessage(yourWeatherResetMsg);
                }
            }
            return true;
        }

        target.setPlayerWeather(weather);
        if (!silent) {
            sender.sendMessage(weatherSetMsg
                    .replace("%weather%", weather == WeatherType.CLEAR ? "sunny" : "rainy")
                    .replace("%player%", target.getName()));
            if (!target.equals(sender)) {
                target.sendMessage(plugin.getMessage("yourWeatherSet")
                        .replace("%weather%", weather == WeatherType.CLEAR ? "sunny" : "rainy"));
            }
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return null;
    }
}

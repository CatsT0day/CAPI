package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class PWeather extends CAPICommandTemplate {

    public PWeather(CAPI plugin) {
        super(plugin, "pweather", List.of(), CAPIPerm.PWEATHER, true, 0, "Set personal player weather");
        setTabCompleteArguments(List.of("sun", "rain", "reset"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        WeatherType weather = null;
        boolean silent = false;
        boolean reset = false;
        String targetName = null;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s") && sender.hasPermission("capi.silent")) silent = true;
            else if (arg.equalsIgnoreCase("sun") || arg.equalsIgnoreCase("clear")) weather = WeatherType.CLEAR;
            else if (arg.equalsIgnoreCase("rain")) weather = WeatherType.DOWNFALL;
            else if (arg.equalsIgnoreCase("reset")) reset = true;
            else targetName = arg;
        }

        Player target = targetName != null ? plugin.getServer().getPlayer(targetName) : player;
        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }

        if (reset) {
            target.resetPlayerWeather();
            if (!silent) sender.sendMessage(plugin.getMessage("weatherReset").replace("%player%", target.getName()));
            return true;
        }

        if (weather != null) {
            target.setPlayerWeather(weather);
            if (!silent) sender.sendMessage(plugin.getMessage("weatherSet")
                    .replace("%weather%", weather == WeatherType.CLEAR ? "sunny" : "rainy")
                    .replace("%player%", target.getName()));
        } else {
            sender.sendMessage(plugin.getMessage("currentWeather")
                    .replace("%weather%", target.getPlayerWeather() == WeatherType.CLEAR ? "sunny" : "rainy")
                    .replace("%player%", target.getName()));
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}
package me.catst0day.capi.Commands.commandAPI;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public interface CAPICommandInterface {
    boolean perform(CommandSender sender, Player player, String[] args);
    boolean perform(Player player, String[] args);
    List<String> tabCompl(Player player, String[] args);
    void setTabCompleteArguments(List<String> arguments);
    String getName();
    String getDescription();
    List<String> getAliases();
}
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Bossbar.CAPIBarStyle;
import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Bossbar.CAPIBossBar;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import me.catst0day.Eclipse.Bossbar.CAPIBarColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class Vanish extends CommandTemplate {

    private final HashSet<UUID> vanishedPlayers = new HashSet<>();
    private final HashMap<UUID, CAPIBossBar> bossBars = new HashMap<>();

    public Vanish(Eclipse plugin) {
        super(plugin, "vanish", List.of("v"), CAPIPermissions.VANISH, false, 0, "Toggle invisibility");
        setTabCompleteArguments(Arrays.asList("on", "off", "list"));
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        Player target = null;
        Boolean vanishState = null;
        boolean silent = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) silent = true;
            else if (arg.equalsIgnoreCase("on")) vanishState = true;
            else if (arg.equalsIgnoreCase("off")) vanishState = false;
            else if (arg.equalsIgnoreCase("list")) {
                showVanishedList(sender);
                return true;
            } else {
                target = Bukkit.getPlayer(arg);
            }
        }

        if (target == null) {
            if (player == null) {
                sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
                return true;
            }
            target = player;
        }

        if (vanishState == null) {
            if (vanishedPlayers.contains(target.getUniqueId())) disableVanish(target, sender, silent);
            else enableVanish(target, sender, silent);
        } else {
            if (vanishState) enableVanish(target, sender, silent);
            else disableVanish(target, sender, silent);
        }
        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    private void enableVanish(Player player, CommandSender sender, boolean silent) {
        if (vanishedPlayers.add(player.getUniqueId())) {
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(plugin, player));

            CAPIBossBar bar = new CAPIBossBar(plugin, player, "vanish_" + player.getUniqueId());
            bar.setTitleOfBar("&cVanish Active");
            bar.setColor(CAPIBarColor.WHITE);
            bar.setStyle(CAPIBarStyle.SEGMENTED_20);
            bar.setMakeVisible(true);
            bossBars.put(player.getUniqueId(), bar);

            if (!silent) {
                if (sender == player) player.sendMessage(plugin.getMessage("vanishEnabled"));
                else sender.sendMessage(plugin.getMessage("vanishSuccess").replace("%player%", player.getName()));
            }
        }
    }

    private void disableVanish(Player player, CommandSender sender, boolean silent) {
        if (vanishedPlayers.remove(player.getUniqueId())) {
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(plugin, player));
            if (bossBars.containsKey(player.getUniqueId())) bossBars.remove(player.getUniqueId()).remove();

            if (!silent) {
                if (sender == player) player.sendMessage(plugin.getMessage("vanishDisabled"));
                else sender.sendMessage(plugin.getMessage("vanishDisabledTarget").replace("%player%", player.getName()));
            }
        }
    }

    private void showVanishedList(CommandSender sender) {
        if (vanishedPlayers.isEmpty()) {
            sender.sendMessage(plugin.getMessage("noVanishedPlayers"));
            return;
        }
        sender.sendMessage(ChatColor.DARK_GRAY + "--- " + plugin.getMessage("vanishedPlayersTitle") + " ---");
        vanishedPlayers.forEach(id -> {
            Player p = Bukkit.getPlayer(id);
            if (p != null) sender.sendMessage(ChatColor.GOLD + p.getName());
        });
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String s : Arrays.asList("on", "off", "list", "-s")) if (s.startsWith(prefix)) completions.add(s);
        }
        return completions;
    }
}

package me.catst0day.capi.Commands;

import me.catst0day.capi.Bossbar.CAPIBarStyle;
import me.catst0day.capi.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Bossbar.CAPIBossBarInfo;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import me.catst0day.capi.Bossbar.CAPIBarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.HashMap;

public class Vanish extends CommandTemplate {

    private final HashSet<UUID> vanishedPlayers = new HashSet<>();
    private final HashMap<UUID, CAPIBossBarInfo> bossBars = new HashMap<>();
    private final CAPIPermissionManager permissionManager;

    public Vanish(CAPI plugin) {
        super(
                plugin,
                "vanish",
                Arrays.asList("v"),
                CAPIPermissionManager.CAPIPerm.VANISH,
                false,
                0,
                "enter vanish (cool invisibility)"
        );
        setTabCompleteArguments(Arrays.asList("on", "off", "list", "-s"));
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return permissionManager.hasPermission(player, CAPIPermissionManager.CAPIPerm.VANISH);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        Player target = null;
        Boolean vanishState = null;
        boolean silent = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-s")) {
                silent = true;
            } else if (arg.equalsIgnoreCase("on")) {
                vanishState = true;
            } else if (arg.equalsIgnoreCase("off")) {
                vanishState = false;
            } else if (arg.equalsIgnoreCase("list")) {
                showVanishedList(sender);
                return true;
            } else {
                Player found = Bukkit.getPlayer(arg);
                if (found != null) {
                    target = found;
                } else {
                    sender.sendMessage(plugin.getMessage("playerNotFound"));
                    return true;
                }
            }
        }

        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
                return true;
            }
            target = (Player) sender;
        }

        if (vanishState == null) {
            toggleVanish(target, sender, silent);
        } else {
            if (vanishState) {
                enableVanish(target, sender, silent);
            } else {
                disableVanish(target, sender, silent);
            }
        }
        return true;
    }

    private void toggleVanish(Player target, CommandSender sender, boolean silent) {
        if (isVanished(target)) {
            disableVanish(target, sender, silent);
        } else {
            enableVanish(target, sender, silent);
        }
    }

    private void enableVanish(Player player, CommandSender sender, boolean silent) {
        if (!isVanished(player)) {
            vanishedPlayers.add(player.getUniqueId());

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.hidePlayer(plugin, player);
            }

            CAPIBossBarInfo bossBar = new CAPIBossBarInfo(plugin, player, "vanish_" + player.getUniqueId().toString());
            bossBar.setTitleOfBar("&cИгрок " + player.getName() + " &4невидим");
            bossBar.setColor(CAPIBarColor.WHITE);
            bossBar.setStyle(CAPIBarStyle.SEGMENTED_20);
            bossBar.setPercentage(1.0);
            bossBar.setMakeVisible(true);

            bossBars.put(player.getUniqueId(), bossBar);

            if (!silent) {
                if (sender == player) {
                    player.sendMessage(plugin.getMessage("vanishEnabled"));
                } else {
                    sender.sendMessage(plugin.getMessage("vanishSuccess")
                            .replace("%player%", player.getName()));
                    player.sendMessage(plugin.getMessage("vanishAdminEnabled"));
                }
            }
        }
    }

    private void disableVanish(Player player, CommandSender sender, boolean silent) {
        if (isVanished(player)) {
            vanishedPlayers.remove(player.getUniqueId());

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }

            CAPIBossBarInfo bossBar = bossBars.remove(player.getUniqueId());
            if (bossBar != null) {
                bossBar.remove();
            }

            if (!silent) {
                if (sender == player) {
                    player.sendMessage(plugin.getMessage("vanishDisabled"));
                } else {
                    sender.sendMessage(plugin.getMessage("vanishDisabledTarget")
                            .replace("%player%", player.getName()));
                    player.sendMessage(plugin.getMessage("vanishAdminDisabled"));
                }
            }
        }
    }

    private boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    private void showVanishedList(CommandSender sender) {
        if (vanishedPlayers.isEmpty()) {
            sender.sendMessage(plugin.getMessage("noVanishedPlayers"));
            return;
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "--- " + plugin.getMessage("vanishedPlayersTitle") + " ---");
        int count = 1;
        for (UUID uuid : vanishedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                String status = player.isOnline() ? plugin.getMessage("online") : plugin.getMessage("offline");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        player.getName() + " &6(" + status + ")"));
            }
        }
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return filterByInput(args[0], Arrays.asList("on", "off", "list"));
        } else if (args.length == 2) {
            return getOnlinePlayerNames(args[1]);
        }
        return new ArrayList<>();
    }

    public void cleanupBossBars() {
        for (CAPIBossBarInfo bar : bossBars.values()) {
            bar.remove();
        }
        bossBars.clear();
    }
}
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.Bossbar.CAPIBarStyle;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.Bossbar.CAPIBossBarInfo;
import me.catst0day.capi.Commands.commandAPI.CAPICommandAnnotation;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;
import me.catst0day.capi.Bossbar.CAPIBarColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

@CAPICommandAnnotation(
        name = "vanish",
        aliases = {"v"},
        permission = CAPIPerm.VANISH,
        requirePlayer = false,
        cooldownSeconds = 0,
        description = "Enter vanish mode (cool invisibility)"
)
public class Vanish extends CAPICommandTemplate {

    private final HashSet<UUID> vanishedPlayers = new HashSet<>();
    private final HashMap<UUID, CAPIBossBarInfo> bossBars = new HashMap<>();

    private String vanishEnabledMsg;
    private String vanishDisabledMsg;
    private String vanishSuccessMsg;
    private String vanishAdminEnabledMsg;
    private String vanishAdminDisabledMsg;
    private String vanishDisabledTargetMsg;
    private String noVanishedPlayersMsg;
    private String vanishedPlayersTitleMsg;
    private String onlineMsg;
    private String offlineMsg;
    private String playerNotFoundMsg;
    private String noPermissionMsg;
    private String playerOnlyCommandMsg;

    public Vanish(CAPI plugin) {
        super(plugin);
        setTabCompleteArguments(Arrays.asList("on", "off", "list", "-s"));
    }

    @Override
    public void get(CAPI plugin) {
        vanishEnabledMsg = plugin.getMessage("vanishEnabled");
        vanishDisabledMsg = plugin.getMessage("vanishDisabled");
        vanishSuccessMsg = plugin.getMessage("vanishSuccess");
        vanishAdminEnabledMsg = plugin.getMessage("vanishAdminEnabled");
        vanishAdminDisabledMsg = plugin.getMessage("vanishAdminDisabled");
        vanishDisabledTargetMsg = plugin.getMessage("vanishDisabledTarget");
        noVanishedPlayersMsg = plugin.getMessage("noVanishedPlayers");
        vanishedPlayersTitleMsg = plugin.getMessage("vanishedPlayersTitle");
        onlineMsg = plugin.getMessage("online");
        offlineMsg = plugin.getMessage("offline");
        playerNotFoundMsg = plugin.getMessage("playerNotFound");
        noPermissionMsg = plugin.getMessage("noPermission");
        playerOnlyCommandMsg = plugin.getMessage("playerOnlyCommand");
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        return plugin.getPermissionManager().hasPermission(player, CAPIPerm.VANISH);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(noPermissionMsg);
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
                    sender.sendMessage(playerNotFoundMsg);
                    return true;
                }
            }
        }

        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(playerOnlyCommandMsg);
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

    @Override
    protected boolean perform(Player player, String[] args) {
        return false;
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
                    player.sendMessage(vanishEnabledMsg);
                } else {
                    sender.sendMessage(vanishSuccessMsg.replace("%player%", player.getName()));
                    player.sendMessage(vanishAdminEnabledMsg);
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
                    player.sendMessage(vanishDisabledMsg);
                } else {
                    sender.sendMessage(vanishDisabledTargetMsg.replace("%player%", player.getName()));
                    player.sendMessage(vanishAdminDisabledMsg);
                }
            }
        }
    }

    private boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    private void showVanishedList(CommandSender sender) {
        if (vanishedPlayers.isEmpty()) {
            sender.sendMessage(noVanishedPlayersMsg);
            return;
        }

        sender.sendMessage(ChatColor.DARK_GRAY + "--- " + vanishedPlayersTitleMsg + " ---");
        int count = 1;
        for (UUID uuid : vanishedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                String status = player.isOnline() ? onlineMsg : offlineMsg;
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        player.getName() + " &6(" + status + ")"));
            }
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String option : Arrays.asList("on", "off", "list", "-s")) {
                if (option.startsWith(prefix)) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String name = onlinePlayer.getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    completions.add(name);
                }
            }
        }
        return completions;
    }
}
package me.catst0day.capi.Commands.commandAPI;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.EventListeners.CAPIOnCommandEvent;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import static me.catst0day.capi.Utils.Util.log;

public abstract class CAPICommandTemplate implements CommandExecutor, TabCompleter {
    private static final HashMap<String, CommandExecutor> registeredCommands = new HashMap<>();
    protected final CAPI plugin;
    protected final String name;
    protected final List<String> aliases;
    protected final CAPIPermissionManager.CAPIPerm perm;
    protected final boolean requirePlayer;
    protected List<String> tabCompleteArguments = new ArrayList<>();
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final long cooldownSeconds;
    protected final String description;



    protected CAPICommandTemplate(CAPI plugin, String name, List<String> aliases,
                                  CAPIPermissionManager.CAPIPerm perm, boolean requirePlayer,
                                  long cooldownSeconds, String description) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases;
        this.perm = perm;
        this.requirePlayer = requirePlayer;
        this.cooldownSeconds = cooldownSeconds;
        this.description = description;

        registeredCommands.put(name, this);
        if (aliases != null) {
            for (String alias : aliases) {
                registeredCommands.put(alias, this);
            }
        }
    }



    public boolean perform(CAPI plugin, CommandSender sender, String[] args) {
        return onCommand(sender, args);
    }

    private boolean onCommand(CommandSender sender, String[] args) {
        if (sender == null) {
            log("Command executed with null sender - ignoring");
            return false;
        }

        CAPIOnCommandEvent event = new CAPIOnCommandEvent(sender, this.name, args);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            Boolean result = event.getCommandResult();
            return result != null ? result : false;
        }

        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (requirePlayer && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
            return true;
        }

        Player player = requirePlayer ? (Player) sender : null;

        if (requirePlayer && player != null) {
            if (!player.hasPermission("catapi.cooldown.bypass")) {
                if (isOnCooldown(player)) {
                    return true;
                }
                startCooldown(player);
            }
        }

        try {
            boolean result = executeWithPlayer(player, args);
            if (result) {
                return true;
            }
            return perform(sender, player, args);
        } catch (Exception e) {
            if (sender != null) sender.sendMessage(plugin.getMessage("commandError"));
            log("&4Error executing command " + name + "&7( &6" + e.getMessage() + "&7)");
            e.printStackTrace();
            return false;
        }
    }
    protected List<String> getTabCompleteArguments() {
        return tabCompleteArguments;
    }

    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        return player.hasPermission(perm.getPermission(args));
    }

    private boolean executeWithPlayer(Player player, String[] args) {
        try {
            return perform(player, args);
        } catch (AbstractMethodError | UnsupportedOperationException e) {
            return false;
        }
    }

    private boolean isOnCooldown(Player player) {
        Long lastUse = cooldowns.get(player.getName());
        if (lastUse == null) return false;

        long currentTime = System.currentTimeMillis();
        long timeLeft = (lastUse + cooldownSeconds * 1000) - currentTime;

        if (timeLeft > 0) {
            long secondsLeft = (timeLeft + 999) / 1000;
            player.sendMessage(String.format(plugin.getMessage("cooldownMessage"), secondsLeft));
            return true;
        }
        return false;
    }

    private void startCooldown(Player player) {
        cooldowns.put(player.getName(), System.currentTimeMillis());
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public static HashMap<String, CommandExecutor> getRegisteredCommands() {
        return registeredCommands;
    }


    public List<String> getAliases() {
        return aliases != null ? aliases : new ArrayList<>();
    }

    public void setTabCompleteArguments(List<String> arguments) {
        this.tabCompleteArguments = arguments;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return perform(plugin, sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (requirePlayer && !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;

        if (!tabCompleteArguments.isEmpty() && args.length == 1) {
            return tabCompleteArguments.stream()
                    .filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<String> result = tabCompl(player, args);
        return result != null ? result : Collections.emptyList();
    }

    protected abstract boolean perform(CommandSender sender, Player player, String[] args);

    protected abstract boolean perform(Player player, String[] args);

    protected abstract List<String> tabCompl(Player player, String[] args);
}

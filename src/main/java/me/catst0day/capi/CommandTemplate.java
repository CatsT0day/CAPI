package me.catst0day.capi;

import me.catst0day.capi.EventListeners.CAPIOnCommandEvent;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Utils.CommandRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static me.catst0day.capi.Utils.Util.log;

public abstract class CommandTemplate implements CommandExecutor, TabCompleter {
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

    protected CommandTemplate(CAPI plugin, String name, List<String> aliases, CAPIPermissionManager.CAPIPerm perm, boolean requirePlayer, long cooldownSeconds, String description) {
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
        CAPIOnCommandEvent event = new CAPIOnCommandEvent(sender, this.name, args);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return event.getCommandResult();
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
            if (result != false) {
                return result;
            }
            return execute(sender, player, args);
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessage("commandError"));
            log("Error executing command " + name + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Универсальная проверка разрешения — может быть переопределена в конкретных командах
     */
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        return player.hasPermission(perm.getPermission());
    }

    private boolean executeWithPlayer(Player player, String[] args) {
        try {
            return execute(player, args);
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

    protected boolean execute(Player player, String[] args) {
        return false;
    }

    protected abstract boolean execute(CommandSender sender, Player player, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (requirePlayer && !(sender instanceof Player)) return null;

        List<String> completions = new ArrayList<>();
        Player player = (Player) sender;

        if (!tabCompleteArguments.isEmpty() && args.length == 1) {
            completions.addAll(tabCompleteArguments.stream()
                    .filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
            return completions;
        }
        return tabComplete(player, args);
    }

    protected List<String> tabComplete(Player player, String[] args) {
        return null;
    }

    protected List<String> getOnlinePlayerNames(String input) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected List<String> filterByInput(String input, List<String> options) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
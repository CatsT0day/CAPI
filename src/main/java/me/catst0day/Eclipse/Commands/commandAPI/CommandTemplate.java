package me.catst0day.Eclipse.Commands.commandAPI;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.EventListeners.CAPIOnCommandEvent;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static me.catst0day.Eclipse.Utils.Util.log;

public abstract class CommandTemplate implements CommandExecutor, TabCompleter {
    private static final HashMap<String, CommandExecutor> registeredCommands = new HashMap<>();

    protected final Eclipse plugin;
    protected final String name;
    protected final List<String> aliases;
    protected final CAPIPermissions perm;
    protected final boolean requirePlayer;
    protected final long cooldownSeconds;
    protected final String description;

    protected List<String> tabCompleteArguments = new ArrayList<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    protected CommandTemplate(Eclipse plugin, String name, List<String> aliases,
                              CAPIPermissions perm, boolean requirePlayer,
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

    public boolean perform(Eclipse plugin, CommandSender sender, String[] args) {
        return onCommand(sender, args);
    }

    private boolean onCommand(CommandSender sender, String[] args) {
        if (sender == null) return false;

        CAPIOnCommandEvent event = new CAPIOnCommandEvent(sender, this.name, args);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            Boolean result = event.getCommandResult();
            return result != null && result;
        }

        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        Player player = (sender instanceof Player p) ? p : null;

        if (requirePlayer && player == null) {
            sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
            return true;
        }

        if (player != null && cooldownSeconds > 0 && !player.hasPermission("catapi.cooldown.bypass")) {
            if (isOnCooldown(player)) return true;
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }

        try {
            if (player != null) {
                if (perform(player, args)) return true;
            }
            return perform(sender, player, args);
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessage("commandError"));
            log("&4Error executing " + name + ": &c" + e.getMessage());
            return false;
        }
    }

    private boolean isOnCooldown(Player player) {
        long lastUse = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        long timeLeft = (lastUse + (cooldownSeconds * 1000)) - System.currentTimeMillis();

        if (timeLeft > 0) {
            long secondsLeft = (timeLeft / 1000) + 1;
            String msg = plugin.getMessage("cooldownMessage");
            if (msg != null) player.sendMessage(msg.replace("%seconds%", String.valueOf(secondsLeft)));
            return true;
        }
        return false;
    }

    protected boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(perm.getPermission(args));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return onCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player player = (sender instanceof Player p) ? p : null;
        if (requirePlayer && player == null) return Collections.emptyList();

        if (args.length == 1 && !tabCompleteArguments.isEmpty()) {
            return tabCompleteArguments.stream()
                    .filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return tabCompl(player, args);
    }

    // --- Abstract Methods ---
    protected abstract boolean perform(CommandSender sender, @Nullable Player player, String[] args);
    protected abstract boolean perform(Player player, String[] args);
    protected abstract List<String> tabCompl(@Nullable Player player, String[] args);

    // --- Restored All Getters & Setters ---
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getAliases() { return aliases != null ? aliases : new ArrayList<>(); }
    public static HashMap<String, CommandExecutor> getRegisteredCommands() { return registeredCommands; }

    public List<String> getTabCompleteArguments() { return tabCompleteArguments; }
    public void setTabCompleteArguments(List<String> arguments) { this.tabCompleteArguments = arguments; }
}
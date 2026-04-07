package me.catst0day.capi.API;


import me.catst0day.capi.CAPI;
import me.catst0day.capi.API.EventListeners.CAPIOnCommandEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static me.catst0day.capi.API.Utils.Util.log;

public abstract class CommandTemplate implements org.bukkit.command.CommandExecutor, org.bukkit.command.TabCompleter {
    private static final List<CommandTemplate> registeredCommands = new ArrayList<>();
    protected final CAPI plugin;
    protected final String name;
    protected final List<String> aliases; // добавлено поле для алиасов
    protected final String permission;
    protected final boolean requirePlayer;
    protected List<String> tabCompleteArguments = new ArrayList<>();
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final long cooldownSeconds;

    /**
     * Конструктор команды
     * @param plugin основной плагин CAPI
     * @param name имя команды (например, "home")
     * @param aliases список алиасов (например, ["h", "sethome"])
     * @param permission требуемое разрешение
     * @param requirePlayer требуется ли игрок (не консоль)
     * @param cooldownSeconds кулдаун в секундах
     */
    protected CommandTemplate(CAPI plugin, String name, List<String> aliases,
                              String permission, boolean requirePlayer, long cooldownSeconds) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases; // инициализация алиасов
        this.permission = permission;
        this.requirePlayer = requirePlayer;
        this.cooldownSeconds = cooldownSeconds;
        registeredCommands.add(this);
    }

    /**
     * Возвращает имя команды
     * @return имя команды (например, "home")
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает список алиасов команды
     * @return алиасы команды или пустой список, если их нет
     */
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

        if (!sender.hasPermission(permission)) {
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
            log("Ошибка выполнения команды " + name + ": " + e.getMessage());
            return false;
        }
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
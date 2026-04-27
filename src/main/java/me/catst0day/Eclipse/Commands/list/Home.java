
package me.catst0day.Eclipse.Commands.list;


import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Entity.Player.Gui;
import me.catst0day.Eclipse.Entity.Player.GuiButton;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Home extends CommandTemplate {

    private final EclipsePermissionManager permissionManager;

    public Home(Eclipse plugin) {
        super(plugin, "home", List.of("hm"), EclipsePermissionManager.CAPIPermissions.HOME, true, 0L, "tp to your homes (with gui)");
        this.permissionManager = plugin.getPermissionManager();
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (!permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.HOME)) {
            return false;
        }


        if (args.length > 0) {
            String homeName = args[0];
            int maxHomes = getMaxHomes(player);
            int currentHomes = plugin.getI().getHomeManager()
                    .getPlayerHomes(player.getUniqueId()).size();

            if (currentHomes >= maxHomes) {
                player.sendMessage(plugin.getMessage("homeLimitReached")
                        .replace("{max}", String.valueOf(maxHomes)));
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return perform((CommandSender) player, player, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (!hasPermission(sender, args)) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length == 0) {
            showHomeMenu(player);
            return true;
        }

        String homeName = args[0];
        Location homeLocation = plugin.getI().getHomeManager().getHome(player.getUniqueId(), homeName);

        if (homeLocation == null) {
            sender.sendMessage(plugin.getMessage("homeNotFound")
                    .replace("{homename}", homeName));
            return true;
        }

        if (homeLocation.getWorld() == null) {
            sender.sendMessage(plugin.getMessage("homeWorldNotFound"));
            return true;
        }

        player.teleport(homeLocation);
        sender.sendMessage(plugin.getMessage("homeTeleported")
                .replace("{homename}", homeName));
        return true;
    }

    private void showHomeMenu(Player player) {
        List<String> homes = plugin.getI().getHomeManager().getPlayerHomes(player.getUniqueId());

        Gui gui = new Gui(player, "§7Homes", 6);

        fillBorders(gui);

        int slot = 10;
        for (String home : homes) {
            GuiButton homeButton = createHomeButton(player, home);
            gui.addButton(slot, homeButton);
            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        gui.open();
    }

    private void fillBorders(Gui gui) {
        GuiButton glassButton = new GuiButton(Material.WHITE_STAINED_GLASS_PANE)
                .setName(" ");

        for (int i = 0; i < 9; i++) {
            gui.addButton(i, glassButton);
            gui.addButton(45 + i, glassButton);
        }

        for (int row = 1; row < 5; row++) {
            gui.addButton(row * 9, glassButton);
            gui.addButton(row * 9 + 8, glassButton);
        }
    }

    private GuiButton createHomeButton(Player player, String homeName) {
        GuiButton button = new GuiButton(Material.PLAYER_HEAD)
                .setName("§a" + homeName)
                .addLore("§fTeleport to home")
                .addLore("")
                .addLore("§eClick to teleport")
                .onLeftClick(p -> {
                    Location homeLocation = plugin.getI().getHomeManager().getHome(p.getUniqueId(), homeName);
                    if (homeLocation != null) {
                        p.teleport(homeLocation);
                        p.closeInventory();
                        p.sendMessage(plugin.getMessage("homeTeleported")
                                .replace("{homename}", homeName));
                    }
                });
        return button;
    }

    public boolean canSetMoreHomes(Player player) {
        int maxHomes = getMaxHomes(player);
        int currentHomes = plugin.getI().getHomeManager()
                .getPlayerHomes(player.getUniqueId()).size();
        return currentHomes < maxHomes;
    }

    private int getMaxHomes(Player player) {
        for (int i = 100; i >= 1; i--) {
            if (permissionManager.hasPermission(player, EclipsePermissionManager.CAPIPermissions.MAX_HOMES, String.valueOf(i))) {
                return i;
            }
        }
        return 1;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> playerHomes = plugin.getI().getHomeManager().getPlayerHomes(player.getUniqueId());
            for (String home : playerHomes) {
                if (home.toLowerCase().startsWith(prefix)) {
                    completions.add(home);
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
        } else if (args.length > 1) {
            completions.clear();
        }
        return completions;
    }
}
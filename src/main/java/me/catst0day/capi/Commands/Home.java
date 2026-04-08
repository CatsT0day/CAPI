package me.catst0day.capi.Commands;

import me.catst0day.capi.API.CommandTemplate;
import me.catst0day.capi.CAPI;
import me.catst0day.capi.API.GUI.CAPIGui;
import me.catst0day.capi.API.GUI.CAPIGuiButton;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Home extends CommandTemplate {

    public static final String PERMISSION_PREFIX = "CatAPI.Homes.max.";

    public Home(CAPI plugin) {
        super(plugin, "home", List.of(), "catapi.home.use", true, 0L);
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return execute((CommandSender) player, player, args);
    }

    @Override
    protected boolean execute(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission("catapi.home.use")) {
            sender.sendMessage(plugin.getMessage("noPermission"));
            return true;
        }

        if (args.length == 0) {
            showHomeMenu(player);
            return true;
        }

        String homeName = args[0];
        Location homeLocation = plugin.getInstance().getHomeManager().getHome(player.getUniqueId(), homeName);

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
        List<String> homes = plugin.getInstance().getHomeManager().getPlayerHomes(player.getUniqueId());

        // Создаём GUI
        CAPIGui gui = new CAPIGui(player, "§7Homes", 6); // 6 строк для двух слоёв стекла

        // Заполняем рамку белым стеклом
        fillBorders(gui);

        // Добавляем дома как головы игроков
        int slot = 10; // Начинаем с 10 слота (вторая строка)
        for (String home : homes) {
            CAPIGuiButton homeButton = createHomeButton(player, home);
            gui.addButton(slot, homeButton);
            slot++;
            if (slot % 9 == 8) slot += 2; // Переходим на следующую строку
        }

        gui.open();
    }

    private void fillBorders(CAPIGui gui) {
        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        CAPIGuiButton glassButton = new CAPIGuiButton(Material.WHITE_STAINED_GLASS_PANE)
                .setName(" ");

        // Верхний и нижний слои (строки 0 и 5)
        for (int i = 0; i < 9; i++) {
            gui.addButton(i, glassButton);
            gui.addButton(45 + i, glassButton);
        }

        // Боковые слои (столбцы 0 и 8)
        for (int row = 1; row < 5; row++) {
            gui.addButton(row * 9, glassButton);
            gui.addButton(row * 9 + 8, glassButton);
        }
    }

    private CAPIGuiButton createHomeButton(Player player, String homeName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        CAPIGuiButton button = new CAPIGuiButton(head.getType())
                .setName("§a" + homeName)
                .addLore("§Teleport to home")
                .addLore("")
                .addLore("§eClick to teleport")
                .onLeftClick(p -> {
                    Location homeLocation = plugin.getInstance().getHomeManager().getHome(p.getUniqueId(), homeName);
                    if (homeLocation != null) {
                        p.teleport(homeLocation);
                        p.closeInventory();
                        p.sendMessage(plugin.getMessage("homeTeleported")
                                .replace("{homename}", homeName));
                    }
                });
        return button;
    }

    /**
     * Проверяет, может ли игрок установить ещё один дом.
     *
     * @param player игрок
     * @return true, если может; false, если достиг лимита
     */
    public boolean canSetMoreHomes(Player player) {
        int maxHomes = getMaxHomes(player);
        int currentHomes = plugin.getInstance().getHomeManager()
                .getPlayerHomes(player.getUniqueId()).size();
        return currentHomes < maxHomes;
    }

    /**
     * Получает максимальное количество домов для игрока на основе пермишенов.
     *
     * @param player игрок
     * @return максимальное количество домов
     */
    private int getMaxHomes(Player player) {
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission(PERMISSION_PREFIX + i)) {
                return i;
            }
        }
        // Дефолтное значение, если нет пермишенов
        return 1;
    }

    @Override
    protected List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        try {
            if (args.length == 1) {
                String prefix = args[0].toLowerCase();
                List<String> playerHomes = plugin.getInstance().getHomeManager().getPlayerHomes(player.getUniqueId());
                for (String home : playerHomes) {
                    if (home.toLowerCase().startsWith(prefix)) {
                        completions.add(home);
                    }
                }
                completions.sort(String.CASE_INSENSITIVE_ORDER);
            } else if (args.length > 1) {
                completions.clear();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка автодополнения для команды /home: " + e.getMessage());
        }

        return completions;
    }
}
package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import me.catst0day.capi.Schedulers.CAPIMainScheduler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EFly extends CAPICommandTemplate implements Listener {

    private final Set<UUID> eflyActive = new HashSet<>();

    public EFly(CAPI plugin) {
        super(plugin, "efly", List.of(), CAPIPermissionManager.CAPIPerm.ELYTRAFLY, true, 0, "Elytra flight mode");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (!eflyActive.contains(player.getUniqueId()) && !hasElytra(player)) {
            player.sendMessage(plugin.getMessage("ElytraRequired"));
            return true;
        }

        boolean newState = !eflyActive.contains(player.getUniqueId());

        if (newState) {
            eflyActive.add(player.getUniqueId());
            player.setAllowFlight(true);
        } else {
            eflyActive.remove(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        String status = newState ? plugin.getMessage("enabled") : plugin.getMessage("disabled");
        player.sendMessage(plugin.getMessage("flyToggled").replace("%s", status));

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("true", "false", "on", "off");
        }
        return List.of();
    }

    private boolean hasElytra(Player player) {
        ItemStack chest = player.getInventory().getChestplate();
        return chest != null && chest.getType() == Material.ELYTRA;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!eflyActive.contains(player.getUniqueId())) return;

        CAPIMainScheduler.runTask(plugin, () -> {
            if (!hasElytra(player)) {
                eflyActive.remove(player.getUniqueId());
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(plugin.getMessage("ElytraRequired"));
            }
        });
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }
}
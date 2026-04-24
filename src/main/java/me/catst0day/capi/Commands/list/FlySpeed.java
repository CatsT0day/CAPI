package me.catst0day.capi.Commands.list;

import me.catst0day.capi.CAPI;
import me.catst0day.capi.Commands.commandAPI.CAPICommandTemplate;
import me.catst0day.capi.Managers.CAPIPermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FlySpeed extends CAPICommandTemplate {

    public FlySpeed(CAPI plugin) {
        super(plugin, "flyspeed", List.of("fspeed"), CAPIPermissionManager.CAPIPerm.ELYTRAFLY, true, 0, "Set fly speed");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        return execute(player, args);
    }
    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new java.util.ArrayList<>(List.of("0.5", "1", "2", "5", "10"));
            Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2) {
            return List.of("0.5", "1", "2", "5", "10");
        }
        return List.of();
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        return execute(sender, args);
    }

    private boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("usage").replace("%s", "/flyspeed [игрок] <0-10>"));
            return true;
        }

        Player target;
        float speed;

        try {
            if (args.length == 1) {
                if (!(sender instanceof Player)) return false;
                target = (Player) sender;
                speed = Float.parseFloat(args[0]);
            } else {
                target = Bukkit.getPlayer(args[0]);
                speed = Float.parseFloat(args[1]);
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessage("speedInvalid"));
            return true;
        }

        if (target == null) {
            sender.sendMessage(plugin.getMessage("playerNotFound"));
            return true;
        }


        if (speed < 0 || speed > 10) {
            sender.sendMessage(plugin.getMessage("speedInvalid"));
            return true;
        }

        float bukkitSpeed = speed / 10.0f;
        target.setFlySpeed(bukkitSpeed);

        String msg = plugin.getMessage("speedSetFly")
                .replace("%player%", target.getName())
                .replace("%s", String.valueOf(speed));
        sender.sendMessage(msg);

        return true;
    }
}

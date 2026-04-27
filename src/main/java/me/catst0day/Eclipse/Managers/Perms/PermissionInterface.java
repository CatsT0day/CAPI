package me.catst0day.Eclipse.Managers.Perms;

import java.util.UUID;
import org.bukkit.entity.Player;

public interface PermissionInterface {
    String getMainGroup(Player player);
    String getPrefix(UUID uuid);
    String getSufix(UUID uuid);
    String getPrefix(Player player);
    String getSufix(Player player);
    String getNameColor(Player player);

    String getMainGroup(String var1, UUID var2);
}

package me.catst0day.capi.Managers.Perms;

import java.util.UUID;

import me.catst0day.capi.Managers.Perms.PermissionInterface;
import org.bukkit.entity.Player;

public class NoneHandler implements PermissionInterface {
    public NoneHandler() {
    }

    public String getMainGroup(Player var1) {
        return null;
    }

    public String getPrefix(Player var1) {
        return null;
    }

    public String getSufix(Player var1) {
        return null;
    }

    public String getMainGroup(String var1, UUID var2) {
        return null;
    }

    public String getPrefix(UUID var1) {
        return null;
    }

    public String getSufix(UUID var1) {
        return null;
    }

    public String getNameColor(Player var1) {
        return null;
    }
}


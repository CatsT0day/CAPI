package me.catst0day.capi.API.Enums;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Version {
    v1_7_R1,
    v1_7_R2,
    v1_7_R3,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_13_R3,
    v1_14_R1,
    v1_14_R2,
    v1_15_R1,
    v1_15_R2,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3(4),
    v1_20_R4(5, 6),
    v1_21_R1(0, 1),
    v1_21_R2(2, 3),
    v1_21_R3(4),
    v1_21_R4(5),
    v1_21_R5(6, 7, 8),
    v1_21_R6(9, 10),
    v1_21_R7(11),
    v1_22_R1(0),
    v1_22_R2(1),
    v1_22_R3(2),
    v1_23_R1(0),
    v1_23_R2,
    v1_23_R3,
    v1_24_R1(0),
    v1_24_R2,
    v1_24_R3;

    private Integer value;
    private int[] minorVersions = null;
    private String shortVersion;
    private static int subVersion = 0;
    private static Version current = null;

    Version(int... versions) {
        this();
        minorVersions = versions;
    }

    Version() {
        try {
            this.value = Integer.valueOf(this.name().replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            this.value = 0;
        }
        shortVersion = this.name().substring(0, this.name().length() - 3);
    }

    public Integer getValue() {
        return value;
    }

    public String getShortVersion() {
        return shortVersion;
    }

    public String getShortFormated() {
        return shortVersion.replace("v", "").replace("_", ".") + ".x";
    }

    public String getFormated() {
        return shortVersion.replace("v", "").replace("_", ".") + "." + subVersion;
    }

    public static boolean isSpigot() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Minecraft getPlatform() {
        if (isFolia()) return Minecraft.folia;
        if (isPaper()) return Minecraft.paper;
        if (isSpigot()) return Minecraft.spigot;
        return Minecraft.craftbukkit;
    }

    public static Version getCurrent() {
        if (current != null) return current;

        String bukkitVersion = Bukkit.getBukkitVersion();
        if (bukkitVersion == null) {
            current = v1_20_R1; // запасная версия
            return current;
        }

        // Извлекаем основную версию (например, «1.21»)
        String mainVersion = bukkitVersion.split("-")[0];
        String[] versionParts = mainVersion.split("\\.");

        if (versionParts.length >= 2) {
            try {
                int major = Integer.parseInt(versionParts[0]);
                int minor = Integer.parseInt(versionParts[1]);
                subVersion = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;

                // Формируем имя версии (например, v1_21_R1)
                String versionName = "v" + major + "_" + minor + "_R1";
                for (Version v : values()) {
                    if (v.name().equalsIgnoreCase(versionName)) {
                        current = v;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                current = v1_20_R1;
            }
        } else {
            current = v1_20_R1;
        }

        return current;
    }

    public boolean isLower(Version version) {
        return getValue() < version.getValue();
    }

    public boolean isHigher(Version version) {
        return getValue() > version.getValue();
    }

    public boolean isEqualOrLower(Version version) {
        return getValue() <= version.getValue();
    }

    public boolean isEqualOrHigher(Version version) {
        return getValue() >= version.getValue();
    }

    public static boolean isCurrentEqualOrHigher(Version v) {
        return getCurrent().getValue() >= v.getValue();
    }

    public static boolean isCurrentHigher(Version v) {
        return getCurrent().getValue() > v.getValue();
    }

    public static boolean isCurrentLower(Version v) {
        return getCurrent().getValue() < v.getValue();
    }

    public static boolean isCurrentEqualOrLower(Version v) {
        return getCurrent().getValue() <= v.getValue();
    }

    public static boolean isCurrentEqual(Version v) {
        return getCurrent().getValue() == v.getValue();
    }

    public static boolean isCurrentSubEqualOrHigher(int subVersion) {
        return Version.subVersion >= subVersion;
    }

    public static boolean isCurrentSubHigher(int subVersion) {
        return Version.subVersion > subVersion;
    }

    public static boolean isCurrentSubLower(int subVersion) {
        return Version.subVersion < subVersion;
    }

    public static boolean isCurrentSubEqual(int subVersion) {
        return Version.subVersion == subVersion;
    }

    public static Integer convertVersion(String v) {
        v = v.replaceAll("[^\\d.]", "");
        Integer version = 0;
        if (v.contains(".")) {
            String lVersion = "";
            for (String one : v.split("\\.")) {
                String s = one;
                if (s.length() == 1)
                    s = "0" + s;
                lVersion += s;
            }

            try {
                version = Integer.parseInt(lVersion);
            } catch (Exception e) {
                // Игнорируем ошибку, возвращаем 0
            }
        } else {
            try {
                version = Integer.parseInt(v);
            } catch (Exception e) {
                // Игнорируем ошибку, возвращаем 0
            }
        }
        return version;
    }

    public static String deconvertVersion(Integer v) {
        StringBuilder version = new StringBuilder();
        String vs = String.valueOf(v);

        while (vs.length() > 0) {
            int subv = 0;
            try {
                if (vs.length() > 2) {
                    subv = Integer.parseInt(vs.substring(vs.length() - 2));
                    version.insert(0, "." + subv);
                } else {
                    subv = Integer.parseInt(vs);
                    version.insert(0, subv);
                }
            } catch (Throwable e) {
                // Игнорируем ошибки преобразования
            }
            if (vs.length() > 2)
                vs = vs.substring(0, vs.length() - 2);
            else
                break;
        }

        return version.toString();
    }

    private String getSimplifiedVersion() {
        return this.name().substring(1).replace("_", ".").split("R", 2)[0];
    }

    public List<String> getMinorVersions() {
        if (minorVersions == null)
            return new ArrayList<>();

        return Arrays.stream(minorVersions)
                .mapToObj(version -> getSimplifiedVersion() + version)
                .collect(Collectors.toList());
    }
}

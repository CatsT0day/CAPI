package me.catst0day.Eclipse.Utils;

import org.bukkit.ChatColor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtil {
    public static final String colorReplacerPlaceholder = "＆";
    public static final String hexSymbol = "#";

    public static final Pattern cleanOfficialColorRegexPattern = Pattern.compile("(?<!:\"|" + colorReplacerPlaceholder + ")" + hexSymbol + "([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    public static final Pattern cleanQuirkyHexColorRegexPattern = Pattern.compile("&" + hexSymbol + "([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    public static final Pattern hexColorRegexPatternLast = Pattern.compile(hexSymbol + "([a-fA-F0-9]{6}|[a-fA-F0-9]{3})(?!.*" + hexSymbol + ")");
    public static final Pattern hexDeColorNamePattern = Pattern.compile("(([&§])x)((([&§])[0-9A-Fa-f]){6})");

    public static final HexUtil BLACK = new HexUtil("Black", '0', 0, 0, 0);
    public static final HexUtil DARK_BLUE = new HexUtil("Dark_Blue", '1', 0, 0, 170);
    public static final HexUtil DARK_GREEN = new HexUtil("Dark_Green", '2', 0, 170, 0);
    public static final HexUtil DARK_AQUA = new HexUtil("Dark_Aqua", '3', 0, 170, 170);
    public static final HexUtil DARK_RED = new HexUtil("Dark_Red", '4', 170, 0, 0);
    public static final HexUtil DARK_PURPLE = new HexUtil("Dark_Purple", '5', 170, 0, 170);
    public static final HexUtil GOLD = new HexUtil("Gold", '6', 255, 170, 0);
    public static final HexUtil GRAY = new HexUtil("Gray", '7', 170, 170, 170);
    public static final HexUtil DARK_GRAY = new HexUtil("Dark_Gray", '8', 85, 85, 85);
    public static final HexUtil BLUE = new HexUtil("Blue", '9', 85, 85, 255);
    public static final HexUtil GREEN = new HexUtil("Green", 'a', 85, 255, 85);
    public static final HexUtil AQUA = new HexUtil("Aqua", 'b', 85, 255, 255);
    public static final HexUtil RED = new HexUtil("Red", 'c', 255, 85, 85);
    public static final HexUtil LIGHT_PURPLE = new HexUtil("Light_Purple", 'd', 255, 85, 255);
    public static final HexUtil YELLOW = new HexUtil("Yellow", 'e', 255, 255, 85);
    public static final HexUtil WHITE = new HexUtil("White", 'f', 255, 255, 255);

    public static final HexUtil OBFUSCATED = new HexUtil("Obfuscated", 'k', false);
    public static final HexUtil BOLD = new HexUtil("Bold", 'l', false);
    public static final HexUtil STRIKETHROUGH = new HexUtil("Strikethrough", 'm', false);
    public static final HexUtil UNDERLINE = new HexUtil("Underline", 'n', false);
    public static final HexUtil ITALIC = new HexUtil("Italic", 'o', false);
    public static final HexUtil RESET = new HexUtil("Reset", 'r', false, true);
    public static final HexUtil HEX = new HexUtil("Hex", 'x', false, false);

    private char c = 10;
    private boolean color = true;
    private boolean isReset = false;
    private int redChannel = -1;
    private int greenChannel = -1;
    private int blueChannel = -1;
    private int alpha = 255;
    private String hexCode = null;
    private String name;

    @Override
    public String toString() {
        return "§" + c;
    }

    public HexUtil(String name, char c, int red, int green, int blue) {
        this(name, c, true, false, red, green, blue);
    }

    public HexUtil(String hex, char m, boolean b) {
        this(null, hex);
    }

    public HexUtil(String name, String hex) {
        if (hex == null) return;
        if (hex.startsWith(hexSymbol)) hex = hex.substring(hexSymbol.length());
        hex = expandHex(hex);

        if (hex.length() == 6) this.hexCode = hex;
        String alphaString = null;
        if (hex.length() == 8) {
            this.hexCode = hex.substring(0, 6);
            alphaString = hex.substring(6, 8);
        }
        this.name = name;
        try {
            if (this.hexCode != null) {
                int rgb = Integer.parseInt(this.hexCode, 16);
                this.redChannel = (rgb >> 16) & 0xFF;
                this.greenChannel = (rgb >> 8) & 0xFF;
                this.blueChannel = rgb & 0xFF;
            }
            if (alphaString != null) this.alpha = Integer.parseInt(alphaString, 16);
        } catch (NumberFormatException ignored) {}
    }

    private HexUtil(String name, char c, boolean color, boolean isReset, int red, int green, int blue) {
        this.name = name; this.c = c; this.color = color; this.isReset = isReset;
        this.redChannel = red; this.greenChannel = green; this.blueChannel = blue;
    }

    private HexUtil(String name, char c, boolean format, boolean isReset) {
        this.name = name; this.c = c; this.color = format; this.isReset = isReset;
    }

    private static String expandHex(String hex) {
        if (hex == null) return null;
        if (hex.startsWith(hexSymbol)) hex = hex.substring(hexSymbol.length());
        if (hex.length() == 3) {
            return "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        } else if (hex.length() == 4) {
            return "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2) + hex.charAt(3) + hex.charAt(3);
        }
        return hex;
    }

    public static List<String> deColorize(List<String> lore) {
        lore.replaceAll(HexUtil::deColorize);
        return lore;
    }

    public static String deColorize(String text) {
        if (text == null || text.isEmpty()) return text;
        Matcher matcher = hexDeColorNamePattern.matcher(text.replace("&", "§"));
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group().replace("§x", "").replace("§", "");
            matcher.appendReplacement(sb, hexSymbol + hex);
        }
        matcher.appendTail(sb);
        return sb.toString().replace("§", "&");
    }

    public static String stripColor(String text) {
        if (text == null) return null;
        return ChatColor.stripColor(translateHexAndAlternateColorCodes(text));
    }

    public static String getLastColors(String text) {
        if (text == null) return null;
        String translated = translateHexAndAlternateColorCodes(text);
        Matcher match = hexColorRegexPatternLast.matcher(translated);
        if (match.find()) return toBukkit(expandHex(match.group(1)));
        return ChatColor.getLastColors(translated);
    }

    public static String translateHexAndAlternateColorCodes(String text) {
        if (text == null || text.isEmpty()) return text;

        Matcher quirkyMatcher = cleanQuirkyHexColorRegexPattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (quirkyMatcher.find()) {
            quirkyMatcher.appendReplacement(sb, toBukkit(expandHex(quirkyMatcher.group(1))));
        }
        quirkyMatcher.appendTail(sb);
        text = sb.toString();

        Matcher officialMatcher = cleanOfficialColorRegexPattern.matcher(text);
        sb = new StringBuilder();
        while (officialMatcher.find()) {
            officialMatcher.appendReplacement(sb, toBukkit(expandHex(officialMatcher.group(1))));
        }
        officialMatcher.appendTail(sb);

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && isColorCode(chars[i + 1])) {
                chars[i] = '§';
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }

    private static String toBukkit(String hex) {
        StringBuilder magic = new StringBuilder("§x");
        for (char c : hex.toCharArray()) magic.append('§').append(c);
        return magic.toString();
    }

    private static boolean isColorCode(char c) {
        return "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(c) > -1;
    }

    public String getName() { return name; }
    public char getChar() { return c; }
    public boolean isColor() { return color; }
    public boolean isReset() { return isReset; }
    public int getRed() { return redChannel; }
    public int getGreen() { return greenChannel; }
    public int getBlue() { return blueChannel; }
    public int getAlpha() { return alpha; }
    public String getHexCode() { return hexCode; }
}
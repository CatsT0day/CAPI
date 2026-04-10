package me.catst0day.capi.Chat;

import org.bukkit.ChatColor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CAPIChatColor {

    private static final Map<String, CAPIChatColor> BY_CHAR = new HashMap<>();
    private static final Map<String, CAPIChatColor> BY_NAME = new HashMap<>();
    private static final LinkedHashMap<String, CAPIChatColor> CUSTOM_BY_NAME = new LinkedHashMap<>();
    private static final Map<String, CAPIChatColor> CUSTOM_BY_HEX = new HashMap<>();
    private static final TreeMap<String, CAPIChatColor> CUSTOM_BY_RGB = new TreeMap<>();


    public static final String colorReplacerPlaceholder = "\uFF06";
    public static String globalColorPrefix = "{gc";
    public static final String hexSymbol = "#";
    public static final String colorHexReplacerPlaceholder = "{" + colorReplacerPlaceholder + hexSymbol;
    public static final String colorFontPrefix = "{@";
    public static final String colorCodePrefix = "{" + hexSymbol;
    public static final String colorCodeSuffix = "}";
    public static final String gradientStart = ">";
    public static final String gradientEnd = "<";
    public static final String gradientMiddle = gradientEnd + gradientStart;
    public static final String hexColorRegex = "(\\" + colorCodePrefix + ")([0-9A-Fa-f]{6}|[0-9A-Fa-f]{3})(\\" + colorCodeSuffix + ")";

    public static final Pattern cleanOfficialColorRegexPattern = Pattern.compile("(?<!\\{|:\"|" + colorReplacerPlaceholder + ")#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    public static final Pattern cleanQuirkyHexColorRegexPattern = Pattern.compile("&#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");
    public static final Pattern hexColorRegexPattern = Pattern.compile(hexColorRegex);
    public static final Pattern hexColorRegexPatternLast = Pattern.compile(hexColorRegex + "(?!.*\\{#)");
    public static final Pattern hexDeColorNamePattern = Pattern.compile("((&|§)x)(((&|§)[0-9A-Fa-f]){6})");
    public static final String ColorNameRegex = "(\\" + colorCodePrefix + ")([a-zA-Z_]{3,})(\\" + colorCodeSuffix + ")";
    public static final Pattern hexColorNamePattern = Pattern.compile(ColorNameRegex);
    public static final Pattern hexColorNamePatternLast = Pattern.compile(ColorNameRegex + "(?!.*\\{#)");
    public static final String ColorFontRegex = "(\\" + colorFontPrefix + ")([a-zA-Z_]{3,})(\\" + colorCodeSuffix + ")";
    public static final Pattern gradientPattern = Pattern.compile("(\\{(#[^\\{\\}]*?)>\\})(.*?)(\\{(#[^\\{\\}]*?)<(>?)\\})");
    public static final String hexColorDecolRegex = "(&x)(&[0-9A-Fa-f]){6}";
    public static final Pattern postGradientPattern = Pattern.compile("(" + hexColorRegex + "|" + ColorNameRegex + ")(.)(" + hexColorRegex + "|" + ColorNameRegex + ")");
    public static final Pattern post2GradientPattern = Pattern.compile("(" + hexColorRegex + "|" + ColorNameRegex + ")(.)((" + hexColorRegex + "|" + ColorNameRegex + ")(.))+");
    public static final Pattern fullPattern = Pattern.compile("(&[0123456789abcdefklmnorABCDEFKLMNOR])|" + hexColorRegex + "|" + ColorNameRegex + "|" + ColorFontRegex);
    public static final Pattern formatPattern = Pattern.compile("(&[klmnorKLMNOR])");

    public static final CAPIChatColor BLACK = new CAPIChatColor("Black", '0', 0, 0, 0);
    public static final CAPIChatColor DARK_BLUE = new CAPIChatColor("Dark_Blue", '1', 0, 0, 170);
    public static final CAPIChatColor DARK_GREEN = new CAPIChatColor("Dark_Green", '2', 0, 170, 0);
    public static final CAPIChatColor DARK_AQUA = new CAPIChatColor("Dark_Aqua", '3', 0, 170, 170);
    public static final CAPIChatColor DARK_RED = new CAPIChatColor("Dark_Red", '4', 170, 0, 0);
    public static final CAPIChatColor DARK_PURPLE = new CAPIChatColor("Dark_Purple", '5', 170, 0, 170);
    public static final CAPIChatColor GOLD = new CAPIChatColor("Gold", '6', 255, 170, 0);
    public static final CAPIChatColor GRAY = new CAPIChatColor("Gray", '7', 170, 170, 170);
    public static final CAPIChatColor DARK_GRAY = new CAPIChatColor("Dark_Gray", '8', 85, 85, 85);
    public static final CAPIChatColor BLUE = new CAPIChatColor("Blue", '9', 85, 85, 255);
    public static final CAPIChatColor GREEN = new CAPIChatColor("Green", 'a', 85, 255, 85);
    public static final CAPIChatColor AQUA = new CAPIChatColor("Aqua", 'b', 85, 255, 255);
    public static final CAPIChatColor RED = new CAPIChatColor("Red", 'c', 255, 85, 85);
    public static final CAPIChatColor LIGHT_PURPLE = new CAPIChatColor("Light_Purple", 'd', 255, 85, 255);
    public static final CAPIChatColor YELLOW = new CAPIChatColor("Yellow", 'e', 255, 255, 85);
    public static final CAPIChatColor WHITE = new CAPIChatColor("White", 'f', 255, 255, 255);

    public static final CAPIChatColor OBFUSCATED = new CAPIChatColor("Obfuscated", 'k', false);
    public static final CAPIChatColor BOLD = new CAPIChatColor("Bold", 'l', false);
    public static final CAPIChatColor STRIKETHROUGH = new CAPIChatColor("Strikethrough", 'm', false);
    public static final CAPIChatColor UNDERLINE = new CAPIChatColor("Underline", 'n', false);
    public static final CAPIChatColor ITALIC = new CAPIChatColor("Italic", 'o', false);
    public static final CAPIChatColor RESET = new CAPIChatColor("Reset", 'r', false, true);
    public static final CAPIChatColor HEX = new CAPIChatColor("Hex", 'x', false, false);

    private char c = 10;
    private boolean color = true;
    private boolean isReset = false;
    private Pattern pattern = null;
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
    public CAPIChatColor(String name, char c, int red, int green, int blue) {
        this(name, c, true, false, red, green, blue);
    }

    public CAPIChatColor(String hex, char m, boolean b) {
        this(null, hex);
    }

    public CAPIChatColor(String name, String hex) {
        if (hex == null) return;

        if (hex.startsWith(colorCodePrefix)) {
            hex = hex.substring(colorCodePrefix.length());
        }
        if (hex.endsWith(colorCodeSuffix)) {
            hex = hex.substring(0, hex.length() - colorCodeSuffix.length());
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        hex = expandHex(hex);

        if (hex.length() == 6) {
            this.hexCode = hex;
        }

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
            if (alphaString != null) {
                this.alpha = Integer.parseInt(alphaString, 16);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private CAPIChatColor(String name, char c, boolean color, boolean isReset, int red, int green, int blue) {
        this.name = name;
        this.c = c;
        this.color = color;
        this.isReset = isReset;
        this.redChannel = red;
        this.greenChannel = green;
        this.blueChannel = blue;
    }

    private CAPIChatColor(String name, char c, boolean format, boolean isReset) {
        this.name = name;
        this.c = c;
        this.color = format;
        this.isReset = isReset;
    }

    private static String expandHex(String hex) {
        if (hex == null) return null;
        hex = hex.startsWith("#") ? hex.substring(1) : hex;

        switch (hex.length()) {
            case 3:
                return "" +
                        hex.charAt(0) + hex.charAt(0) +
                        hex.charAt(1) + hex.charAt(1) +
                        hex.charAt(2) + hex.charAt(2);
            case 4:
                return "" +
                        hex.charAt(0) + hex.charAt(0) +
                        hex.charAt(1) + hex.charAt(1) +
                        hex.charAt(2) + hex.charAt(2) +
                        hex.charAt(3) + hex.charAt(3);
            default:
                return hex;
        }
    }
    public static List<String> deColorize(List<String> lore) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, deColorize(Collections.singletonList(lore.get(i))).toString());
        }
        return lore;
    }

    public static String stripColor(String text) {
        if (text == null)
            return null;
        text = CAPIChatColor.translate(text);
        return ChatColor.stripColor(text);
    }

    public static String stripHexColor(String message) {

        message = translate(message);

        Matcher match = hexColorRegexPattern.matcher(message);

        while (match.find()) {
            String string = match.group();
            message = message.replace(string, "");
        }

        if (message.contains("&x") || message.contains("§x")) {
            match = hexDeColorNamePattern.matcher(message);
            while (match.find()) {
                String string = match.group();
                message = message.replace(string, "");
            }
        }

        return message;
    }

    public static String getLastColors(String text) {
        if (text == null)
            return null;

        text = deColorize(Collections.singletonList(text)).toString();
        Matcher match = hexColorRegexPatternLast.matcher(text);
        if (match.find()) {
            String colorByHex = match.group(0);
            if (text.endsWith(colorByHex))
                return colorByHex;
            String[] split = text.split(escape(colorByHex), 2);
            if (split == null)
                return colorByHex;
            String last = getLastColors(split[1]);
            return last == null || last.isEmpty() ? colorByHex : last;

        }

        match = hexColorNamePatternLast.matcher(text);
        if (match.find()) {
            String colorByName = match.group();
            if (text.endsWith(colorByName))
                return colorByName;
            String[] split = text.split(escape(colorByName), 2);
            if (split == null)
                return colorByName;
            String last = getLastColors(split[1]);
            return last == null || last.isEmpty() ? colorByName : last;
        }

        return ChatColor.getLastColors(translate(text));
    }

    public String getName() {
        return name;
    }

    public char getChar() {
        return c;
    }

    public boolean isColor() {
        return color;
    }

    public boolean isReset() {
        return isReset;
    }

    public int getRed() {
        return redChannel;
    }

    public int getGreen() {
        return greenChannel;
    }

    public int getBlue() {
        return blueChannel;
    }

    public int getAlpha() {
        return alpha;
    }

    public String getHexCode() {
        return hexCode;
    }

    public static String translate(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Matcher hexMatcher = hexColorRegexPattern.matcher(text);
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(2);
            hex = expandHex(hex);

            StringBuilder magic = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                magic.append('§').append(c);
            }
            text = text.replace(hexMatcher.group(), magic.toString());
        }

        Matcher quirkyMatcher = cleanQuirkyHexColorRegexPattern.matcher(text);
        while (quirkyMatcher.find()) {
            String hex = quirkyMatcher.group().substring(2); // убираем &#
            hex = expandHex(hex);

            StringBuilder magic = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                magic.append('§').append(c);
            }
            text = text.replace(quirkyMatcher.group(), magic.toString());
        }

        Matcher officialMatcher = cleanOfficialColorRegexPattern.matcher(text);
        while (officialMatcher.find()) {
            String hex = officialMatcher.group().substring(1); // убираем #
            hex = expandHex(hex);

            StringBuilder magic = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                magic.append('§').append(c);
            }
            text = text.replace(officialMatcher.group(), magic.toString());
        }

        text = translateVanillaColorCodes(text);

        return text;
    }
    private static String translateVanillaColorCodes(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && isColorCode(chars[i + 1])) {
                chars[i] = '§';
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }

    private static boolean isColorCode(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F') || c == 'k' || c == 'l' ||
                c == 'm' || c == 'n' || c == 'o' || c == 'r' ||
                c == 'K' || c == 'L' || c == 'M' || c == 'N' ||
                c == 'O' || c == 'R';
    }
    private static String escape(String text) {
        return text.replace("#", "\\#").replace("{", "\\{").replace("}", "\\}");
    }
}
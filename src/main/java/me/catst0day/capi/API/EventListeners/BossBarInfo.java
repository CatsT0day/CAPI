
package me.catst0day.capi.API.EventListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.catst0day.capi.API.Shedulers.CAPITask;

public class BossBarInfo {
    private Player player;
    private Double percentage = null;
    private Double adjustPerc = null;
    private Integer keepFor = 60;
    private Integer auto = null;
    private BossBar bar;
    private BarColor startingColor = null;
    private BarStyle style = null;
    private CAPITask autoScheduler = null;
    private CAPITask hideScheduler = null;
    private String nameOfBar;
    private String titleOfBar = "";
    private boolean withPlaceholder = false;
    private List<String> cmds = null;
    private boolean global = false;
    private boolean makeVisible = false;
    private long started = 0L;
    private boolean translateColors = true;
    private List<ChatColor> colors = null;
    private int colorChangeIntervalTicks = 20;
    private long nextColorChange = 0L;
    private final Plugin plugin;

    public BossBarInfo(Plugin plugin, String nameOfBar) {
        this(plugin, null, nameOfBar, null);
    }

    public BossBarInfo(Plugin plugin, Player player, String nameOfBar) {
        this(plugin, player, nameOfBar, null);
    }

    public BossBarInfo(Plugin plugin, Player player, String nameOfBar, BossBar bar) {
        this.plugin = plugin;
        this.player = player;
        this.nameOfBar = nameOfBar;
        this.bar = bar;
        started = System.currentTimeMillis();
        // Создаём бар, если он не передан
        createBossBar();
    }

    /**
     * Создаёт BossBar, если он ещё не создан, используя текущие настройки объекта
     */
    private void createBossBar() {
        if (bar != null) {
            return; // Бар уже создан
        }

        if (player == null) {
            plugin.getLogger().warning("Cannot create BossBar for null player. Bar name: " + nameOfBar);
            return;
        }

        String title = getTitleOfBar(player);
        BarColor color = startingColor != null ? startingColor : BarColor.WHITE;
        BarStyle style = this.style != null ? this.style : BarStyle.SOLID;

        bar = Bukkit.createBossBar(NamespacedKey.minecraft(getNameOfBar()), title, color, style);

        // Применяем процент, если он задан
        if (percentage != null) {
            bar.setProgress(percentage);
        }

        // Если видимость включена, показываем бар игроку сразу
        if (makeVisible) {
            bar.addPlayer(player);
            bar.setVisible(true);
        }
    }

    public BossBarInfo clone(Player player) {
        BossBarInfo barInfo = new BossBarInfo(plugin, player, nameOfBar);
        barInfo.percentage = percentage;
        barInfo.adjustPerc = adjustPerc;
        barInfo.keepFor = keepFor;
        barInfo.auto = auto;
        barInfo.bar = bar;
        barInfo.startingColor = startingColor;
        barInfo.style = style;
        barInfo.nameOfBar = nameOfBar;
        barInfo.translateColors = translateColors;
        barInfo.titleOfBar = titleOfBar;
        barInfo.withPlaceholder = containsPlaceholder(titleOfBar);
        barInfo.cmds = cmds;
        barInfo.global = global;
        barInfo.colors = colors == null ? null : new ArrayList<>(colors);
        barInfo.colorChangeIntervalTicks = colorChangeIntervalTicks;
        // Инициализируем бар в клоне
        barInfo.createBossBar();
        return barInfo;
    }

    private boolean containsPlaceholder(String text) {
        return text != null && text.contains("%");
    }

    public synchronized void cancelAutoScheduler() {
        if (autoScheduler != null) {
            autoScheduler.cancel();
            autoScheduler = null;
        }
    }

    public synchronized void cancelHideScheduler() {
        if (hideScheduler != null) {
            hideScheduler.cancel();
            hideScheduler = null;
        }
    }

    public void remove() {
        cancelAutoScheduler();
        cancelHideScheduler();
        if (bar != null) {
            bar.setVisible(false);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public BossBar getBar() {
        createBossBar(); // Гарантируем, что бар создан
        return bar;
    }

    public Double getPercentage() {
        if (percentage == null) percentage = 0D;
        return Math.max(0, Math.min(1, percentage));
    }

    public void setPercentage(double max, double current) {
        if (max == 0) max = 1D;
        setPercentage(current / max);
    }

    public void setPercentage(Double percentage) {
        if (percentage != null) {
            percentage = Math.max(0, Math.min(1, percentage));
            if (Double.isNaN(percentage) || Double.isInfinite(percentage)) {
                percentage = adjustPerc != null && adjustPerc > 0 ? 0D : 1D;
            }
        }
        this.percentage = percentage;

        // Обновляем прогресс бара, если он существует
        if (bar != null) {
            bar.setProgress(this.percentage);
        } else {
            createBossBar();
        }
    }

    public String getNameOfBar() {
        if (nameOfBar == null)
            nameOfBar = "CAPIBossbar:new(this)" + (new Random().nextInt(Integer.MAX_VALUE));
        return nameOfBar;
    }

    public void setNameOfBar(String nameOfBar) {
        this.nameOfBar = nameOfBar;
    }

    public Integer getKeepFor() {
        return keepFor == null ? 30 : keepFor;
    }

    public void setKeepForTicks(Integer keepFor) {
        if (keepFor != null) this.keepFor = keepFor;
    }

    public String getTitleOfBarClean() {
        return titleOfBar == null ? "" : titleOfBar;
    }

    public String getTitleOfBar() {
        if (titleOfBar != null && titleOfBar.contains("[autoTimeLeft]")) {
            if (this.percentage != null && this.adjustPerc != null && this.auto != null) {
                return getDynamicColor() + titleOfBar.replace("[autoTimeLeft]", formatTime(getLeftDuration()));
            }
            return getDynamicColor() + titleOfBar.replace("[autoTimeLeft]", formatTime(0L));
        }
        return titleOfBar == null ? "" : getDynamicColor() + titleOfBar;
    }

    public String getTitleOfBar(Player player) {
        if (player == null) return getTitleOfBar();
        String t = getTitleOfBar();
        if (isWithPlaceholder()) t = processPlaceholders(player, t);
        return t == null ? "" : isTranslateColors() ? ChatColor.translateAlternateColorCodes('&', t) : t;
    }

    private String getDynamicColor() {
        if (colors == null || colors.isEmpty()) return "";
        if (nextColorChange > System.currentTimeMillis())
            return colors.get(colors.size() - 1).toString();
        nextColorChange = System.currentTimeMillis() + (colorChangeIntervalTicks * 50L);
        ChatColor c = colors.remove(0);
        colors.add(c);
        return c.toString();
    }

    public long getLeftDuration() {
        long mili = 0L;
        if (this.percentage != null && this.adjustPerc != null && this.auto != null) {
            double leftTicks = this.percentage / (this.adjustPerc < 0 ? -this.adjustPerc : this.adjustPerc);
            long totalTicks = (long) (leftTicks * (this.auto < 0 ? -this.auto : this.auto));
            mili = totalTicks * 50;
        }
        return mili;
    }

    public void setTitleOfBar(String titleOfBar) {
        if (titleOfBar == null || titleOfBar.isEmpty()) {
            this.titleOfBar = null;
        } else {
            this.titleOfBar = titleOfBar;
        }
        withPlaceholder = containsPlaceholder(titleOfBar);

        // Обновляем заголовок бара, если он существует
        if (bar != null && player != null) {
            bar.setTitle(getTitleOfBar(player));
        } else if (player != null) {
            createBossBar();
        }
    }

    public void setBar(BossBar bar) {
        this.bar = bar;
    }

    public BarColor getColor() {
        return startingColor;
    }

    public void setColor(BarColor startingColor) {
        this.startingColor = startingColor;

        if (bar != null) {
            bar.setColor(startingColor);
        } else {
            createBossBar();
        }
    }

    public Double getAdjustPerc() {
        return adjustPerc;
    }

    public void setAdjustPerc(Double adjustPerc) {
        this.adjustPerc = adjustPerc;
    }

    public BarStyle getStyle() {
        return style;
    }

    public void setStyle(BarStyle style) {
        this.style = style;

        if (bar != null) {
            bar.setStyle(style);
        } else {
            createBossBar();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public CAPITask getHideScheduler() {
        return hideScheduler;
    }

    public Integer getAuto() {
        return auto == null ? 20 : auto;
    }

    public void setAuto(Integer auto) {
        cancelAutoScheduler();
        this.auto = auto;
    }

    public CAPITask getAutoId() {
        return autoScheduler;
    }

    public void setAutoId(CAPITask cmiTask) {
        this.autoScheduler = cmiTask;
    }

    public List<String> getCommands() {
        return cmds;
    }

    public List<String> getCommands(Player player) {
        List<String> result = new ArrayList<>();
        if (cmds != null) {
            for (String cmd : cmds) {
                result.add(processPlaceholders(player, cmd));
            }
        }
        return result;
    }

    public void setCmds(List<String> cmds) {
        this.cmds = cmds;
    }

    public boolean stillRunning() {
        if (getKeepFor() < 0) return true;

        if (getPercentage() < 1 && getAdjustPerc() != null && getAdjustPerc() > 0) return true;
        if (getPercentage() > 0 && getAdjustPerc() != null && getAdjustPerc() < 0) return true;
        if (getPercentage() <= 0 && getAdjustPerc() != null && getAdjustPerc() < 0) return false;
        if (getPercentage() >= 1 && getAdjustPerc() != null && getAdjustPerc() > 0) return false;

        if (getAdjustPerc() == null && getKeepFor() > 0 && getStarted() > 0
                && System.currentTimeMillis() < getStarted() + (getKeepFor() * 50L)) {
            return true;
        }

        return getAdjustPerc() == null && getKeepFor() < 0
                || (getPercentage() != null && getPercentage() <= 0 && getAdjustPerc() != null && getAdjustPerc() < 0)
                || (getPercentage() != null && getPercentage() >= 1 && getAdjustPerc() != null && getAdjustPerc() > 0);
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isMakeVisible() {
        return makeVisible;
    }

    public void setMakeVisible(boolean makeVisible) {
        this.makeVisible = makeVisible;

        if (bar != null) {
            if (makeVisible) {
                bar.addPlayer(player);
                bar.setVisible(true);
            } else {
                bar.removePlayer(player);
                bar.setVisible(false);
            }
        } else if (makeVisible && player != null) {
            createBossBar();
        }
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public void setSeconds(int time) {
        double change = (100D / (time * 20D)) / 100D;
        setAdjustPerc(change);
        if (time < 0) setPercentage(1D);
        else setPercentage(0D);
        setAuto(1);
    }

    public boolean isWithPlaceholder() {
        return withPlaceholder;
    }

    public boolean isTranslateColors() {
        return translateColors;
    }

    public void setTranslateColors(boolean translateColors) {
        this.translateColors = translateColors;
    }

    public void updateCycle() {
        // Логика обновления цикла (можно расширить при необходимости)
    }

    public List<ChatColor> getColors() {
        return colors;
    }

    public void setColors(List<ChatColor> colors) {
        this.colors = colors;
    }

    public int getColorChangeIntervalTicks() {
        return colorChangeIntervalTicks;
    }

    public void setColorChangeIntervalTicks(int colorChangeIntervalTicks) {
        this.colorChangeIntervalTicks = colorChangeIntervalTicks;
    }

    public boolean timerRunOut() {
        if (getKeepFor() <= 0) return false;
        long elapsed = System.currentTimeMillis() - getStarted();
        return elapsed >= getKeepFor() * 50L;
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("%d", seconds);
        }
    }

    private String processPlaceholders(Player player, String text) {
        if (text == null) return "";
        String result = text;
        result = result.replace("%player%", player.getName());
        return result;
    }
}
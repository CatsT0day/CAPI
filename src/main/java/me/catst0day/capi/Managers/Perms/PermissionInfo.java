package me.catst0day.capi.Managers.Perms;

import java.util.HashSet;
import java.util.Set;

public class PermissionInfo {
    private String permission;
    private boolean enabled = false;
    private Long delay = 1000L;
    private Long lastChecked = null;
    private Double maxValue = null;
    private Double minValue = null;
    private Set<String> values = new HashSet();

    public PermissionInfo(String var1, Long var2) {
        this.permission = var1;
        if (var2 != null) {
            this.delay = var2;
        }

    }

    public boolean isTimeToRecalculate() {
        return this.lastChecked == null || this.delay + this.lastChecked < System.currentTimeMillis();
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String var1) {
        this.permission = var1;
    }

    public Long getDelay() {
        return this.delay;
    }

    public void setDelay(long var1) {
        this.delay = var1;
    }

    public Long getLastChecked() {
        if (this.lastChecked == null) {
            this.lastChecked = System.currentTimeMillis();
        }

        return this.lastChecked;
    }

    public void setLastChecked(long var1) {
        this.lastChecked = var1;
    }

    public Double getMaxValue() {
        return this.maxValue;
    }

    public Double getMaxValue(double var1) {
        return this.maxValue == null ? var1 : (this.maxValue > var1 ? this.maxValue : var1);
    }

    public int getMaxValue(int var1) {
        return this.maxValue == null ? var1 : (this.maxValue > (double)var1 ? this.maxValue.intValue() : var1);
    }

    public void setMaxValue(Double var1) {
        this.maxValue = var1;
    }

    public Double getMinValue() {
        return this.minValue;
    }

    public Double getMinValue(double var1) {
        return this.minValue == null ? var1 : (this.minValue < var1 ? this.minValue : var1);
    }

    public int getMinValue(int var1) {
        return this.minValue == null ? var1 : (this.minValue < (double)var1 ? this.minValue.intValue() : var1);
    }

    public void setMinValue(Double var1) {
        this.minValue = var1;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean var1) {
        this.enabled = var1;
    }

    public Set<String> getValues() {
        return this.values;
    }

    public void addValue(String var1) {
        this.values.add(var1);
    }
}

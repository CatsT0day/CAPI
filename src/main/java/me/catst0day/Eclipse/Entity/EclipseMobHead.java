package me.catst0day.Eclipse.Entity;

import java.util.ArrayList;
import java.util.List;

public class EclipseMobHead {

    private String customName = null;

    private List<EclipseEntitySubType> criterias = new ArrayList<EclipseEntitySubType>();

    private List<String> lore = new ArrayList<String>();

    public EclipseMobHead() {
    }

    public String getName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public List<EclipseEntitySubType> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<EclipseEntitySubType> criterias) {
        this.criterias = criterias;
    }

    public void addCriterias(EclipseEntitySubType criteria) {
        this.criterias.add(criteria);
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}

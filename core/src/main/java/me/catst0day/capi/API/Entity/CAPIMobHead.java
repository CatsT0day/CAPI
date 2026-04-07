package me.catst0day.capi.API.Entity;

import java.util.ArrayList;
import java.util.List;

public class CAPIMobHead {

    private String customName = null;

    private List<CAPIEntitySubType> criterias = new ArrayList<CAPIEntitySubType>();

    private List<String> lore = new ArrayList<String>();

    public CAPIMobHead() {
    }

    public String getName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public List<CAPIEntitySubType> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<CAPIEntitySubType> criterias) {
        this.criterias = criterias;
    }

    public void addCriterias(CAPIEntitySubType criteria) {
        this.criterias.add(criteria);
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}

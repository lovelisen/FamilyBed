package com.dywl.familybed.model;

import java.util.List;

public class TodaysMedicationJsonData {

    private List<TodaysMedicationJsonDrug> drug;
    private List<TodaysMedicationJsonUsed> used;

    public void setDrug(List<TodaysMedicationJsonDrug> drug) {
        this.drug = drug;
    }

    public List<TodaysMedicationJsonDrug> getDrug() {
        return drug;
    }

    public void setUsed(List<TodaysMedicationJsonUsed> used) {
        this.used = used;
    }

    public List<TodaysMedicationJsonUsed> getUsed() {
        return used;
    }
}
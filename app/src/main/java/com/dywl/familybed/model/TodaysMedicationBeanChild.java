package com.dywl.familybed.model;

public class TodaysMedicationBeanChild {

    // 药品名称
    private String drug;
    // 用法
    private String usage;
    // 频率：一天两次、一天三次
    private String frequency;
    // 剂量
    private String note;
    // 嘱托
    private String entrust;


    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {

        this.drug = drug;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEntrust() {
        return entrust;
    }

    public void setEntrust(String entrust) {

        if (!String.valueOf(entrust).isEmpty()) {
            if (String.valueOf(entrust).equals("null")) {
                this.entrust = "无";

            } else {
                this.entrust = entrust;
            }
        }
    }
}

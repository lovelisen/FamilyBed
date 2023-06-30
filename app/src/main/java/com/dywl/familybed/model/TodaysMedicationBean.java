package com.dywl.familybed.model;

import com.dywl.familybed.adapter.ListViewAdapter;

public class TodaysMedicationBean {
    // 日期
    private String todaysDate;
    // 时段
    private String period;
    private String used;

    private String ids;

    private String voices;


    public ListViewAdapter<TodaysMedicationBeanChild> getAdapter() {
        return adapter;
    }

    public void setAdapter(ListViewAdapter<TodaysMedicationBeanChild> adapter) {
        this.adapter = adapter;
    }

    private ListViewAdapter<TodaysMedicationBeanChild> adapter;


    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setUsed(String used) {
        this.used = used;
    }
    public String getUsed() {
        return used;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getVoices() {
        return voices;
    }

    public void setVoices(String voices) {
        this.voices = voices;
    }
}

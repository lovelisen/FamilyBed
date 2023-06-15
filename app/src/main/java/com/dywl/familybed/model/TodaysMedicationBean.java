package com.dywl.familybed.model;

import com.dywl.familybed.adapter.ListViewAdapter;

public class TodaysMedicationBean {
    // 日期
    private String todaysDate;
    // 时段
    private String period;


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


}

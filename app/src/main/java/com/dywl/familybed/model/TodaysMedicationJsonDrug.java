package com.dywl.familybed.model;

import java.util.Date;
/**
 * Auto-generated: 2023-06-15 18:2:25
 *
 * @author 大洋网络·李森
 */
public class TodaysMedicationJsonDrug {

    private String ID;
    private String BedID;
    private String PatientID;
    private String Drug;
    private String Usage;
    private String Frequency;
    private String Note;
    private String Jinji;
    private String Time;
    private String Entrust;
    private String CreateTime;
    private String Status;
    private String StartTime;
    private String EndTime;
    private String period;
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }

    public void setBedID(String BedID) {
        this.BedID = BedID;
    }
    public String getBedID() {
        return BedID;
    }

    public void setPatientID(String PatientID) {
        this.PatientID = PatientID;
    }
    public String getPatientID() {
        return PatientID;
    }

    public void setDrug(String Drug) {
        this.Drug = Drug;
    }
    public String getDrug() {
        return Drug;
    }

    public void setUsage(String Usage) {
        this.Usage = Usage;
    }
    public String getUsage() {
        return Usage;
    }

    public void setFrequency(String Frequency) {
        this.Frequency = Frequency;
    }
    public String getFrequency() {
        return Frequency;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }
    public String getNote() {
        return Note;
    }

    public void setJinji(String Jinji) {
        this.Jinji = Jinji;
    }
    public String getJinji() {
        return Jinji;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }
    public String getTime() {
        return Time;
    }

    public void setEntrust(String Entrust) {
        if (!String.valueOf(Entrust).isEmpty()) {
            if (String.valueOf(Entrust).equals("null")) {
                this.Entrust = "无";

            } else {
                this.Entrust = Entrust;
            }
        }
    }
    public String getEntrust() {
        return Entrust;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }
    public String getCreateTime() {
        return CreateTime;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
    public String getStatus() {
        return Status;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }
    public String getStartTime() {
        return StartTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }
    public String getEndTime() {
        return EndTime;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
    public String getPeriod() {
        return period;
    }

}

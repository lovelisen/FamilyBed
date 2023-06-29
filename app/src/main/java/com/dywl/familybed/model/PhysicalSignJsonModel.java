package com.dywl.familybed.model;

public class PhysicalSignJsonModel {

    private String ID;
    private String Name;
    private String Number;
    private String Status;
    private String Unit;
    private String Icon;
    private String Color;
    private String Abbr;
    private String CharName;
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public String getName() {
        return Name;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }
    public String getNumber() {
        return Number;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
    public String getStatus() {
        return Status;
    }

    public void setUnit(String Unit) {
        if (!String.valueOf(Unit).isEmpty()) {
            if (String.valueOf(Unit).equals("null")) {
                this.Unit = "";

            } else {
                this.Unit = Unit;
            }
        }
    }
    public String getUnit() {
        return Unit;
    }

    public void setIcon(String Icon) {
        this.Icon = Icon;
    }
    public String getIcon() {
        return Icon;
    }

    public void setColor(String Color) {
        this.Color = Color;
    }
    public String getColor() {
        return Color;
    }

    public void setAbbr(String Abbr) {
        this.Abbr = Abbr;
    }
    public String getAbbr() {
        return Abbr;
    }

    public void setCharName(String CharName) {
        this.CharName = CharName;
    }
    public String getCharName() {
        return CharName;
    }

}
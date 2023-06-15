package com.dywl.familybed.model;

import java.io.Serializable;

public class FamilyBedModelBean implements Serializable {

    private FamilyBedModel data;
    private String msg;
    private int code;
    public void setData(FamilyBedModel data) {
        this.data = data;
    }
    public FamilyBedModel getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

}

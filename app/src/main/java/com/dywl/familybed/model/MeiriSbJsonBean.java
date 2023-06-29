package com.dywl.familybed.model;

public class MeiriSbJsonBean {

    private MeiriSbJsonData data;
    private String msg;
    private int code;
    public void setData(MeiriSbJsonData data) {
        this.data = data;
    }
    public MeiriSbJsonData getData() {
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
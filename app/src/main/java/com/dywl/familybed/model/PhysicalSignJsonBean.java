package com.dywl.familybed.model;

import java.io.Serializable;

public class PhysicalSignJsonBean implements Serializable {

    /**
     * 数据
     */
    private PhysicalSignJsonData data;
    /**
     * 消息
     */
    private String msg;
    /**
     * 代码
     */
    private int code;

    public void setData(PhysicalSignJsonData data) {
        this.data = data;
    }

    public PhysicalSignJsonData getData() {
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
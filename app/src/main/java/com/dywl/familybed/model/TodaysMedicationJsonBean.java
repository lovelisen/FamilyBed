package com.dywl.familybed.model;

import java.io.Serializable;
import java.util.List;

/**
 * Auto-generated: 2023-06-15 18:2:25
 *
 * @author 大洋网络·李森
 */
public class TodaysMedicationJsonBean implements Serializable {
    private TodaysMedicationJsonData data;
    private String msg;
    private int code;
    public void setData(TodaysMedicationJsonData data) {
        this.data = data;
    }
    public TodaysMedicationJsonData getData() {
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
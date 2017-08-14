package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;

/**
 * created by ：ycy on 2017/3/19.
 * email 1490258886@qq.com
 */

public class GroupChannel implements Serializable {
    private String type;
    private String unit;
    private float value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}

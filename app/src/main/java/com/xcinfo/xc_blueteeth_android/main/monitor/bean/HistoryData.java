package com.xcinfo.xc_blueteeth_android.main.monitor.bean;

import java.io.Serializable;

/**
 * created by ：ycy on 2017/3/22.
 * email 1490258886@qq.com
 * */


public class HistoryData implements Serializable {
    private float maxValue;
    private float minValue;
    private float coordinateAxis;
    private float value;
    private String time;

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getCoordinateAxis() {
        return coordinateAxis;
    }

    public void setCoordinateAxis(float coordinateAxis) {
        this.coordinateAxis = coordinateAxis;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

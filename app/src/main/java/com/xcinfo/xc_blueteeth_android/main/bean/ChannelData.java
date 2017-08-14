package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;

/**
 * created by ：ycy on 2017/3/14.
 * email 1490258886@qq.com
 */

public class ChannelData implements Serializable {

    private float CHL1_value;
    private String CHL1_unit;
    private float CHL2_value;
    private String CHL2_unit;
    private float CHL3_value;
    private String CHL3_unit;
    private float CHL4_value;
    private String CHL4_unit;

    private float maxLimit;
    private float minLimit;
    private String time;

    public String getCHL1_unit() {
        return CHL1_unit;
    }

    public void setCHL1_unit(String CHL1_unit) {
        this.CHL1_unit = CHL1_unit;
    }

    public String getCHL2_unit() {
        return CHL2_unit;
    }

    public void setCHL2_unit(String CHL2_unit) {
        this.CHL2_unit = CHL2_unit;
    }

    public String getCHL3_unit() {
        return CHL3_unit;
    }

    public void setCHL3_unit(String CHL3_unit) {
        this.CHL3_unit = CHL3_unit;
    }

    public String getCHL4_unit() {
        return CHL4_unit;
    }

    public void setCHL4_unit(String CHL4_unit) {
        this.CHL4_unit = CHL4_unit;
    }

    private int alarmType;
    private int saveType;

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getSaveType() {
        return saveType;
    }

    public void setSaveType(int saveType) {
        this.saveType = saveType;
    }

    public float getCHL1_value() {
        return CHL1_value;
    }

    public void setCHL1_value(float CHL1_value) {
        this.CHL1_value = CHL1_value;
    }

    public float getCHL2_value() {
        return CHL2_value;
    }

    public void setCHL2_value(float CHL2_value) {
        this.CHL2_value = CHL2_value;
    }

    public float getCHL3_value() {
        return CHL3_value;
    }

    public void setCHL3_value(float CHL3_value) {
        this.CHL3_value = CHL3_value;
    }

    public float getCHL4_value() {
        return CHL4_value;
    }

    public void setCHL4_value(float CHL4_value) {
        this.CHL4_value = CHL4_value;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(float maxLimit) {
        this.maxLimit = maxLimit;
    }

    public float getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(float minLimit) {
        this.minLimit = minLimit;
    }
}

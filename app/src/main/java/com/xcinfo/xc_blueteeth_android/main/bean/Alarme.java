package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;

/**
 * created by ：ycy on 2017/3/14.
 * email 1490258886@qq.com
 */

public class Alarme implements Serializable {
    private String device_manager_name;
    private String deviceSerial;//设备的序列号
    private float CHL1_maxLimit;
    private float CHL1_minLimit;
    private float CHL1_singleLimit;

    private float CHL2_maxLimit;
    private float CHL2_minLimit;
    private float CHL2_singleLimit;

    private float CHL3_maxLimit;
    private float CHL3_minLimit;
    private float CHL3_singleLimit;

    private float CHL4_maxLimit;
    private float CHL4_minLimit;
    private float CHL4_singleLimit;
    private int wayOfWaring;//报警方式
    private int alarmType;
    private int savetype;

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getSavetype() {
        return savetype;
    }

    public void setSavetype(int savetype) {
        this.savetype = savetype;
    }

    public int getWayOfWaring() {
        return wayOfWaring;
    }

    public void setWayOfWaring(int wayOfWaring) {
        this.wayOfWaring = wayOfWaring;
    }

    public String getDevice_manager_name() {
        return device_manager_name;
    }

    public void setDevice_manager_name(String device_manager_name) {
        this.device_manager_name = device_manager_name;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public float getCHL1_maxLimit() {
        return CHL1_maxLimit;
    }

    public void setCHL1_maxLimit(float CHL1_maxLimit) {
        this.CHL1_maxLimit = CHL1_maxLimit;
    }

    public float getCHL1_minLimit() {
        return CHL1_minLimit;
    }

    public void setCHL1_minLimit(float CHL1_minLimit) {
        this.CHL1_minLimit = CHL1_minLimit;
    }

    public float getCHL1_singleLimit() {
        return CHL1_singleLimit;
    }

    public void setCHL1_singleLimit(float CHL1_singleLimit) {
        this.CHL1_singleLimit = CHL1_singleLimit;
    }

    public float getCHL2_maxLimit() {
        return CHL2_maxLimit;
    }

    public void setCHL2_maxLimit(float CHL2_maxLimit) {
        this.CHL2_maxLimit = CHL2_maxLimit;
    }

    public float getCHL2_minLimit() {
        return CHL2_minLimit;
    }

    public void setCHL2_minLimit(float CHL2_minLimit) {
        this.CHL2_minLimit = CHL2_minLimit;
    }

    public float getCHL2_singleLimit() {
        return CHL2_singleLimit;
    }

    public void setCHL2_singleLimit(float CHL2_singleLimit) {
        this.CHL2_singleLimit = CHL2_singleLimit;
    }

    public float getCHL3_maxLimit() {
        return CHL3_maxLimit;
    }

    public void setCHL3_maxLimit(float CHL3_maxLimit) {
        this.CHL3_maxLimit = CHL3_maxLimit;
    }

    public float getCHL3_minLimit() {
        return CHL3_minLimit;
    }

    public void setCHL3_minLimit(float CHL3_minLimit) {
        this.CHL3_minLimit = CHL3_minLimit;
    }

    public float getCHL3_singleLimit() {
        return CHL3_singleLimit;
    }

    public void setCHL3_singleLimit(float CHL3_singleLimit) {
        this.CHL3_singleLimit = CHL3_singleLimit;
    }

    public float getCHL4_maxLimit() {
        return CHL4_maxLimit;
    }

    public void setCHL4_maxLimit(float CHL4_maxLimit) {
        this.CHL4_maxLimit = CHL4_maxLimit;
    }

    public float getCHL4_minLimit() {
        return CHL4_minLimit;
    }

    public void setCHL4_minLimit(float CHL4_minLimit) {
        this.CHL4_minLimit = CHL4_minLimit;
    }

    public float getCHL4_singleLimit() {
        return CHL4_singleLimit;
    }

    public void setCHL4_singleLimit(float CHL4_singleLimit) {
        this.CHL4_singleLimit = CHL4_singleLimit;
    }
}



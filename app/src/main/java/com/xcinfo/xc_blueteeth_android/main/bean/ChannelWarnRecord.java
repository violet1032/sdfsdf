package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;

/**
 * created by ：ycy on 2017/3/23.
 * email 1490258886@qq.com
 */

public class ChannelWarnRecord implements Serializable{
    private String deviceSerial;//设备序列号
    private String deviceName;//设备名称

    public String getDeviceManngerName() {
        return deviceManngerName;
    }

    public void setDeviceManngerName(String deviceManngerName) {
        this.deviceManngerName = deviceManngerName;
    }

    private String deviceManngerName;//
    private int ChannelId;//通道的Id
    private String channelType;//通道类型
    private int occurCount;//发生次数
    private String recordTime;//记录时间
    private String warningInfo;//警报信息
    private int state;//0是已经处理  1是待处理
    private float realValue;//报警时的值
    private float maxLimit;//最大值限制
    private float minLimit;//最小值限制

    public float getRealValue() {
        return realValue;
    }

    public void setRealValue(float realValue) {
        this.realValue = realValue;
    }

    public float getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(float minLimit) {
        this.minLimit = minLimit;
    }

    public float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(float maxLimit) {
        this.maxLimit = maxLimit;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public int getChannelId() {
        return ChannelId;
    }

    public void setChannelId(int channelId) {
        ChannelId = channelId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public int getOccurCount() {
        return occurCount;
    }

    public void setOccurCount(int occurCount) {
        this.occurCount = occurCount;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getWarningInfo() {
        return warningInfo;
    }

    public void setWarningInfo(String warningInfo) {
        this.warningInfo = warningInfo;
    }
}

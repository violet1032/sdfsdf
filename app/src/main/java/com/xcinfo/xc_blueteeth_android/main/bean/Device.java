package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by ：ycy on 2017/3/14.
 * email 1490258886@qq.com
 */

public class Device implements Serializable {
    private String deviceName;//设备（传感器）本身的名字
    private String device_manager_name;//上层蓝牙硬件设备的名字
    private int deviceId;//设备的Id
    private String location;//设备的地理位置
    private String lastUpLoadTime;//上一次上传的时间
    private String deviceSerial;//设备的序列号 设备的唯一身份标识
    private int toatlChannelCount;//设备下面通道的个数
    private int channelType;//通道类别，1为温度，2为温湿度

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    private int deviceState;//设备状态  0正常；1报警；2异常；3离线。
    private String belongGroup;//所属分组
    private List<GroupChannel> groupChannels;//通道信息

    private String time;//实时记录的第一条的时间

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private int CHL1_ID;//通道1的ID
    private float CHL1_current;//通道1当前的值   -0表示没有值
    private String CHL1_type;//通道1类型
    private String CHL1_unit;//通道1单位

    private int CHL2_ID;
    private float CHL2_current;//通道2当前的值
    private String CHL2_type;
    private String CHL2_unit;

    private int CHL3_ID;
    private float CHL3_current;//通道3当前的值
    private String CHL3_type;
    private String CHL3_unit;

    private int CHL4_ID;
    private float CHL4_current;//通道4当前的值
    private String CHL4_type;
    private String CHL4_unit;

    public String getBelongGroup() {
        return belongGroup;
    }

    public void setBelongGroup(String belongGroup) {
        this.belongGroup = belongGroup;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public void setDevice_manager_name(String device_manager_name) {
        this.device_manager_name = device_manager_name;
    }



    public void setLastUpLoadTime(String lastUpLoadTime) {
        this.lastUpLoadTime = lastUpLoadTime;
    }



    public String getDeviceName() {
        return deviceName;
    }

    public String getDevice_manager_name() {
        return device_manager_name;
    }


    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public void setCHL1_ID(int CHL1_ID) {
        this.CHL1_ID = CHL1_ID;
    }

    public void setCHL1_type(String CHL1_type) {
        this.CHL1_type = CHL1_type;
    }

    public void setCHL1_unit(String CHL1_unit) {
        this.CHL1_unit = CHL1_unit;
    }

    public void setCHL2_ID(int CHL2_ID) {
        this.CHL2_ID = CHL2_ID;
    }

    public void setCHL2_type(String CHL2_type) {
        this.CHL2_type = CHL2_type;
    }

    public void setCHL2_unit(String CHL2_unit) {
        this.CHL2_unit = CHL2_unit;
    }

    public void setCHL3_ID(int CHL3_ID) {
        this.CHL3_ID = CHL3_ID;
    }

    public void setCHL3_type(String CHL3_type) {
        this.CHL3_type = CHL3_type;
    }

    public void setCHL3_unit(String CHL3_unit) {
        this.CHL3_unit = CHL3_unit;
    }

    public void setCHL4_ID(int CHL4_ID) {
        this.CHL4_ID = CHL4_ID;
    }

    public void setCHL4_type(String CHL4_type) {
        this.CHL4_type = CHL4_type;
    }

    public void setCHL4_unit(String CHL4_unit) {
        this.CHL4_unit = CHL4_unit;
    }

    public void setToatlChannelCount(int toatlChannelCount) {
        this.toatlChannelCount = toatlChannelCount;
    }

    public int getToatlChannelCount() {
        return toatlChannelCount;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getLastUpLoadTime() {
        return lastUpLoadTime;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public int getCHL1_ID() {
        return CHL1_ID;
    }

    public String getCHL1_type() {
        return CHL1_type;
    }

    public String getCHL1_unit() {
        return CHL1_unit;
    }

    public int getCHL2_ID() {
        return CHL2_ID;
    }

    public String getCHL2_type() {
        return CHL2_type;
    }

    public String getCHL2_unit() {
        return CHL2_unit;
    }

    public int getCHL3_ID() {
        return CHL3_ID;
    }

    public String getCHL3_type() {
        return CHL3_type;
    }

    public String getCHL3_unit() {
        return CHL3_unit;
    }

    public int getCHL4_ID() {
        return CHL4_ID;
    }

    public String getCHL4_type() {
        return CHL4_type;
    }

    public String getCHL4_unit() {
        return CHL4_unit;
    }

    public float getCHL1_current() {
        return CHL1_current;
    }

    public void setCHL1_current(float CHL1_current) {
        this.CHL1_current = CHL1_current;
    }

    public float getCHL2_current() {
        return CHL2_current;
    }

    public void setCHL2_current(float CHL2_current) {
        this.CHL2_current = CHL2_current;
    }

    public float getCHL3_current() {
        return CHL3_current;
    }

    public void setCHL3_current(float CHL3_current) {
        this.CHL3_current = CHL3_current;
    }

    public float getCHL4_current() {
        return CHL4_current;
    }

    public void setCHL4_current(float CHL4_current) {
        this.CHL4_current = CHL4_current;
    }

    public List<GroupChannel> getGroupChannels() {
        return groupChannels;
    }

    public void setGroupChannels(List<GroupChannel> groupChannels) {
        this.groupChannels = groupChannels;
    }
}

package com.xcinfo.xc_blueteeth_android.main.bean;

import java.io.Serializable;
import java.util.List;

/**
 * created by ：ycy on 2017/3/17.
 * email 1490258886@qq.com
 */

public class Group implements Serializable {
    private String groupName;//分组名称
    private int deviceCount;//设备数目
    private List<Device> deviceList;

    public String getGroupName() {
        return groupName;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}

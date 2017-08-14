package com.xcinfo.xc_blueteeth_android.common.bluetooth.service;

/**
 * Service与activity通信接口
 * Created by com.亚东 on 2017/4/19.
 */

public interface BindHelper  {
    BlueToothCommuicationService.BluetoothBinder getBinder();
    void connectFailed();//连接失败
    void connectSuccess();//连接成功
    void notInitialize();
    void getBaseDataFinish();//建表完成
}

package com.xcinfo.xc_blueteeth_android.servicetest;

/**
 * Created by com.亚东 on 2017/4/10.
 */

public interface ConnectHelper {
    void connectFailed();
    void connectSuccess();
    void notInitialize();
    void showInformation(String information);
}

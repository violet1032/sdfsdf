package com.xcinfo.xc_blueteeth_android.main.monitor.activity;

/**
 * 设置界面的回调
 * Created by com.亚东 on 2017/4/26.
 */

public interface SettingHelper {
    void setTime(boolean isSuccess);//同步时间成功
    void getPowerSuccess(int power);//同步时间失败
    void getPowerFailed();//获取电量失败
    void setAlarmType(boolean isSuccess);//设置报警方式
    void getMtkSuccess(int mtk);//获取MTK成功
    void getMtkFailed();//获取MTK失败
    void setSaveTypeSuccess();//设置存储满后的处理成功
    void setSaveTypeFailed();//设置存储满后的处理失败
    void setIntervalSuccess();//设置间隔成功
    void setINtervalFailed();//设置间隔失败
    void setTempertureLimitSuccess();//设置报警上下限成功
    void setTempertureLimitFailed();//设置报警上下限失败
    void setShowWayFailed();//设置显示方式失败
    void setShowWaySuccess();//设置显示方式成功
}

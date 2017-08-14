package com.xcinfo.xc_blueteeth_android.main.monitor.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.EditGroupActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

/**
 * created by ：ycy on 2017/3/25.
 * email 1490258886@qq.com
 */

public class DeviceManagerUtil {

    public static String getDeviceManagerName(Context context){
        return (String) SPUtils.get(context, Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1");
    }

    public static void setDeviceManagerName(Context context,String name){
        SPUtils.put(context, Constant.DEVICE_MANAGER_NAME_KEY,name);
    }
    //是否震动
    public static boolean isVirbrator(Context context){
        return (boolean) SPUtils.get(context,Constant.VIBRATOR_KEY,false);
    }
    public static void putVibrator(Context context,boolean isVibrator){
        SPUtils.put(context,Constant.VIBRATOR_KEY,isVibrator);
    }

    //事都响铃
    public static boolean isRing(Context context){
        return (boolean) SPUtils.get(context,Constant.RING_DOWN,false);
    }
    public static void putRing(Context context,boolean isRing){
        SPUtils.put(context,Constant.RING_DOWN,isRing);
    }

    //发送短信
    public static boolean isSendSMS(Context context){
        return (boolean) SPUtils.get(context,Constant.SEND_SMS,false);
    }
    public static void putSMS(Context context,boolean isSendSMS){
        SPUtils.put(context,Constant.SEND_SMS,isSendSMS);
    }

    //短信手机号
    public static void putTel(Context context,String tel){
        SPUtils.put(context,Constant.TEL_NUMBER,tel);
    }
    public static String  getTel(Context context){
        return (String) SPUtils.get(context,Constant.TEL_NUMBER,"18081241489");
    }

    //自动上传
    public static void putAutouploading(Context context,boolean isautoloading){
        SPUtils.put(context,Constant.AUTO_UPLOADING,isautoloading);
    }
    public static boolean isAutouploading(Context context){
        return (boolean) SPUtils.get(context,Constant.AUTO_UPLOADING,false);
    }

    //固件版本号
    public static void putVision(Context context,int vision){
         SPUtils.put(context,Constant.BLUETOOTH_VISION,vision);
    }
    public static int getVision(Context context){
        return (int) SPUtils.get(context,Constant.BLUETOOTH_VISION,1);
    }

    //华氏度显示
    public static boolean getIsFahrenheit(Context context){
        return (boolean) SPUtils.get(context,Constant.IS_FAHRENHEIT,false);
    }

    public static void putIsFahrenheit(Context context,boolean isFahrenheit){
        SPUtils.put(context,Constant.IS_FAHRENHEIT,isFahrenheit);
    }


}

package com.xcinfo.xc_blueteeth_android.common.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.xcinfo.widget.CustomDailog.CustomDialog;

/**
 * Created by com.亚东 on 2017/3/4.
 */

public class DialogUtil {


    public static void connectFail(Context context, DialogInterface.OnClickListener onClickListener){
        CustomDialog.Builder dialog=new CustomDialog.Builder(context);
        dialog.setTitle("连接失败")
                .setMessage("请检查是否连接了正确的设备")
                .setShowNotice(false)
                .setPositiveButton("确定",onClickListener)
                .create().show();
    }

    //处理报警
    public static void updateWarnRecord(Context context, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onNeClickListener){
        CustomDialog.Builder dialog=new CustomDialog.Builder(context);
        dialog.setTitle("是否处理该条报警？")
                //.setMessage("请检查是否连接了正确的设备")
                .setShowNotice(false)
                .setPositiveButton("确定",onClickListener)
                .setNegativeButton("取消",onNeClickListener)
                .create().show();
    }

    //连接断开
    public static void disConnected(Context context, DialogInterface.OnClickListener onClickListener){
        CustomDialog.Builder dialog=new CustomDialog.Builder(context);
        dialog.setTitle("连接失败")
                .setMessage("请重新连接设备")
                .setShowNotice(false)
                .setPositiveButton("确定",onClickListener)
                .create().show();
    }

    public static void exitApp(Context context, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onNeClickListener){
            CustomDialog.Builder dialog=new CustomDialog.Builder(context);
            dialog.setTitle("退出程序")
                    .setShowNotice(true)

                .setPositiveButton("确定",onClickListener);
            dialog.setNegativeButton("取消",onNeClickListener)
                .create().show();
    }


}

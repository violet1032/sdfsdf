package com.xcinfo.xc_blueteeth_android.common.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.activity.MainActivity;

/**
 * Created by com.亚东 on 2017/5/6.
 */

public class NotificationUtils {
    public static void getWarnRecord(Context context,String deviceName,int recordNum,int deviceSerail){
        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(context);
        Intent intent=new Intent(context, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
       // builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentTitle("新的报警记录");
        builder.setContentText("设备："+deviceName+"  条数："+recordNum);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker("您有新的报警信息");
        builder.setOngoing(false);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
       // builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSound(Uri.parse("android.resource://"
                + context.getPackageName() + "/" +R.raw.warn_dound));
        Notification notification=builder.build();

        manager.notify(deviceSerail,notification);
    }
}

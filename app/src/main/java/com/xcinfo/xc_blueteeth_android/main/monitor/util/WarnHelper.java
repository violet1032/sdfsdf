package com.xcinfo.xc_blueteeth_android.main.monitor.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.NotificationUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Alarme;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.bean.Exception;

import java.util.List;
import java.util.Random;

/**
 * created by ：ycy on 2017/3/28.
 * email 1490258886@qq.com
 */

public class WarnHelper {

    //震动
    private static void doVibrator(Context context){
        if(DeviceManagerUtil.isVirbrator(context))
        {
            final Vibrator vibrator;
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            long [] pattern = {200,600,200,600};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern,1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1400);
                        if(vibrator!=null){
                            vibrator.cancel();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }
    //响铃
    private static void doRing(Context context){
        if(DeviceManagerUtil.isRing(context)){
            NotificationManager mgr = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification nt = new Notification();
            nt.defaults = Notification.DEFAULT_SOUND;
            int soundId = new Random(System.currentTimeMillis())
                    .nextInt(Integer.MAX_VALUE);
            mgr.notify(soundId, nt);
        }
    }
    //发送短信
    private static void sendSMS(Context context,String content){
        if(DeviceManagerUtil.isSendSMS(context)&&DeviceManagerUtil.getTel(context)!=null){
            SmsManager smsManager=SmsManager.getDefault();
            PendingIntent pi=PendingIntent.getActivity(context,0,new Intent(),0);
            smsManager.sendTextMessage(DeviceManagerUtil.getTel(context).toString(),null,content,pi,null);
        }
    }

    public static void isWarn(Context context,String deviceSerial,int channelId,Device device){

        Log.d("@@isWarn","  serial:"+deviceSerial+"  id:"+channelId);
        Exception exception=SqliteUtil.getInstance(context).getException(deviceSerial);
        List<ChannelWarnRecord> records=SqliteUtil.getInstance(context).getChannelWarnRecord(deviceSerial,channelId,device);
        //存储报警数据
        for (ChannelWarnRecord record:records){
            SqliteUtil.getInstance(context).saveWarnRecord(device.getDevice_manager_name(),
                    deviceSerial,record,channelId);
        }

        //是否通知报警
        if(records.size()>0){
            if(TimeUtil.getTimeBad(records.get(records.size()-1).getRecordTime())<5*60*1000){
                Log.d("@@warn","报警小于五分钟");
                ChannelWarnRecord record=records.get(records.size()-1);
                NotificationUtils.getWarnRecord(context,record.getDeviceName(),records.size(), Integer.parseInt(record.getDeviceSerial()));
                sendSMS(context,"您的设备："+device.getDeviceName()+"最近五分钟有报警记录"+records.size()+"条请及时处理。");
            }

            //判断异常
            for (ChannelWarnRecord record:records){
                if (record.getRealValue()>exception.getMaxRange()|record.getRealValue()<exception.getMinRange()){
                    Log.d("@@设备异常", ""+record.getRealValue());
                    SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,3);
                    break;
                }
            }
        }


        List<ChannelWarnRecord> records1=SqliteUtil.getInstance(context).getWarnRecord(device);
        if (records1.size()>0){
            SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,1);



        }else {
            SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,0);
        }

//        if(records.size()>0){
//            NotificationUtils.getWarnRecord(context,records.get(0).getDeviceName(),records.size(), Integer.parseInt(records.get(0).getDeviceSerial()));
//                        if(TimeUtil.getTimeBad(records.get(records.size()-1).getRecordTime())<5*60*1000){
//                                Log.d("@@warn","报警小于五分钟");
//                                //设备状态改为报警
//                                SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,1);
//                                sendSMS(context,"您的设备："+device.getDeviceName()+"最近五分钟有报警记录"+records.size()+"条请及时处理。");
//
//                        }else {
//                                //如果该设备有未处理的报警记录，则改为异常
//                                SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,2);
//                            Log.d("@@warn","else1");
//                        }
//                    }else {
//            if(SqliteUtil.getInstance(context).getWarnRecord(device).size()>0){
//                //如果该设备有未处理的报警记录，则改为异常
//                SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,2);
//                Log.d("@@warn","else2");
//            }else {
//                //如果该设备有未处理的报警记录，则改为异常
//                SqliteUtil.getInstance(context).upDateDeviceStateBySerial(deviceSerial,0);
//                Log.d("@@warn","else3");
//            }
//        }



//        switch (channelId){
//            case 0:

//                if(device.getCHL1_current()>channelData.getMaxLimit()&&channelData.getMaxLimit()!=0.0){
//                    //Log.e("tag++","note2");
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL1_type()!=null){
//                        record.setChannelType(device.getCHL1_type());
//                    }
//                    record.setChannelId(1);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL1_type()!=null){
//                        sb.append(device.getCHL1_type());
//                    }
//                    sb.append("当前值:"+device.getCHL1_current());
//                    sb.append(",大于了您设定的上限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                            doRing(context);
//                            sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                if(device.getCHL1_current()<channelData.getMinLimit()&&channelData.getMinLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL1_type()!=null){
//                        record.setChannelType(device.getCHL1_type());
//                    }
//                    record.setChannelId(1);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL1_type()!=null){
//                        sb.append(device.getCHL1_type());
//                    }
//                    sb.append("当前值:"+device.getCHL1_current());
//                    sb.append(",小于了您设定的下限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                break;
//            case 1:
//                if(device.getCHL2_current()>channelData.getMaxLimit()&&channelData.getMaxLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL2_type()!=null){
//                        record.setChannelType(device.getCHL1_type());
//                    }
//                    record.setChannelId(2);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL2_type()!=null){
//                        sb.append(device.getCHL1_type());
//                    }
//                    sb.append("当前值:"+device.getCHL2_current());
//                    sb.append(",大于了您设定的上限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                if(device.getCHL2_current()<channelData.getMinLimit()&&channelData.getMinLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL2_type()!=null){
//                        record.setChannelType(device.getCHL2_type());
//                    }
//                    record.setChannelId(2);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL2_type()!=null){
//                        sb.append(device.getCHL2_type());
//                    }
//                    sb.append("当前值:"+device.getCHL2_current());
//                    sb.append(",小于了您设定的下限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                break;
//            case 2:
//                if(device.getCHL3_current()>channelData.getMaxLimit()&&channelData.getMaxLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL3_type()!=null){
//                        record.setChannelType(device.getCHL3_type());
//                    }
//                    record.setChannelId(3);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL3_type()!=null){
//                        sb.append(device.getCHL3_type());
//                    }
//                    sb.append("当前值:"+device.getCHL3_current());
//                    sb.append(",大于了您设定的上限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                if(device.getCHL3_current()<channelData.getMinLimit()&&channelData.getMinLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL3_type()!=null){
//                        record.setChannelType(device.getCHL3_type());
//                    }
//                    record.setChannelId(1);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL3_type()!=null){
//                        sb.append(device.getCHL3_type());
//                    }
//                    sb.append("当前值:"+device.getCHL3_current());
//                    sb.append(",小于了您设定的下限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                break;
//            case 3:
//                if(device.getCHL4_current()>channelData.getMaxLimit()&&channelData.getMaxLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL4_type()!=null){
//                        record.setChannelType(device.getCHL4_type());
//                    }
//                    record.setChannelId(4);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL1_type()!=null){
//                        sb.append(device.getCHL1_type());
//                    }
//                    sb.append("当前值:"+device.getCHL1_current());
//                    sb.append(",大于了您设定的上限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                if(device.getCHL1_current()<channelData.getMinLimit()&&channelData.getMinLimit()!=0.0){
//                    ChannelWarnRecord record=new ChannelWarnRecord();
//                    record.setDeviceSerial(deviceSerial);
//                    if(device.getCHL4_type()!=null){
//                        record.setChannelType(device.getCHL4_type());
//                    }
//                    record.setChannelId(4);
//                    int currenCount=SqliteUtil.getInstance(context).getWarnRecordCount(deviceSerial,channelId)+1;
//                    record.setOccurCount(currenCount);
//                    record.setRecordTime(time);
//                    StringBuilder sb=new StringBuilder();
//                    sb.append(deviceSerial);
//                    sb.append(",通道:");
//                    if(device.getCHL4_type()!=null){
//                        sb.append(device.getCHL4_type());
//                    }
//                    sb.append("当前值:"+device.getCHL4_current());
//                    sb.append(",小于了您设定的下限值:");
//                    sb.append(channelData.getMaxLimit());
//                    sb.append("请及时查看");
//                    record.setWarningInfo(sb.toString());
//                    if(DeviceManagerUtil.isVirbrator(context)){
//                        if(TimeUtil.getTimeBad(time)<5*60*1000)//小于5分钟才会振动
//                            doVibrator(context);//振动
//                        doRing(context);
//                        sendSMS(context,sb.toString());
//                    }
//                    SqliteUtil.getInstance(context).saveWarnRecord(DeviceManagerUtil.getDeviceManagerName(context),deviceSerial,record,channelId);
//                }
//                break;

 //       }


    }



}

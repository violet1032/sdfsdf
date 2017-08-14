package com.xcinfo.xc_blueteeth_android.common.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ByteUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ProtocolUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.QppApi;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.iQppCallback;
import com.xcinfo.xc_blueteeth_android.common.config.AppContext;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.NetUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.NotificationUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.main.bean.Alarme;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.bean.Exception;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.*;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.WarnHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlueToothCommuicationService extends Service {
    List<Device> devices = new ArrayList<>();
    int deviceNums = 0;
    int requestCount=0;
    private String bindTag = "";
    private BluetoothBinder mBinder = new BluetoothBinder();
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    boolean mConnected = false;//连接状态
    boolean isInitialize = false;//服务初始化是否成功
    boolean isWaitupdate = false;//是否处于等待更新应答
    private BluetoothDevice mDevice;//当前连接的device
    private static BluetoothDevice hasConnectedDevice = null;//已经连接的device
    private ProtocolUtil mProtocolUtil;//发送命令工具类
    private SqliteUtil sqliteUtil;//数据库工具
    private BindHelper helper;//连接界面的回调
    private com.xcinfo.xc_blueteeth_android.main.monitor.activity.SettingHelper settingHelper;//设置界面的回调

    public void setSettingHelper(com.xcinfo.xc_blueteeth_android.main.monitor.activity.SettingHelper settingHelper) {
        this.settingHelper = settingHelper;
    }

    public static BluetoothDevice getHasConnectedDevice() {
        return hasConnectedDevice;
    }

    protected static String uuidQppService = "0000fee9-0000-1000-8000-00805f9b34fb";
    protected static String uuidQppCharWrite = "d44bc439-abfd-45a2-b575-925416129600";

    public static String BLE_DISCONNECTED="com.BlueToothCommuicationService.disConnected";
    public static String BLE_REFRESHUI="com.BlueToothCommuicationService.refreshUI";//检查报警后刷新UI

    private static final int CONNECTSUCESS = 1;
    private static final int CONNECTFAILED = 2;
    private static final int GETDATAFINISH = 3;
    Handler helpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (helper!=null){
                switch (msg.what) {
                    case CONNECTSUCESS:
                        helper.connectSuccess();
                        break;
                    case CONNECTFAILED:
                        helper.connectFailed();
                        break;
                    case GETDATAFINISH:
                        helper.getBaseDataFinish();
                        break;
                }
            }
        }
    };

    Handler historyHandler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            //自动请求历史数据
            mBinder.getHistoryInformation();
        }
    };

    Handler updateHanlder=new Handler();
    Runnable updateRunnable=new Runnable() {
        @Override
        public void run() {
            if (isWaitupdate){
                //超时重新发送
                mBinder.sendBinFiles();
            }
        }
    };


    private static final int SETTIMESUCCESS = 4;
    private static final int SETTIMEFAILED = 5;
    private static final int GETPOWERSUCCESS = 6;
    private static final int GETPOWERFAILED = 7;
    private static final int SETALARMTYPESUCCESS = 8;
    private static final int SETALARMTYPEFAILED = 9;
    private static final int GETMTKSUCCESS = 10;
    private static final int GETMTKFAILED = 11;
    private static final int SETSAVETYPESUCCESS = 12;
    private static final int SETSAVETYPEFAILED = 13;
    private static final int SETINTERVALSUCCESS = 14;
    private static final int SETINTERVALFAILED = 15;
    private static final int SETTEMPERATUREDISPLAYSUCCESS = 16;
    private static final int SETTEMPERATUREDISPLAYFAILED = 17;
    private static final int SETTEMPERATURELIMITSUCCESS = 18;
    private static final int SETTEMPERATURELIMITFAILED = 19;
    Handler settingHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SETTIMESUCCESS:
                    settingHelper.setTime(true);
                    break;
                case SETTIMEFAILED:
                    settingHelper.setTime(false);
                    break;
                case GETPOWERSUCCESS:
                    settingHelper.getPowerSuccess(msg.arg1);
                    break;
                case GETPOWERFAILED:
                    settingHelper.getPowerFailed();
                    break;
                case SETALARMTYPESUCCESS:
                    settingHelper.setAlarmType(true);
                    break;
                case SETALARMTYPEFAILED:
                    settingHelper.setAlarmType(false);
                    break;
                case GETMTKSUCCESS:
                    settingHelper.getMtkSuccess(msg.arg1);
                    break;
                case GETMTKFAILED:
                    settingHelper.getMtkFailed();
                    break;
                case SETSAVETYPESUCCESS:
                    settingHelper.setSaveTypeSuccess();
                    break;
                case SETSAVETYPEFAILED:
                    settingHelper.setSaveTypeFailed();
                    break;
                case SETINTERVALSUCCESS:
                    settingHelper.setIntervalSuccess();
                    break;
                case SETINTERVALFAILED:
                    settingHelper.setINtervalFailed();
                    break;
                case SETTEMPERATUREDISPLAYSUCCESS:
                    settingHelper.setShowWaySuccess();
                    break;
                case SETTEMPERATUREDISPLAYFAILED:
                    settingHelper.setShowWayFailed();
                    break;
                case SETTEMPERATURELIMITSUCCESS:
                    settingHelper.setTempertureLimitSuccess();
                    break;
                case SETTEMPERATURELIMITFAILED:
                    settingHelper.setTempertureLimitFailed();
                    break;
            }
        }
    };
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("@@@@@@@Service", "onConnectionStateChange : " + status + "  newState : " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
                mConnected = true;
                Log.d("@@@@@@@Service", "连接：成功");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                mConnected = false;
                hasConnectedDevice = null;
                mDevice = null;
                Log.d("@@@@@@@Service", "连接：失败");
                helpHandler.sendEmptyMessage(CONNECTFAILED);
                devices.clear();
                //发送广播
                Intent intent=new Intent();
                intent.setAction(BLE_DISCONNECTED);
                sendBroadcast(intent);

                close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (QppApi.qppEnable(mBluetoothGatt, uuidQppService, uuidQppCharWrite)) {
                isInitialize = true;
                hasConnectedDevice = mDevice;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //成功后存储蓝牙名
                        DeviceManager manager = new DeviceManager();
                        manager.setName(mDevice.getName());
                        sqliteUtil.saveDeviceManager(manager);
                    }
                }).start();


                Log.d("@@@@@@@Service", "初始化成功");
                helpHandler.sendEmptyMessage(CONNECTSUCESS);

            } else {
                isInitialize = false;
                helpHandler.sendEmptyMessage(CONNECTFAILED);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            QppApi.updateValueForNotification(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //super.onDescriptorWrite(gatt, descriptor, status);
            Log.w("@@@@@@@Service", "onDescriptorWrite");
            QppApi.setQppNextNotify(gatt, true);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicWrite(gatt, characteristic, status);
            //status:数据传输是否成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
            /*This is a workaround,20140819,xiesc: it paused with unknown reason on android 4.4.3
             */
                Log.e("@@@@@@@Service", "Send success!!!!");
                //handlersend.post(runnableSend);
            } else {
                Log.e("@@@@@@@Service", "Send failed!!!!");
                //数据传输失败
            }
        }
    };

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    private void initBlueTooth() {
        //如果蓝牙未开启则请求开启蓝牙
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e("@@@@@@@Service", "Unable to initialize BluetoothManager.");
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("@@@@@@@Service", "onCreate() executed");



        initBlueTooth();
        QppApi.setCallback(new iQppCallback() {
            @Override
            public void onQppReceiveData(BluetoothGatt mBluetoothGatt, String qppUUIDForNotifyChar, byte[] qppData) {

                for (byte b : qppData) {
                    Log.d("@@接收byte：", "统一入口：" + ByteUtil.ByteToString(b));
                }

                if (qppData[3] == (byte) 0x04) {
                    //如果是历史数据则直接存储
                    saveHistoryData(qppData);

                } else if (crcChecker(qppData)) {
                    //如果通过crc校验则解析指令
                    analyzer(qppData);

                } else {

                }

            }
        });

        sqliteUtil = SqliteUtil.getInstance(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("@@@@@@@Service", "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
        hasConnectedDevice=null;
        Log.d("@@@@@@@Service", "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("@@@@@@@Service", "onBind() executed");
        return mBinder;
    }


    private void saveHistoryData(byte[] qppData) {
        requestCount=0;

        int num1 = ByteUtil.ByteToInt(qppData[4]);
        String sNum1;
        if (num1<10){
            sNum1="0"+num1;
        }else {
            sNum1=""+num1;
        }
        int num2 = ByteUtil.ByteToInt(qppData[5]);
        String sNum2;
        if (num2<10){
            sNum2="0"+num2;
        }else {
            sNum2=""+num2;
        }
        int num3 = ByteUtil.ByteToInt(qppData[6]);
        String sNum3;
        if (num3<10){
            sNum3="0"+num3;
        }else {
            sNum3=""+num3;
        }
        int num4 = ByteUtil.ByteToInt(qppData[7]);
        String sNum4;
        if (num4<10){
            sNum4="0"+num4;
        }else {
            sNum4=""+num4;
        }
        String serialNumber =sNum1+sNum2+sNum3+sNum4;

        StringBuilder builder = new StringBuilder();
        builder.append(ByteUtil.ByteToInt(qppData[8]));
        builder.append(ByteUtil.ByteToInt(qppData[9]));
        String year = builder.toString();//年（两位）

        int month = ByteUtil.ByteToInt(qppData[10]);
        String sMonth= String.valueOf(month);
        if (month<10){
            sMonth="0"+sMonth;
        }
        int day = ByteUtil.ByteToInt(qppData[11]);
        String sDay=String.valueOf(day);
        if (day<10){
            sDay="0"+sDay;
        }
        int hour = ByteUtil.ByteToInt(qppData[12]);
        String sHour=String.valueOf(hour);
        if (hour<10){
            sHour="0"+sHour;
        }
        int minute = ByteUtil.ByteToInt(qppData[13]);
        String sMinute=String.valueOf(minute);
        if (minute<10){
            sMinute="0"+sMinute;
        }

        String second="00";


        builder.delete(0, builder.length());

        builder.append(ByteUtil.ByteToString(qppData[14]));
        builder.append(ByteUtil.ByteToString(qppData[15]));
        int DATA1 = Integer.parseInt(ByteUtil.getRealData(builder.toString()), 16);
        int intNum1 = DATA1 / 10;
        int floatNum1 = DATA1 % 10;
        String data1 = "" + intNum1 + "." + floatNum1;
        String unit1 = new String(new byte[]{qppData[16]});


        builder.delete(0, builder.length());
        builder.append(ByteUtil.ByteToString(qppData[17]));
        builder.append(ByteUtil.ByteToString(qppData[18]));
        int DATA2 = Integer.parseInt(ByteUtil.getRealData(builder.toString()), 16);
        int intNum2 = DATA2 / 10;
        int floatNum2 = DATA2 % 10;
        String data2 = "" + intNum2 + "." + floatNum2;
        String unit2 = new String(new byte[]{qppData[19]});


        String finalData = "" + year + "-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute + ":" + second+" " + data1 + unit1 + " " + data2 + unit2;
        Log.d("@@历史信息解析：", "编号：" + serialNumber + "数据:" + finalData);

        //以序列号为数据表名，然后存储此条数据
        String time=""+year+"-" + sMonth + "-" + sDay + " " + sHour + ":" + sMinute+":" + second;
        ChannelData channelData=new ChannelData();
        channelData.setTime(time);
        channelData.setCHL1_value(Float.parseFloat(data1));
        channelData.setCHL1_unit(unit1);
        channelData.setCHL2_value(Float.parseFloat(data2));
        channelData.setCHL2_unit(unit2);
        sqliteUtil.saveChannelData("serial"+serialNumber,channelData,2);

    }


    private void analyzer(byte[] data) {

        if (data[0] == (byte) 0xbb) {
            //如果是设备上传指令则解析
            if (data[3] == (byte) 0x00) {
                //应答版本号
                String visionNumber = String.valueOf(ByteUtil.ByteToInt(data[4]));
            } else if (data[3] == (byte) 0x05) {
                //设置时间成功
                Log.d("@@设置时间成功！", "success");
                if (settingHelper!=null)
                settingHanlder.sendEmptyMessage(SETTIMESUCCESS);

            } else if (data[3] == (byte) 0x06) {
                Log.d("@@设置时间失败！", "fail");
                settingHanlder.sendEmptyMessage(SETTIMEFAILED);

            } else if (data[3] == (byte) 0x07) {
                Log.d("@@设置间隔成功！", "fail");
                settingHanlder.sendEmptyMessage(SETINTERVALSUCCESS);

            } else if (data[3] == (byte) 0x08) {
                Log.d("@@设置间隔失败！", "fail");
                settingHanlder.sendEmptyMessage(SETINTERVALFAILED);

            } else if (data[3] == (byte) 0x09) {
                Log.d("@@设置温度显示成功！", "fail");
                settingHanlder.sendEmptyMessage(SETTEMPERATUREDISPLAYSUCCESS);

            } else if (data[3] == (byte) 0x0A) {
                Log.d("@@设置温度显示失败！", "fail");
                settingHanlder.sendEmptyMessage(SETTEMPERATUREDISPLAYFAILED);

            } else if (data[3] == (byte) 0x0B) {
                //返回设备电量
                Log.d("@@成功！", "analyzPower");
                analyzPower(data);

            } else if (data[3] == (byte) 0x0C) {
                //设置报警上下限成功
                Log.d("@@设置报警上下限成功！", ".");
                settingHanlder.sendEmptyMessage(SETTEMPERATURELIMITSUCCESS);

            } else if (data[3] == (byte) 0x0D) {
                //设置报警上下限失败
                Log.d("@@设置报警上下限失败！", ".");
                settingHanlder.sendEmptyMessage(SETTEMPERATURELIMITFAILED);

            } else if (data[3] == (byte) 0x0E) {
                //设置报警方式成功
                Log.d("@@设置报警方式成功！", ".");
                settingHanlder.sendEmptyMessage(SETALARMTYPESUCCESS);

            } else if (data[3] == (byte) 0x0F) {
                //设置报警方式失败
                Log.d("@@设置报警方式失败！", ".");
                settingHanlder.sendEmptyMessage(SETALARMTYPEFAILED);

            } else if (data[3] == (byte) 0x10) {
                //获取MTK
                getMKT(data);

            } else if (data[3] == (byte) 0x11) {
                Log.d("@@", "设备详情");
                //返回设备详情
                getChannelInformation(data);

            } else if (data[3] == (byte) 0x12) {
                //返回存储满后处理的应答
                Log.d("@@存储满后处理设置成功", ".");
                settingHanlder.sendEmptyMessage(SETSAVETYPESUCCESS);

            } else if (data[3] == (byte) 0x13) {
                //返回存储满后处理的应答
                Log.d("@@存储满后处理设置失败", ".");
                settingHanlder.sendEmptyMessage(SETSAVETYPEFAILED);

            } else if (data[3] == (byte) 0x14) {
                //返回基本信息
                getBaseData(data);

            } else if (data[3] == (byte) 0x16) {
                //开始检查报警
                Log.d("@@","开始检查报警");
                for (Device device:devices){
                    SqliteUtil.getInstance(this).upDateDeviceStateBySerial(device.getDeviceSerial(),0);
                    Log.d("@@","manager:"+device.getDevice_manager_name()+"serail:"+device.getDeviceSerial()+"channeltype:"+device.getChannelType());
                    switch (device.getChannelType()){
                        case 1:
                            WarnHelper.isWarn(this,device.getDeviceSerial(),0,device);
                            break;
                        case 2:
                            WarnHelper.isWarn(this,device.getDeviceSerial(),0,device);
                            WarnHelper.isWarn(this,device.getDeviceSerial(),1,device);
                            break;
                    }
                }

                sendBroad();
                historyHandler.postDelayed(runnable,60*1000);//一分钟请求一次
                uploadData();

            } else if (data[3] == (byte) 0x17) {
                //更新完成

                analyzScope(data);
            }

        }
    }

    //解析设备上下限
    private void analyzScope(byte[] data) {
        float maxValue=ByteUtil.channelValue(data[4],data[5]);
        float minValue=ByteUtil.channelValue(data[6],data[7]);
        Log.d("@@设备温度范围",maxValue+"  "+minValue);
        Exception exception=new Exception();
        exception.setMaxRange(maxValue);
        exception.setMinRange(minValue);
        exception.setDeviceSerail(devices.get(0).getDeviceSerial());
        sqliteUtil.saveException(exception);
    }



    //上传数据
    public void uploadData(){
        if(DeviceManagerUtil.isAutouploading(this)&& NetUtils.isNetAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Device device:devices){
                        Log.d("@@upLoad",device.getDeviceSerial());
                        SqliteUtil.getInstance(getApplicationContext()).upLoadingData(device,getApplicationContext());
                    }
                }
            }).start();

        }else {
            if (!DeviceManagerUtil.isAutouploading(this))
                Log.d("@@upload","自动上传关闭");
            if (!NetUtils.isNetAvailable(this))
                Log.d("@@upload","当前网络不可用");
        }

    }

    private void sendBroad(){
        //发送跟新界面广播
        Intent intent=new Intent();
        intent.setAction(BlueToothCommuicationService.BLE_REFRESHUI);
        sendBroadcast(intent);
    }


    private void getMKT(byte[] data) {
        int mkt = ByteUtil.ByteToInt(data[4]);
        Log.d("@@获取", "MKT:" + mkt);
        Message msg = new Message();

        msg.what = GETMTKSUCCESS;
        msg.arg1 = mkt;
        settingHanlder.sendMessage(msg);

    }

    private void analyzPower(byte[] data) {
        Log.d("@@设备电量：", ByteUtil.ByteToString(data[4]));
        int power = ByteUtil.ByteToInt(data[4]);
        Log.d("@@设备电量：", "power:" + String.valueOf(power));

        Message msg = new Message();
        msg.what = GETPOWERSUCCESS;
        msg.arg1 = power;
        settingHanlder.sendMessage(msg);

    }

    //获取设备详情
    private void getChannelInformation(byte[] data) {
        //此处添加设备实体类
        Device device = new Device();
        Log.d("@@解析设备详情：", "");
        int num1 = ByteUtil.ByteToInt(data[4]);
        String sNum1;
        if (num1<10){
            sNum1="0"+num1;
        }else {
            sNum1=""+num1;
        }
        int num2 = ByteUtil.ByteToInt(data[5]);
        String sNum2;
        if (num2<10){
            sNum2="0"+num2;
        }else {
            sNum2=""+num2;
        }
        int num3 = ByteUtil.ByteToInt(data[6]);
        String sNum3;
        if (num3<10){
            sNum3="0"+num3;
        }else {
            sNum3=""+num3;
        }
        int num4 = ByteUtil.ByteToInt(data[7]);
        String sNum4;
        if (num4<10){
            sNum4="0"+num4;
        }else {
            sNum4=""+num4;
        }
        Log.d("@@:", "" + sNum1 + sNum2+sNum3+sNum4);
        device.setDeviceSerial(""+sNum1 + sNum2+sNum3+sNum4);
        int status = ByteUtil.ByteToInt(data[8]);
        Log.d("@@:", "通道类别：" + status);
        device.setChannelType(status);
        device.setDeviceName(""+sNum1 + sNum2+sNum3+sNum4);
        //sqliteUtil.createDataTable(""+num1+num2);
        device.setDevice_manager_name(hasConnectedDevice.getName());

        switch (status) {
            //判断通道类型
            case 1:
                Log.d("@@", "单温度通道");
                device.setCHL1_unit("℃");
                device.setCHL1_type("温度");
                break;
            case 2:
                Log.d("@@", "温湿度通道");
                device.setCHL1_unit("℃");
                device.setCHL2_unit("RH%");
                device.setCHL1_type("温度");
                device.setCHL2_type("湿度");
        }

        Alarme alarme=new Alarme();
        alarme.setDeviceSerial(""+sNum1 + sNum2+sNum3+sNum4);
        alarme.setAlarmType(1);
        alarme.setSavetype(1);
        devices.add(device);
        sqliteUtil.saveDevice(device,2);
        sqliteUtil.saveAlarme(alarme);
        sqliteUtil.createDataTable("serial"+sNum1 + sNum2+sNum3+sNum4);
        deviceNums--;
        if (deviceNums == 0) {
            helpHandler.sendEmptyMessage(GETDATAFINISH);
            Log.d("@@", "获取基本信息完毕");
        }
        mBinder.setTime();
        mBinder.getHistoryInformation();
    }

    //解析基本信息
    private void getBaseData(byte[] data) {
        StringBuffer buffer = new StringBuffer();

        Log.d("@@解析基本数据：", "");
        if (ByteUtil.ByteToInt(data[4]) == 1) {
            Log.d("@@：", "存在历史数据");
            buffer.append("存在历史数据");
        } else {
            Log.d("@@：", "不存在历史数据");
            buffer.append("不存在历史数据");
        }
        int deviceNum = ByteUtil.ByteToInt(data[5]);
        Log.d("@@：", "子设备总数：" + deviceNum);
        buffer.append("子设备总数：" + deviceNum);

        //之后请求设备详情
        mBinder.getchannelInformation(deviceNum);

    }

    private Boolean crcChecker(byte[] qppData) {
        byte[] CRC = new byte[]{qppData[qppData.length - 2], qppData[qppData.length - 1]};
        byte[] data = new byte[qppData.length - 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = qppData[i];
        }
        byte[] crc = CRC16Util.crcByte(data);
        for (byte b : crc) {
            Log.d("@@crc校验计算：", ByteUtil.ByteToString(b));
        }
        if (CRC[0] == crc[0]) {
            if (CRC[1] == crc[1]) {
                Log.d("@@@", "ServiceRecviced:" + "crc校验成功");
                return true;
            } else return false;
        } else {
            Log.d("@@@", "ServiceRecviced:" + "crc校验失败");
            return false;
        }

    }


    public class BluetoothBinder extends Binder {



        public void uploadDatas(){
            uploadData();
        }

        public boolean isInitialize(){
            return isInitialize;
        }

        public void connectDevice(BluetoothDevice device, BindHelper bindHelper) {
            mBluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
            mDevice = device;
            helper = bindHelper;
        }

        public void disConnect(){
            if (mBluetoothGatt!=null){
                mBluetoothGatt.disconnect();
            }
        }

        //在退出连接时注销接口
        public void cancelHelperInterface(){
            Log.d("@@cancelHelperInterface","helper");
            helper=null;
        }

        public void connectDevice(BluetoothDevice device, com.xcinfo.xc_blueteeth_android.main.monitor.activity.SettingHelper bindHelper) {
            mBluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
            mDevice = device;
            settingHelper = bindHelper;
        }

        public void getHistoryInformation() {
            //此处需要先存储获取到的设备信息
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getHistoryInformation()) {
                requestCount++;
                Log.d("@@requestcount",""+requestCount);
                if (requestCount==5){
                    for (Device device:devices){
                        Log.d("@@离线",device.getDeviceSerial());
                        SqliteUtil.getInstance(getApplicationContext()).upDateDeviceStateBySerial(device.getDeviceSerial(),3);
                        sendBroad();
                    }
                }
                Log.d("@@@@@@@Service", "发送请求温度数据成功");
            } else {
                if (isInitialize()){
                    historyHandler.postDelayed(runnable,60*1000);//一分钟请求一次
                    Log.d("@@","一分钟后重新请求");
                }

                Log.d("@@@@@@@Service", "发送请求温度数据失败");
            }
        }

        public void getScope(){
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getScope()) {
                Log.d("@@@@@@@Service", "发送获取范围数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取范围数据失败");
            }
        }


        public void setTime() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.setTime()) {
                Log.d("@@@@@@@Service", "发送设置时间数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置时间数据成功");
            }
        }

        public void getBaseData() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getBaseInformation()) {
                Log.d("@@@@@@@Service", "发送成功");
            } else {
                Log.d("@@@@@@@Service", "发送成功");
            }
        }

        //获取设备详情
        public void getchannelInformation(int deviceNum) {
            deviceNums = deviceNum;
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }

            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            for (int i = 0; i < deviceNum; i++) {
                if (mProtocolUtil.getChannelInformation()) {
                    Log.d("@@@@@@@Service", "发送获取通道信息数据成功");
                } else {
                    Log.d("@@@@@@@Service", "发送获取通道信息数据成功");
                }
            }

        }

        public void getPower() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getBatteryPower()) {
                Log.d("@@@@@@@Service", "发送获取电量信息数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取电量信息数据成功");
            }
        }

        public void setDegree(boolean isDegree) {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if(isDegree){
                if (mProtocolUtil.degreeSet()) {
                    Log.d("@@@@@@@Service", "发送设置摄氏度显示成功");
                } else {
                    Log.d("@@@@@@@Service", "发送设置摄氏度显示成功");
                }
            }else {
                if (mProtocolUtil.fahrenheitSet()) {
                    Log.d("@@@@@@@Service", "发送设置华氏度成功");
                } else {
                    Log.d("@@@@@@@Service", "发送设置华氏度成功");
                }
            }

        }

        public void getVision() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.getVisionNumber()) {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            }
        }

        public void setInterval(String interval) {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.setInterval(interval)) {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            }
        }

        public void setAlarmType(boolean isSingle) {

            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.setAlarmType(isSingle)) {
                Log.d("@@@@@@@Service", "发送设置报警方式数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置报警方式数据成功");
            }
        }


        public void getMtk() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.getMTK()) {
                Log.d("@@@@@@@Service", "发送获取mtk数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取mtk数据成功");
            }
        }

        public void whenMemoryFull(boolean isCover) {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.whenMemoryFull(isCover)) {
                Log.d("@@@@@@@Service", "发送数据满后处理数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送数据满后处理数据成功");
            }
        }

        public void setAlarmLimit(float maxLimit,float minLimit,int channelId){
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            switch (channelId){
                case 0:
                    //温度通道
                    if (mProtocolUtil.setAlarmLimit(maxLimit,minLimit,200,200)) {
                        Log.d("@@@@@@@Service", "发送报警上下限数据成功");
                    } else {
                        Log.d("@@@@@@@Service", "发送报警上下限数据成功");
                    }
                    break;
                case 1:
                    //湿度通道
                    if (mProtocolUtil.setAlarmLimit(200,200,maxLimit,minLimit)) {
                        Log.d("@@@@@@@Service", "发送报警上下限数据成功");
                    } else {
                        Log.d("@@@@@@@Service", "发送报警上下限数据成功");
                    }
            }
        }

        public void sendBinFiles() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");

                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            ContentResolver cr = getContentResolver();
            File binFiles = new File(AppContext.getBinFileDir());

            File[] files = binFiles.listFiles();
            if (files.length == 0) {
                Log.d("@@文件夹为空", "failed");
            } else {
                for (int i = 0; i < files.length; i++) {
                    Log.d("@@发送文件：", files[i].getName() + " path: " + files[i].getPath());
                    try {
                        byte[] binFile = ByteUtil.InputStreamTOByte(cr.openInputStream(Uri.parse(files[i].getPath())));
                        sendData(binFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        public BlueToothCommuicationService  getService(){
            return BlueToothCommuicationService.this;
        }

        public void setBindTag(String tag) {
            bindTag = tag;
        }

        public String getBindTag() {
            return bindTag;
        }


        public void sendFile(String filePath){
            File binFile=new File(filePath);
            if (binFile.exists()){

                try {

                    byte[] binFiles = ByteUtil.InputStreamTOByte(new FileInputStream(binFile));

                    if (sendData(binFiles)){

                    }else {

                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }

            }else {

            }
        }


        boolean sendData(byte[] message) {

            return QppApi.qppSendData(mBluetoothGatt, message);
        }


    }
}

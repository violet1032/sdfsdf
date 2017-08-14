package com.xcinfo.xc_blueteeth_android.servicetest;

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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ByteUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ProtocolUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.QppApi;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.iQppCallback;
import com.xcinfo.xc_blueteeth_android.common.config.AppContext;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyService extends Service {
    private int deviceCount = -1;
    private int fileNum=-1;
    private int repeatCount=0;

    ContentResolver cr;
    private MyBinder mBinder = new MyBinder();
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    boolean mConnected = false;//连接状态
    boolean isInitialize = false;//服务初始化是否成功
    boolean isDownLoadTemperature = false;//是否处于下载温度数据状态
    boolean isWaitupdate = false;//是否处于等待更新应答
    BluetoothDevice mDevice;
    ProtocolUtil mProtocolUtil;//发送命令工具类
    SqliteUtil sqliteUtil;

    private ConnectHelper connectHelper;//连接状态的回调

    protected static String uuidQppService = "0000fee9-0000-1000-8000-00805f9b34fb";
    protected static String uuidQppCharWrite = "d44bc439-abfd-45a2-b575-925416129600";

    private Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    connectHelper.connectSuccess();
                    break;
                case 2:
                    connectHelper.connectFailed();
                    break;
                case 3:
                    String information = (String) msg.obj + "\n";
                    connectHelper.showInformation(information);
            }
        }
    };

    Handler updateHanlder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.d("@@","case1");
                    if (isWaitupdate&&repeatCount<4){
                        showInformation("超时重新发送"+repeatCount);
                        mBinder.sendFile(AppContext.getBinFileDir()+"/"+fileNum);
                        repeatCount++;
                    }

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

                connectHandler.sendEmptyMessage(1);

                DeviceManager manager = new DeviceManager();
                manager.setName(mDevice.getName());
                sqliteUtil.saveDeviceManager(manager);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                mConnected = false;
                Log.d("@@@@@@@Service", "连接：失败");
                connectHandler.sendEmptyMessage(2);
                close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (QppApi.qppEnable(mBluetoothGatt, uuidQppService, uuidQppCharWrite)) {
                isInitialize = true;
                Log.d("@@@@@@@Service", "初始化成功");
            } else {
                isInitialize = false;
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
                //Log.e("@@@@@@@Service", "Send success!!!!");
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
        cr=getContentResolver();
        Log.d("@@@@@@@Service", "onCreate() executed");
        initBlueTooth();
        QppApi.setCallback(new iQppCallback() {
            @Override
            public void onQppReceiveData(BluetoothGatt mBluetoothGatt, String qppUUIDForNotifyChar, byte[] qppData) {
                //showInformation("接收byte：");
                for (byte b : qppData) {
                    Log.d("@@接收byte：", "统一入口：" + ByteUtil.ByteToString(b));
                    //showInformation(ByteUtil.ByteToString(b));
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

    private void saveHistoryData(byte[] qppData) {
        int serialNumber = ByteUtil.ByteToInt(qppData[1]) + ByteUtil.ByteToInt(qppData[2]);
        StringBuilder builder = new StringBuilder();
        builder.append(ByteUtil.ByteToInt(qppData[4]));
        builder.append(ByteUtil.ByteToInt(qppData[5]));
        String year = builder.toString();//年（两位）


        int month = ByteUtil.ByteToInt(qppData[6]);
        int day = ByteUtil.ByteToInt(qppData[7]);
        int hour = ByteUtil.ByteToInt(qppData[8]);
        int minute = ByteUtil.ByteToInt(qppData[9]);


        builder.delete(0, builder.length());

        builder.append(ByteUtil.ByteToString(qppData[10]));
        builder.append(ByteUtil.ByteToString(qppData[11]));
        int DATA1 = Integer.parseInt(ByteUtil.getRealData(builder.toString()), 16);
        int intNum1 = DATA1 / 10;
        int floatNum1 = DATA1 % 10;
        String data1 = "" + intNum1 + "." + floatNum1;
        String unit1 = new String(new byte[]{qppData[12]});


        builder.delete(0, builder.length());
        builder.append(ByteUtil.ByteToString(qppData[13]));
        builder.append(ByteUtil.ByteToString(qppData[14]));
        int DATA2 = Integer.parseInt(ByteUtil.getRealData(builder.toString()), 16);
        int intNum2 = DATA2 / 10;
        int floatNum2 = DATA2 % 10;
        String data2 = "" + intNum2 + "." + floatNum2;
        String unit2 = new String(new byte[]{qppData[15]});


        String finalData = "" + year + "-" + month + "-" + day + " " + hour + ":" + minute + " " + data1 + unit1 + " " + data2 + unit2;
        Log.d("@@历史信息解析：", "编号：" + serialNumber + "数据:" + finalData);

        showInformation(finalData);
    }


    //展示接收到的信息
    private void showInformation(String information) {
        Message msg = new Message();
        msg.obj = information;
        msg.what = 3;
        connectHandler.sendMessage(msg);
    }

    private void analyzer(byte[] data) {

        if (data[0] == (byte) 0xbb) {
            //如果是设备上传指令则解析
            if (data[3] == (byte) 0x00) {
                //应答版本号
                String visionNumber = String.valueOf(ByteUtil.ByteToInt(data[4]));
                showInformation("版本号：" + visionNumber);

            } else if (data[3] == (byte) 0x01) {
                repeatCount=0;
                showInformation("@@继续发送！");
                isWaitupdate=false;

                //继续发送，直到fileNum==0
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (fileNum>1){
                            mBinder.sendFile(AppContext.getBinFileDir()+"/"+--fileNum);
                        }else {
                            showInformation("发送完毕");
                        }
                    }
                }).start();


            } else if (data[3] == (byte) 0x02) {
                //设置时间成功
                showInformation("@@重新发送");
                isWaitupdate=false;

                //mBinder.sendFile(AppContext.getBinFileDir()+"/"+fileNum);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (fileNum>1){
                            mBinder.sendFile(AppContext.getBinFileDir()+"/"+fileNum);
                        }else {
                            showInformation("发送完毕");
                        }
                    }
                }).start();

            }else if (data[3] == (byte) 0x05) {
                //设置时间成功
                showInformation("@@设置时间成功！");

            } else if (data[3] == (byte) 0x06) {
                showInformation("@@设置时间失败！");

            } else if (data[3] == (byte) 0x07) {
                showInformation("@@设置间隔成功！");

            } else if (data[3] == (byte) 0x08) {
                showInformation("@@设置间隔失败！");

            } else if (data[3] == (byte) 0x09) {
                showInformation("@@设置温度显示成功！");

            } else if (data[3] == (byte) 0x0A) {
                showInformation("@@设置温度显示失败！");

            } else if (data[3] == (byte) 0x0B) {
                //返回设备电量
                Log.d("@@成功！", "analyzPower");
                analyzPower(data);

            } else if (data[3] == (byte) 0x0C) {
                //设置报警上下限成功
                showInformation("@@设置报警上下限成功！");

            } else if (data[3] == (byte) 0x0D) {
                //设置报警上下限失败
                showInformation("@@设置报警上下限失败！");

            } else if (data[3] == (byte) 0x0E) {
                //设置报警方式成功
                showInformation("@@设置报警方式成功！");

            } else if (data[3] == (byte) 0x0F) {
                //设置报警方式失败
                showInformation("@@设置报警方式失败！");

            } else if (data[3] == (byte) 0x10) {
                //获取MTK
                getMKT(data);

            } else if (data[3] == (byte) 0x11) {
                //返回设备详情
                Log.d("@@", "设备详情");
                getChannelInformation(data);

            } else if (data[3] == (byte) 0x12) {
                //返回存储满后处理的应答
                showInformation("存储满后处理设置成功");


            } else if (data[3] == (byte) 0x13) {
                //返回存储满后处理的应答
                showInformation("存储满后处理设置失败");


            } else if (data[3] == (byte) 0x14) {
                //返回基本信息
                getBaseData(data);

            }else if (data[3] == (byte) 0x15) {
                //更新完成
                showInformation("跟新完成");
                updateHanlder.removeMessages(1);
            }else if (data[3] == (byte) 0x17) {
                //更新完成
                showInformation("测量范围");
                analyzScope(data);
            }

        }
    }

    private void analyzScope(byte[] data) {
        float maxValue=ByteUtil.channelValue(data[4],data[5]);
        float minValue=ByteUtil.channelValue(data[6],data[7]);
        showInformation("上限："+maxValue+"  下限："+minValue);
    }

    private void getMKT(byte[] data) {
        int mkt=ByteUtil.ByteToInt(data[4]);
        showInformation("MKT:"+mkt);
    }

    private void analyzPower(byte[] data) {
        Log.d("@@设备电量：", ByteUtil.ByteToString(data[4]));
        int power = ByteUtil.ByteToInt(data[4]);
        Log.d("@@设备电量：", "power:" + String.valueOf(power));
        showInformation("@@设备电量：" + String.valueOf(power));
    }

    //获取设备详情
    private void getChannelInformation(byte[] data) {
        Log.d("@@解析设备详情：", "");
        int num1 = ByteUtil.ByteToInt(data[4]);
        int num2 = ByteUtil.ByteToInt(data[5]);
        Log.d("@@序列号:", "" + num1 + num2);
        int status = ByteUtil.ByteToInt(data[6]);
        Log.d("@@:", "通道类别：" + status);
        switch (status) {
            //判断通道类型
            case 0:

                break;
        }
        showInformation("设备详情：" + "序列号：" + num1 + num2 + "\n" + "通道类别：" + status);
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
        showInformation(buffer.toString());
        deviceCount = deviceNum;
        //之后请求设备详情
        mBinder.getchannelInformation();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("@@@@@@@Service", "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
        Log.d("@@@@@@@Service", "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;

    }


    public class MyBinder extends Binder {

        public void sendFile(String filePath){


            File binFile=new File(filePath);
            if (binFile.exists()){
                showInformation("发送文件"+binFile.getPath());
                try {

                    byte[] binFiles = ByteUtil.InputStreamTOByte(new FileInputStream(binFile));
                    showInformation("--"+binFiles.length);
                    if (sendData(binFiles)){
                        updateHanlder.removeMessages(1);
                        updateHanlder.sendEmptyMessageDelayed(1,5000);
                        isWaitupdate=true;
                        showInformation("发送成功");
                    }else {
                        showInformation("发送失败");
                        sendFile(AppContext.getBinFileDir()+"/"+fileNum);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    showInformation(e.toString());
                }

            }else {
                showInformation("文件不存在"+filePath);
            }
        }

        public void startDownload(ConnectHelper helper) {
            Log.d("@@@@@@@Service", "startDownload() executed");
            // 执行具体的下载任务
            connectHelper = helper;
        }

        public void connectDevice(BluetoothDevice device, ConnectHelper connectHelpers) {
            mBluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
            mDevice = device;
            connectHelper = connectHelpers;

        }

        public void getHistoryInformation() {
            showInformation("获取历史数据");
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getHistoryInformation()) {
                isDownLoadTemperature = true;
                Log.d("@@@@@@@Service", "发送请求温度数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送请求温度数据失败");
            }
        }

        public void setTime() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.setTime()) {
                Log.d("@@@@@@@Service", "发送设置时间数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置时间数据失败");
            }
        }

        public void getScope(){
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getScope()) {
                Log.d("@@@@@@@Service", "发送获取范围数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取范围数据失败");
            }
        }

        public void getBaseData() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getBaseInformation()) {
                Log.d("@@@@@@@Service", "发送获取基本信息成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取基本信息失败");
            }
        }

        public void getchannelInformation() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getChannelInformation()) {
                Log.d("@@@@@@@Service", "发送获取通道信息数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取通道信息数据失败");
            }
        }

        public void getPower() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);
            if (mProtocolUtil.getBatteryPower()) {
                Log.d("@@@@@@@Service", "发送获取电量信息数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取电量信息数据成功");
            }
        }

        public void setDegree() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.degreeSet()) {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送设置温湿度数据成功");
            }
        }

        public void getVision() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }
            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

            if (mProtocolUtil.getVisionNumber()) {
                Log.d("@@@@@@@Service", "发送获取版本好数据成功");
            } else {
                Log.d("@@@@@@@Service", "发送获取版本好数据成功");
            }
        }

        public void setInterval(String interval) {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
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


        public void sendBinFiles() {
            if (!isInitialize) {
                Log.d("@@!isInitialize", "???");
                connectHelper.notInitialize();
                return;
            }
            if (mBluetoothGatt == null) {
                Log.d("@@mBluetoothGatt==null", "??????");
                return;
            }

            mProtocolUtil = new ProtocolUtil(mBluetoothGatt);

//            if (mProtocolUtil.updateVision()) {
//                Log.d("@@@@@@@Service", "发送更新指令成功");
//            } else {
//                Log.d("@@@@@@@Service", "发送更新指令失败");
//            }
            File binFiles = new File(AppContext.getBinFileDir());
            File[] files = binFiles.listFiles();
            for (int i = 0; i < files.length; i++) {
               // showInformation(files[i].getPath());
            }
            fileNum=11;
            sendFile(AppContext.getBinFileDir()+"/"+fileNum);

//            if (files.length == 0) {
//                Log.d("@@文件夹为空", "failed");
//            } else {
//                for (int i = 0; i < files.length; i++) {
//                    Log.d("@@发送文件：", files[i].getName() + " path: " + files[i].getPath());
//                    try {
//                        byte[] binFile = ByteUtil.InputStreamTOByte(cr.openInputStream(Uri.parse(files[i].getPath())));
//                        sendData(binFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }


        }



        boolean sendData(byte[] message) {
//            for (byte b:message){
//                showInformation(ByteUtil.ByteToString(b));
//            }
            return QppApi.qppSendData(mBluetoothGatt, message);

        }


    }
}

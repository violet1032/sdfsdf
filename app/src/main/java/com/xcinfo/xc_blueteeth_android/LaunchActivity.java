package com.xcinfo.xc_blueteeth_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.bluetoothtest.BlueToothActivity;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ByteUtil;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;
import com.xcinfo.xc_blueteeth_android.main.activity.MainActivity;
import com.xcinfo.xc_blueteeth_android.main.bean.Alarme;

import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.uploading.TCPSocketUtil;
import com.xcinfo.xc_blueteeth_android.main.uploading.UploadingProtocolUtil;
import com.xcinfo.xc_blueteeth_android.servicetest.ServiceActivity;
import org.kymjs.kjframe.ui.BindView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LaunchActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @BindView(id=R.id.device_list,click = true)
    ListView deviceList;

    @BindView(id=R.id.button ,click = true)
    Button button;

    @BindView(id=R.id.button2,click = true)
    Button button2;

    @BindView(id=R.id.button7,click = true)
    Button button3;

    @BindView(id=R.id.button11,click = true)
    Button button11;

    @BindView(id=R.id.button12,click = true)
    Button button12;

    @BindView(id=R.id.button13,click = true)
    Button button13;

    @BindView(id=R.id.button9,click = true)
    Button button14;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_launch);
    }

    @Override
    public void initData() {
        super.initData();
        //uploadData();

    }

    private void uploadData() {
        ChannelData channelData=new ChannelData();
        channelData.setCHL1_value((float) 17.9);
        channelData.setCHL1_unit("C");
        channelData.setMaxLimit((float) 18);
        channelData.setMinLimit((float) 0);

        ChannelData channelData1=new ChannelData();
        channelData1.setCHL1_value((float) 16.7);
        channelData1.setCHL1_unit("%");
        channelData1.setMaxLimit((float) 18);
        channelData1.setMinLimit((float) 0);
        List<ChannelData> channelDatas=new ArrayList<>();
        channelDatas.add(channelData);
        channelDatas.add(channelData1);

        final byte[] data= UploadingProtocolUtil.getupLoadingBytes(channelDatas,"17030001",2,5);
        int i=1;
        for (byte b:data){
            Log.d("uploadbyte", ByteUtil.ByteToString(b)+"");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                TCPSocketUtil.getInstance().send(data);
            }
        }).start();
        Log.d("uploadbyte.length", String.valueOf(data.length));
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()){
            case R.id.button:
                startActivity(new Intent(LaunchActivity.this,BlueToothActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(LaunchActivity.this, ServiceActivity.class));
                break;
            case R.id.button11:

            //建表用的测试代码
            DeviceManager deviceManager=new DeviceManager();
                deviceManager.setName("蓝牙主机1");
                SqliteUtil.getInstance(LaunchActivity.this).saveDeviceManager(deviceManager);
                DeviceManager deviceManager1=new DeviceManager();
                deviceManager1.setName("蓝牙主机2");
                SqliteUtil.getInstance(LaunchActivity.this).saveDeviceManager(deviceManager1);

                //设备1
                Device device=new Device();
                device.setDeviceSerial("245321312");
                device.setDeviceName("绵阳西创GPS测试设备1");
                device.setDevice_manager_name("蓝牙主机2");
                //device.setDeviceState(0);//正常状态
                //device.setToatlChannelCount(2);
                device.setChannelType(2);
                device.setCHL1_unit("℃");
                device.setCHL1_type("温度");
                device.setCHL2_unit("RH%");
                device.setCHL2_type("湿度");
                //device.setBelongGroup("1");

                Alarme alarme=new Alarme();
                alarme.setDeviceSerial("245321312");
//                alarme.setCHL1_maxLimit(32);
//                alarme.setCHL1_minLimit(0.1f);
//                alarme.setCHL2_maxLimit(100);
//                alarme.setCHL2_minLimit(-32f);
                alarme.setAlarmType(1);
                alarme.setSavetype(1);

                //设备2
                Device device1=new Device();
                device1.setDeviceSerial("3524213");
                device1.setDeviceName("绵阳西创GPS测试设备2");
                device1.setDevice_manager_name("蓝牙主机2");
                //device1.setDeviceState(0);//正常状态
                //device1.setToatlChannelCount(2);
                device1.setChannelType(2);
                device1.setCHL1_unit("℃");
                device1.setCHL1_type("温度");
                device1.setCHL2_unit("RH%");
                device1.setCHL2_type("湿度");
                //device1.setBelongGroup("1");

                Alarme alarme1=new Alarme();
                alarme1.setDeviceSerial("3524213");
//                alarme1.setCHL1_maxLimit(32);
//                alarme1.setCHL1_minLimit(0.1f);
//                alarme1.setCHL2_maxLimit(100);
//                alarme1.setCHL2_minLimit(-32f);
                alarme1.setAlarmType(1);
                alarme1.setSavetype(1);

                //设备3
                Device device2=new Device();
                device2.setDeviceSerial("211354");
                device2.setDeviceName("测试设备");
                device2.setBelongGroup("分组2");
                device2.setDevice_manager_name("蓝牙主机2");
               // device2.setDeviceState(0);//正常状态
                //device2.setToatlChannelCount(2);
                device2.setChannelType(2);
                device2.setCHL1_unit("℃");
                device2.setCHL1_type("温度");
                device2.setCHL2_unit("RH%");
                device2.setCHL2_type("湿度");
                //device2.setBelongGroup("1");

                Alarme alarme11=new Alarme();
                alarme11.setDeviceSerial("211354");
//                alarme11.setCHL1_maxLimit(32);
//                alarme11.setCHL1_minLimit(0.1f);
//                alarme11.setCHL2_maxLimit(100);
//                alarme11.setCHL2_minLimit(-32f);
                alarme11.setAlarmType(1);
                alarme11.setSavetype(1);

                SqliteUtil.getInstance(LaunchActivity.this).saveDevice(device,2);
                SqliteUtil.getInstance(LaunchActivity.this).saveDevice(device1,2);
                SqliteUtil.getInstance(LaunchActivity.this).saveDevice(device2,2);
                SqliteUtil.getInstance(LaunchActivity.this).saveAlarme(alarme);
                SqliteUtil.getInstance(LaunchActivity.this).saveAlarme(alarme1);
                SqliteUtil.getInstance(LaunchActivity.this).saveAlarme(alarme11);

                Device device5=new Device();
                device5.setDeviceSerial("201703161619");
                device5.setDeviceName("测试设备");
                device5.setDevice_manager_name("蓝牙主机1");
                device5.setDeviceState(0);//正常状态
                device5.setBelongGroup("");
                device5.setToatlChannelCount(2);
                device5.setCHL1_unit("RH%");
                device5.setCHL1_type("湿度");
                device5.setCHL2_unit("RH%");
                device5.setCHL2_type("湿度");

                SqliteUtil.getInstance(LaunchActivity.this).saveDevice(device5,2);

                Group group=new Group();
                group.setDeviceCount(3);
                group.setGroupName("测试分组");
                //SqliteUtil.getInstance(LaunchActivity.this).saveGroup(group,"蓝牙主机1");
                Group group1=new Group();
                group1.setGroupName("分组2");
                group1.setDeviceCount(10);
                //SqliteUtil.getInstance(LaunchActivity.this).saveGroup(group1,"蓝牙主机1");

                Device device4=new Device();
                device4.setDeviceSerial("201703161616");
                device4.setDeviceName("单通道设备");
                device4.setBelongGroup("测试分组");
                device4.setDevice_manager_name("蓝牙主机1");
                device4.setToatlChannelCount(1);
                device4.setCHL1_unit("RH%");
                device4.setCHL1_type("湿度");
                SqliteUtil.getInstance(LaunchActivity.this).saveDevice(device4,1);

                ChannelData data4=new ChannelData();

                for (int i = 10; i < 60; i++) {
                    String time1="2017-05-01 15:"+i+":23";
                    data4.setTime(time1);
                    data4.setCHL1_value(15.3f);
                    data4.setCHL2_value(36.3f+1);
                    SqliteUtil.getInstance(LaunchActivity.this).saveChannelData("蓝牙主机1201703161613",data4,2);
                }

                ChannelData data0=new ChannelData();
                data0.setTime(TimeUtil.getNowTime());
                float num=11.1f;
                num= (float) (num+1.2);
                data0.setCHL1_value(num);
                SqliteUtil.getInstance(LaunchActivity.this).saveChannelData("蓝牙主机1201703161616",data0,1);
///*
//
                ChannelData data=new ChannelData();
                data.setTime(TimeUtil.getNowTime());
                data.setCHL1_value(25);
                data.setCHL2_value(36.3f+1);
                SqliteUtil.getInstance(LaunchActivity.this).saveChannelData("蓝牙主机1201703161614",data,2);


                ChannelData data1=new ChannelData();
                data1.setTime(TimeUtil.getNowTime());
                data1.setCHL1_value(20+1);
                data1.setCHL2_value(38.3f+1);
                SqliteUtil.getInstance(LaunchActivity.this).saveChannelData("蓝牙主机1201703161615",data1,2);



                break;
            case R.id.button12:

                break;
            case R.id.button13:
                String path=this.getFilesDir().toString()+"/"+"xc_blueteeth_db.db";
                File db=new File(path);
                if (db.exists()){
                    if (db.delete()){
                        Toast.makeText(this, "删除数据库成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case  R.id.button9:
                uploadData();
                break;

        }
    }
}

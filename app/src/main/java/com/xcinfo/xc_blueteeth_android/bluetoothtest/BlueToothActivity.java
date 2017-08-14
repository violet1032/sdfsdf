package com.xcinfo.xc_blueteeth_android.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ProtocolUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.BluetoothService;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.QppApi;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.iQppCallback;

import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlueToothActivity extends BaseActivity {
    StringBuilder builder=new StringBuilder();
    private static final String TAG="BlueToothActivyty:";
    private static final int REQUEST_ENABLE_BT = 1;//开启蓝牙ResultCode
    private static final int REQUEST_OPEN_BT = 2;//开启蓝牙ResultCode
    protected static String uuidQppService = "0000fee9-0000-1000-8000-00805f9b34fb";
    protected static String uuidQppCharWrite = "d44bc439-abfd-45a2-b575-925416129600";
    ProtocolUtil mProtocolUtil;
    ListAdapter mAdapter;
    BluetoothService mService;
    private BluetoothManager mBluetoothManager = null;
    static BluetoothAdapter mBluetoothAdapter=null;
    List<BluetoothDevice> deviceList = new ArrayList<>();
    private Handler handler;
    public static BluetoothGatt mBluetoothGatt = null;
    StringBuilder mStringBuilder=new StringBuilder();
    KJDB db;
    boolean mConnected=false;
    boolean isInitialize=false;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "onConnectionStateChange : " + status + "  newState : " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
                mConnected = true;
                Toast.makeText(BlueToothActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                Log.d("@@@@@@@","连接：成功");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                mConnected = false;
                Toast.makeText(BlueToothActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
                Log.d("@@@@@@@","连接：失败");
                close();
//                clearHandler(handlerQppDataRate, runnableQppDataRate);
//                clearHandler(handlersend, runnableSend);
//                dataRecvFlag = false;
//                if (qppSendDataState) {
//                    setBtnSendState("Send");
//                    qppSendDataState = false;
//                }
//                close();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (QppApi.qppEnable(mBluetoothGatt, uuidQppService, uuidQppCharWrite)) {
                isInitialize = true;
                Log.d("@@@@@@@","初始化成功");
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
            Log.w(TAG, "onDescriptorWrite");
            QppApi.setQppNextNotify(gatt, true);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicWrite(gatt, characteristic, status);
            //status:数据传输是否成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
            /*This is a workaround,20140819,xiesc: it paused with unknown reason on android 4.4.3
			 */
                Log.e("@@@", "Send success!!!!");
                //handlersend.post(runnableSend);
            } else {
                Log.e("@@@", "Send failed!!!!");
                //数据传输失败
            }
        }
    };



    @BindView(id = R.id.getDevice, click = true)
    Button btn;
    @BindView(id = R.id.device_list, click = true)
    ListView mListView;
    @BindView(id=R.id.information_tv,click = false)
    TextView informationTv;
    @BindView(id=R.id.get_information,click = true)
    Button getButton;
    @BindView(id=R.id.get_db_information,click = true)
    Button getDbButton;
    @BindView(id=R.id.delect_db_information,click = true)
    Button deleteButton;
    @BindView(id=R.id.clear_tv,click = true)
    Button clearBtn;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_blue_tooth);
    }

    @Override
    public void initData() {
        db=KJDB.create(this);
        informationTv.setText("等待中");
        super.initData();
        initBlueTooth();
        getPairedService();
        mAdapter = new ListAdapter(this, deviceList);
        mListView = (ListView) findViewById(R.id.device_list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(deviceList.get(position).getAddress());
                mBluetoothGatt=device.connectGatt(BlueToothActivity.this,false,mGattCallback);
                informationTv.setText("连接："+device.getName());
                Log.d("@@@@@@@","连接："+device.getName()+device.getAddress());
            }
        });


    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.getDevice:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_OPEN_BT);
                break;
            case R.id.get_information:
//                if(QppApi.qppSendData(mBluetoothGatt,"cscsccss".getBytes())){
//                    Log.d("@@@@@@@","发送成功");
//                }else {
//                    Log.d("@@@@@@@","发送失败");
//                }
                mProtocolUtil=new ProtocolUtil(mBluetoothGatt);
                mProtocolUtil.getHistoryInformation();
                Log.d("@@@@@@@","点击了：");
                break;
            case R.id.get_db_information:
//                Test test=new Test();
//                test.setName("任亚东");
//                test.setPhone("18148413696");
//                db.save(test);
//                List<Temperature> list;
//                list= db.findAll(Temperature.class);
//                if (list.isEmpty()){
//                    Log.d("@@@","数据库为空");
//                }else {
//                    Log.d("@@@","数据"+list.size());
//                    for (int i = 0; i < list.size(); i++) {
//                        Log.d("@@@","数据"+list.get(i).getTemperature()+"  "+list.get(i).getId());
//                    }}
                break;
            case R.id.delect_db_information:
//                    db.deleteByWhere(Temperature.class,"id>0");
                break;
            case R.id.clear_tv:
                builder.delete(0,builder.length()-1);
                informationTv.setText("");

        }
    }

    private void initBlueTooth() {
        //如果蓝牙未开启则请求开启蓝牙
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");

            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();


        mService = new BluetoothService(this, handler);
        if (mBluetoothAdapter.enable() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果用户不开启则退出activity
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        if (requestCode == REQUEST_OPEN_BT && resultCode == Activity.RESULT_FIRST_USER) {
            Toast.makeText(getApplicationContext(), "退出蓝牙界面", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getPairedService() {
        // 获取已配对设备
        Set<BluetoothDevice> pairedDevices = mService.getPairedDev();
        for (BluetoothDevice device : pairedDevices) {
            deviceList.add(device);
            //Toast.makeText(this, device.getName(), Toast.LENGTH_LONG).show();
        }




QppApi.setCallback(new iQppCallback() {
    @Override
    public void onQppReceiveData(BluetoothGatt mBluetoothGatt, String qppUUIDForNotifyChar, final byte[] qppData) {
        builder.append(new String(qppData));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                informationTv.setText(builder.toString());
                //Toast.makeText(BlueToothActivity.this,new String(qppData), Toast.LENGTH_SHORT).show();
            }
        });



//        String[] temperature=new String(qppData).split(" ");
//        for (int i = 0; i < temperature.length; i++) {
//            Log.d("@@@@@@@",temperature[i]);
//            db.save(new Temperature(temperature[i]));
//        }


    }
});

        //监听蓝牙连接状态
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothService.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:   //已连接
                                Log.d("@@Handler:","已连接");
                                break;
                            case BluetoothService.STATE_CONNECTING:  //正在连接
                                Log.d("@@Handler:","正在连接");
                                break;
                            case BluetoothService.STATE_LISTEN:     //监听连接的到来
                            case BluetoothService.STATE_NONE:
                                break;
                        }
                        break;
                    case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                        Log.d("@@Handler:","断开连接");

                        break;
                    case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备

                        break;

                }
            }

        };
    }
}

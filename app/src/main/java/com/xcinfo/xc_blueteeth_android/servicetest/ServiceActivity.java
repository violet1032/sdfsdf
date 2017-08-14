package com.xcinfo.xc_blueteeth_android.servicetest;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ByteUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.BluetoothService;

import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServiceActivity extends BaseActivity implements ConnectHelper {
    private MyService.MyBinder myBinder;
    private List<BluetoothDevice> devices;
    BluetoothService mService;
    Handler handler;
    PairedDeviceAdapter adapter;
    StringBuilder builder=new StringBuilder();

    @BindView(id = R.id.button3, click = true)
    Button button3;
    @BindView(id = R.id.button4, click = true)
    Button button4;
    @BindView(id = R.id.button5, click = true)
    Button button5;
    @BindView(id = R.id.button6, click = true)
    Button button6;
    @BindView(id = R.id.button8, click = true)
    Button button8;
    @BindView(id = R.id.textView, click = true)
    TextView mTextView;

    @BindView(id = R.id.button10, click = true)
    Button button10;
    @BindView(id=R.id.button14,click = true)
    Button button11;
    @BindView(id=R.id.button15,click = true)
    Button button12;
    @BindView(id=R.id.button16,click = true)
    Button button13;
    @BindView(id=R.id.button17,click = true)
    Button button14;
    @BindView(id=R.id.button18,click = true)
    Button button15;
    @BindView(id=R.id.button19,click = true)
    Button button16;
    @BindView(id=R.id.button20,click = true)
    Button button17;
    @BindView(id=R.id.button21,click = true)
    Button button18;
    @BindView(id=R.id.button22,click = true)
    Button button19;
    @BindView(id=R.id.edit_tv,click = true)
    TextView editTv;
    @BindView(id=R.id.paired_device_recyclerView,click = true)
    RecyclerView pairedDeviceRecyclerView;
    @BindView(id=R.id.data_recived_tv,click = true)
    TextView DataRecievedTv;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyService.MyBinder) service;
            myBinder.startDownload(ServiceActivity.this);
        }
    };

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getVision();
                }
                break;
            case R.id.button4:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                    byte b=ByteUtil.StringtoHexByte(editTv.getText().toString());
                    byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x0A,b};
                    byte[] crc= CRC16Util.crcByte(bytes);
                    for (byte by:crc){
                        Log.d("@@crc:",ByteUtil.ByteToString(by));
                    }
                } else {
                    if (TextUtils.isEmpty(editTv.getText())){
                        return;
                    }
                    String interval=editTv.getText().toString();

                    byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x0A,ByteUtil.StringtoHexByte(interval),(byte)0x00};
                    showInformation("时间间隔设置发送数据：");
                    for (int i = 0; i < bytes.length; i++) {
                        showInformation(ByteUtil.ByteToString(bytes[i]));
                    }
                    myBinder.setInterval(interval);
                }
//                int interval=Integer.parseInt(editTv.getText().toString());
//                Log.d("@@editNumber:",""+interval);
                break;
            case R.id.button5:
                Intent bindIntent = new Intent(this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.button6:
                unbindService(connection);
                myBinder=null;
                break;
            case R.id.button8:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.startDownload(ServiceActivity.this);
                }
                break;
            case R.id.textView:

                break;
            case R.id.button10:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getHistoryInformation();
                }
                break;
            case R.id.button14:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                        myBinder.setTime();
                }
                break;
            case R.id.button15:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getBaseData();
                }
                break;
            case R.id.button16:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getchannelInformation();
                }
                break;
            case R.id.button17:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getPower();
                }
                break;
            case R.id.button18:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.setDegree();
                }
                break;
            case R.id.button19:
                builder.delete(0,builder.length()-1);
                DataRecievedTv.setText("");
                break;
            case R.id.button20:
                //发送bin文件
                break;
            case R.id.button21:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.sendBinFiles();
                }
                break;
            case R.id.button22:
                if (myBinder == null) {
                    Toast.makeText(this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.getScope();
                }
                break;

        }
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_service);
        super.setRootView();
    }

    @Override
    public void initData() {
        super.initData();

        mService = new BluetoothService(this,//监听蓝牙连接状态
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case BluetoothService.MESSAGE_STATE_CHANGE:
                                switch (msg.arg1) {
                                    case BluetoothService.STATE_CONNECTED:   //已连接
                                        break;
                                    case BluetoothService.STATE_CONNECTING:  //正在连接
                                        break;
                                    case BluetoothService.STATE_LISTEN:     //监听连接的到来
                                    case BluetoothService.STATE_NONE:
                                        break;
                                }
                                break;
                            case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                                break;
                            case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                                break;
                        }
                    }

                });
        devices = new ArrayList<>();
        Set<BluetoothDevice> device = mService.getPairedDev();
        for (BluetoothDevice device1 : device) {
            devices.add(device1);
            Log.d("@@pairedDevice::",device1.getName());
        }

        adapter=new PairedDeviceAdapter(devices,this);
        adapter.setItemOnclickListener(new PairedDeviceAdapter.OnclickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(ServiceActivity.this, "连接："+devices.get(position).getName(), Toast.LENGTH_SHORT).show();
                if (myBinder == null) {
                    Toast.makeText(ServiceActivity.this, "服务未绑定", Toast.LENGTH_SHORT).show();
                } else {
                    myBinder.connectDevice(devices.get(position),ServiceActivity.this);
                    mTextView.setText("连接："+devices.get(position).getName());
                }
            }
        });

        pairedDeviceRecyclerView.setAdapter(adapter);
        pairedDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }




    /**
     * 文件发送相关方法
     */








    /**
     * 实现ConnectHelper接口方法
     */
    @Override
    public void connectFailed() {
        mTextView.setText("连接失败");
    }

    @Override
    public void connectSuccess() {
        Log.d("@@connectSuccess() ","????");
        Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        mTextView.setText("连接成功");
    }

    @Override
    public void notInitialize() {
        mTextView.setText("数据传输服务未初始化\n请检查是否连接了正常的蓝牙模块");
        Toast.makeText(this, "数据传输服务未初始化\n" +
                "请检查是否连接了正常的蓝牙模块", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInformation(String information) {
        builder.append(information);
        DataRecievedTv.setText(builder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

}

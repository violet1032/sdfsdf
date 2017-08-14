package com.xcinfo.xc_blueteeth_android.main.activity.searchdevice;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.BluetoothService;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BindHelper;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.toolbarbuilder.MyNavigationBuilder;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.activity.CommonScanActivity;
import com.xcinfo.xc_blueteeth_android.servicetest.ServiceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SearchDeviceActivity extends AppCompatActivity implements BindHelper {
    BluetoothService mService;
    BluetoothAdapter bluetoothAdapter;
    RecyclerView mRecyclerView;//蓝牙设备列表
    LinearLayout dialogLinearLayout;//连接时的进度列表
    Button btnConnect;
    DeviceRecyclerAdapter mDeviceRecyclerAdapter;
    List<BluetoothDevice> pairedDevice = new ArrayList<>();
    Button startBlue;//开始扫描
    Button qrScan;
    ProgressBar progress;//扫描进度条
    boolean isBind;
    boolean isConnected;
    BroadcastReceiver mReceiver;
    Handler mHandler = new Handler();

    protected static String uuidQppService = "0000fee9-0000-1000-8000-00805f9b34fb";
    protected static String uuidQppCharWrite = "d44bc439-abfd-45a2-b575-925416129600";

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            //Log.d("@@", device.getName());
            runOnUiThread(new Runnable() {
                BluetoothDevice bluetoothDevice = device;

                @Override
                public void run() {
                    //如果有新设备则显示
                    for (int i = 0; i < pairedDevice.size(); i++) {
                        if (pairedDevice.get(i).getAddress().equals(device.getAddress())) {
                            bluetoothDevice = null;
                            break;
                        }
                    }

                    if (bluetoothDevice != null) {
                        pairedDevice.add(bluetoothDevice);
                        Log.d("@@pairedDevice.add", bluetoothDevice.getName());
                        refreshRecyclerView();
                    }
                }
            });

        }
    };

    static RefreshState state;


    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SCAN_RESULT = 3;

    private BlueToothCommuicationService.BluetoothBinder myBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BlueToothCommuicationService.BluetoothBinder) service;
            isBind = true;
            Log.d("@@ServiceSearchAct", "binded");
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        initToolBar();//添加ToolBar
        initBroadcastReceiver();//注册广播
        initBluetooth();

        initView();
        bindBluetoothService();

        //开始搜索
        bleSearch(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleSearch(false);
            }
        }, 10000);


    }

    private void initBluetooth() {
        mService = new BluetoothService(this, handler);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //如果蓝牙未开启这开启蓝牙
        if (mService.isBTopen() == false) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }


    }


    private void bindBluetoothService() {
        Intent bindIntent = new Intent(this, BlueToothCommuicationService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    private void initBroadcastReceiver() {
        IntentFilter mFilter = new IntentFilter();

        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("@@ACTION", "statechamge");
                //refreshRecyclerView();
            }
        };

        registerReceiver(mReceiver, mFilter);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.reciclerview_device);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL, R.drawable.bluetoothdevice_decoration));

        refreshRecyclerView();

        progress = (ProgressBar) findViewById(R.id.progressbar_searchdevice);
        startBlue = (Button) findViewById(R.id.startblue);
        startBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleSearch(true);
                //10秒后停止搜索
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bleSearch(false);
                    }
                }, 10000);
            }
        });

        qrScan= (Button) findViewById(R.id.btn_qr_scan);
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchDeviceActivity.this,CommonScanActivity.class);
                startActivityForResult(intent,SCAN_RESULT);
            }
        });

    }

    private void bleSearch(boolean isStart) {
        pairedDevice.clear();
        if (isStart) {
            startBlue.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            startBlue.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    public static void ActionStart(RefreshState refreshState, Context context) {
        Intent intent = new Intent(context, SearchDeviceActivity.class);
        state = refreshState;
        context.startActivity(intent);
    }

    private void refreshRecyclerView() {

        mDeviceRecyclerAdapter = new DeviceRecyclerAdapter(this, pairedDevice);
        mDeviceRecyclerAdapter.setHasConnectedDevice(BlueToothCommuicationService.getHasConnectedDevice());
        mRecyclerView.setAdapter(mDeviceRecyclerAdapter);
        //mDeviceRecyclerAdapter.notifyDataSetChanged();
        mDeviceRecyclerAdapter.setRecyclerviewListener(new DeviceRecyclerAdapter.RecyclerviewListener() {

            @Override
            public void onClick(BluetoothDevice device, LinearLayout dialogLayout, Button button) {

                btnConnect = button;
                if (dialogLinearLayout != null) {
                    //如果之前连接过其他设备，则隐藏该设备连接状态
                    dialogLinearLayout.setVisibility(View.GONE);
                }
                dialogLinearLayout = dialogLayout;
                TextView tvConnect = (TextView) dialogLinearLayout.findViewById(R.id.tv_connect);
                ProgressBar progressBarConnect = (ProgressBar) dialogLinearLayout.findViewById(R.id.procressbar_connect);

                //tvConnect.setVisibility(View.VISIBLE);
                progressBarConnect.setVisibility(View.VISIBLE);
                dialogLinearLayout.setVisibility(View.VISIBLE);
                myBinder.connectDevice(device, SearchDeviceActivity.this);

            }
        });
    }

    private void initToolBar() {
        MyNavigationBuilder builder = new MyNavigationBuilder(this, (ViewGroup) this.getWindow().getDecorView().findViewById(R.id.activity_search_device));
        builder.settitleString("选择蓝牙设备").
                setLeftIconRes(R.drawable.left_img)
                .setleftIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).onCreatAndBind((ViewGroup) this.getWindow().getDecorView().findViewById(R.id.activity_search_device));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshRecyclerView();
        switch (requestCode) {
            case 1:
                Log.d("@@onActivityResult:", "case:1");
                if (resultCode == RESULT_OK) {
                    Log.d("@@onActivityResult:", "OK");
                } else if (requestCode == RESULT_CANCELED) {
                    Log.d("@@onActivityResult:", "cancele");
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_CANCELED) {
                    finish();
                }
                break;
            case SCAN_RESULT:
                if (resultCode==CommonScanActivity.SCAN_RESULT){
                    String address=data.getStringExtra("address");
                    Log.d("@@scanResult",data.getStringExtra("address"));
                    BluetoothDevice device=bluetoothAdapter.getRemoteDevice(address);
                    UIHelper.showLoadingDialog(this,address);
                    myBinder.connectDevice(device,this);
                }

        }
    }

    //监听蓝牙连接状态
    private Handler handler = new Handler() {
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

    };

    @Override
    public BlueToothCommuicationService.BluetoothBinder getBinder() {
        return null;
    }

    @Override
    public void connectFailed() {
        UIHelper.stopLoadingDialog("连接失败");
        DialogUtil.connectFail(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogLinearLayout.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        isConnected = false;
        UIHelper.ToastMessage("与蓝牙设备断开连接");

        if(dialogLinearLayout!=null){
            TextView tvConnect = (TextView) dialogLinearLayout.findViewById(R.id.tv_connect);
            ProgressBar progressBarConnect = (ProgressBar) dialogLinearLayout.findViewById(R.id.procressbar_connect);

            tvConnect.setVisibility(View.GONE);
            progressBarConnect.setVisibility(View.GONE);
        }

        isConnected = false;
    }

    @Override
    public void connectSuccess() {
        if (dialogLinearLayout!=null){
            TextView tvConnect = (TextView) dialogLinearLayout.findViewById(R.id.tv_connect);
            ProgressBar progressBarConnect = (ProgressBar) dialogLinearLayout.findViewById(R.id.procressbar_connect);
            tvConnect.setText("已连接");
            progressBarConnect.setVisibility(View.GONE);
        }

        UIHelper.showLoadingDialog(this, "正在同步设备信息");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    myBinder.getBaseData();//获取基本信息建表
                    myBinder.getPower();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Handler mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isConnected) {
                    UIHelper.stopLoadingDialog("");
                    DialogUtil.connectFail(SearchDeviceActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        };
        mHandler.postDelayed(runnable, 10000);

    }

    @Override
    public void notInitialize() {
        isConnected = false;
    }

    @Override
    public void getBaseDataFinish() {
        UIHelper.stopLoadingDialog("同步设备信息成功");
        UIHelper.ToastMessage("同步设备信息成功");
        myBinder.getScope();
        isConnected = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        if (state != null) {
            state.getRefresh();
            state = null;
        }
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(mReceiver);
        myBinder.cancelHelperInterface();//清空接口引用
        bluetoothAdapter.stopLeScan(leScanCallback);//停止蓝牙搜索
    }
}

package com.xcinfo.xc_blueteeth_android.main.monitor.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.common.utils.Utility;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.EditTelDialog;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.main.bean.Alarme;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.ChannelSimpleAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;

public class DeviceManagerActivity extends BaseActivity implements SettingHelper {

    @BindView(id=R.id.activity_device_manager,click = true)
    LinearLayout rootView;

    @BindView(id = R.id.left_img, click = true)
    private ImageView left_img;
    @BindView(id = R.id.tv_title)
    private TextView tv_title;
    @BindView(id = R.id.device_more_name_tv)
    private TextView tv_deviceName;
    @BindView(id = R.id.device_more_number_tv)
    private TextView tv_deviceSerial;
    @BindView(id = R.id.device_more_info_rl, click = true)
    private RelativeLayout relativeLayout;

    @BindView(id = R.id.deviceManager_listview)
    private ListView mListView;

    //设置界面
    @BindView(id = R.id.linearlayout_settime, click = true)
    LinearLayout linearlayoutSettime;
    @BindView(id = R.id.tv_settime, click = false)
    TextView tvSettime;
    @BindView(id = R.id.progressbar_settime, click = false)
    ProgressBar progressBarSettime;

    @BindView(id = R.id.linearlayout_getpower, click = true)
    LinearLayout linearlayoutGetpower;
    @BindView(id = R.id.tv_getpower, click = false)
    TextView tvGetpower;
    @BindView(id = R.id.progressbar_getpower, click = false)
    ProgressBar progressBarGetpower;

    @BindView(id = R.id.linearlayout_setalarmtype, click = true)
    LinearLayout linearlayoutSetalarmtype;
    @BindView(id = R.id.tv_setalarmtype, click = false)
    TextView tvSetAlarmtype;
    @BindView(id = R.id.progressbar_setalarmtype, click = false)
    ProgressBar progressBarSetAlarmtype;

    @BindView(id = R.id.linearlayout_getmtk, click = true)
    LinearLayout linearlayoutGetmtk;
    @BindView(id = R.id.tv_getmtk, click = false)
    TextView tvGetmtk;
    @BindView(id = R.id.progressbar_getmtk, click = false)
    ProgressBar progressBarGetmtk;

    @BindView(id = R.id.linearlayout_setsavetype, click = true)
    LinearLayout linearlayoutSetsavetype;
    @BindView(id = R.id.tv_setsavetype, click = false)
    TextView tvSetSavetype;
    @BindView(id = R.id.progressbar_setsavetype, click = false)
    ProgressBar progressBarSetSavetype;

    @BindView(id = R.id.linearlayout_setinterval, click = true)
    LinearLayout linealayoutsetinterval;
    @BindView(id = R.id.tv_setinterval, click = false)
    TextView tvSetinterval;
    @BindView(id = R.id.progressbar_setinterval, click = false)
    ProgressBar progressBarSetinterval;

    @BindView(id = R.id.linearlayout_showway, click = true)
    LinearLayout linealayoutsetshowway;
    @BindView(id = R.id.tv_showway, click = false)
    TextView tvSetshowway;
    @BindView(id = R.id.progressbar_showway, click = false)
    ProgressBar progressBarSetshowway;

    /*******************
     * 变量
     ***************************/
    android.support.v7.app.AlertDialog.Builder alarmTypebuilder;//对话框builder
    int alarmWhich = 0;
    android.support.v7.app.AlertDialog.Builder saveTypebuilder;//对话框builder
    int saveWhich = 0;
    android.support.v7.app.AlertDialog.Builder showWaybuilder;//对话框builder
    int showWhich = 0;
    android.support.v7.app.AlertDialog.Builder intervalBuilder;//对话框builder

    String interval;
    Alarme limitAlarme;//报警上下限
    int alarmePosition;//设置报警上下限的通道id
    //广播接收
    BroadcastReceiver broadcastReceiver;//收听蓝牙断开广播
    IntentFilter filter;

    private ChannelSimpleAdapter adapter;
    private Device device;
    private ChannelData alarme;
    private List<String> channelList = new ArrayList<>();
    private BlueToothCommuicationService.BluetoothBinder myBinder;
    private boolean flag = false;
    private boolean isBind;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (BlueToothCommuicationService.BluetoothBinder) service;
            isBind = true;
            myBinder.getService().setSettingHelper(DeviceManagerActivity.this);
            Log.d("@@ServiceSearchAct", "binded");

            //如果连接完毕则请求部分数据
            if (myBinder.isInitialize()){
                linearlayoutGetmtk.callOnClick();
            }else {
                setSettingEnable();
            }
        }
    };

    //设置界面不可点
    private void setSettingEnable(){
        rootView.setClickable(false);
        mListView.setClickable(false);
        linearlayoutGetmtk.setClickable(false);
        linearlayoutSettime.setClickable(false);
        linearlayoutGetpower.setClickable(false);
        linealayoutsetinterval.setClickable(false);
        linearlayoutSetsavetype.setClickable(false);
        linearlayoutSetalarmtype.setClickable(false);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_device_manager);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        parseIntent();
        initTitle();
        initSetting();//初始化设置部分监听
        initService();//初始化服务绑定
        initalarmBuilder();
        initSavetypeBuilder();
        initshowWayBuilder();
        initIntervalBuilder();
        initBroadcastReceiver();
    }

    //注册广播
    private void initBroadcastReceiver() {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DialogUtil.disConnected(DeviceManagerActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSettingEnable();
                        finish();
                    }
                });
            }
        };

        filter=new IntentFilter();

        filter.addAction(BlueToothCommuicationService.BLE_DISCONNECTED);
        registerReceiver(broadcastReceiver,filter);
    }

    private void initService() {
        Intent bindIntent = new Intent(this, BlueToothCommuicationService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    private void initSetting() {
        //读取设置信息
        initSettingMsg();

        linearlayoutSettime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.setTime();
                progressBarSettime.setVisibility(View.VISIBLE);
            }
        });

        linealayoutsetshowway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWhich=0;
                showWaybuilder.show();
            }
        });

        linearlayoutGetpower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.getPower();
                progressBarGetpower.setVisibility(View.VISIBLE);
            }
        });



        linearlayoutSetalarmtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmWhich = 0;
                alarmTypebuilder.show();
            }
        });

        linearlayoutGetmtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.getMtk();
                progressBarGetmtk.setVisibility(View.VISIBLE);
            }
        });



        linearlayoutSetsavetype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWhich = 0;
                saveTypebuilder.show();
            }
        });



        linealayoutsetinterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intervalBuilder.show();
            }
        });
    }

    //查询设置信息
    private void initSettingMsg(){

        alarme = SqliteUtil.getInstance(this).getAlarmeInfoBySerial(device.getDeviceSerial(), 0);

        Log.d("@@alarm", "alarmtype" + alarme.getAlarmType() + "savetype" + alarme.getSaveType());
        //查询设置记录
        if (alarme.getAlarmType() == 1) {
            tvSetAlarmtype.setText("报警方式：" + "累计报警");
        } else if (alarme.getAlarmType() == 2) {
            tvSetAlarmtype.setText("报警方式：" + "单次报警");
        } else {
            tvSetAlarmtype.setText("报警方式：" + "未设置");
        }


        //查询设置记录
        if (alarme.getSaveType() == 1) {
            tvSetSavetype.setText("设备存储满后：" + "存满覆盖");
        } else if (alarme.getAlarmType() == 2) {
            tvSetSavetype.setText("设备存储满后：" + "存满停止");
        } else {
            tvSetSavetype.setText("设备存储满后：" + "未设置");
        }

        tvSetinterval.setText("记录间隔："+SPUtils.get(this,"interval"+device.getDeviceSerial(),""+60));

        tvSetshowway.setText("显示方式："+getShowWay());
    }

    //创建报警方式builder
    private void initalarmBuilder() {
        alarmTypebuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alarmTypebuilder.setTitle("选择报警方式");
        alarmTypebuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(DeviceManagerActivity.this, "取消", Toast.LENGTH_SHORT).show();
            }
        });
        alarmTypebuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alarmWhich == 1)

                    myBinder.setAlarmType(false);
                else
                    myBinder.setAlarmType(true);
                dialog.dismiss();

            }
        });

        final String[] choice = new String[]{"累计多次报警", "单次报警"};
        alarmTypebuilder.setSingleChoiceItems(choice, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alarmWhich = which;
            }
        });

        alarmTypebuilder.create();
    }



    //创建存储方式builder
    private void initSavetypeBuilder() {
        saveTypebuilder = new android.support.v7.app.AlertDialog.Builder(this);
        saveTypebuilder.setTitle("选择存储方式");
        saveTypebuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        saveTypebuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveWhich==0){
                    myBinder.whenMemoryFull(true);
                }else {
                    myBinder.whenMemoryFull(false);
                }
                dialog.dismiss();
            }
        });

        final String[] choice = new String[]{"存满覆盖", "存满停止"};
        saveTypebuilder.setSingleChoiceItems(choice, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveWhich = which;

            }
        });

        saveTypebuilder.create();
    }

    //创建摄氏度华氏度方式builder
    private void initshowWayBuilder() {
        showWaybuilder = new android.support.v7.app.AlertDialog.Builder(this);
        showWaybuilder.setTitle("选择温度显示方式");
        showWaybuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        showWaybuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (showWhich==0){
                    myBinder.setDegree(true);
                }else {
                    myBinder.setDegree(false);
                }
                dialog.dismiss();
            }
        });

        final String[] choice = new String[]{"摄氏度", "华氏度"};
        showWaybuilder.setSingleChoiceItems(choice, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWhich = which;

            }
        });

        showWaybuilder.create();
    }

    private void initIntervalBuilder() {
        final View intervalDialog = LayoutInflater.from(this).inflate(R.layout.dialog_interval, null);
        intervalBuilder = new android.support.v7.app.AlertDialog.Builder(this) {
            @Override
            public android.support.v7.app.AlertDialog show() {

                if (intervalDialog.getParent() != null) {
                    ViewGroup parent = (ViewGroup) intervalDialog.getParent();
                    parent.removeView(intervalDialog);
                }
                return super.show();
            }
        };

        final EditText edInterval = (EditText) intervalDialog.findViewById(R.id.ed_interval);
        intervalBuilder.setTitle("请输入间隔");
        intervalBuilder.setView(intervalDialog);
        intervalBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        intervalBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String intervals;
                intervals = edInterval.getText().toString();
                if (!TextUtils.isEmpty(intervals))
                    myBinder.setInterval(intervals);
                interval = intervals;
                dialog.dismiss();
            }
        });


    }


    private void initTitle() {
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("设备管理");
        left_img.setVisibility(View.VISIBLE);
        if (device != null) {
            if (device.getDeviceName() != null && !device.getDeviceName().isEmpty())
                tv_deviceName.setText(device.getDeviceName());
            else
                tv_deviceName.setText("未命名设备");
            tv_deviceSerial.setText(device.getDeviceSerial());
        } else {

        }
        if (channelList.size() > 0) {
            initList();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder customDia = new AlertDialog.Builder(DeviceManagerActivity.this);
                final View viewDia = LayoutInflater.from(DeviceManagerActivity.this).inflate(R.layout.dialog_edit, null);
                final EditText editMax = (EditText) viewDia.findViewById(R.id.editTextmax);
                final EditText editMin = (EditText) viewDia.findViewById(R.id.editTextmin);
                editMax.setText("" + SqliteUtil.getInstance(DeviceManagerActivity.this).
                        getAlarmeInfoBySerial(device.getDeviceSerial(), position).getMaxLimit());
                editMin.setText("" + SqliteUtil.getInstance(DeviceManagerActivity.this).
                        getAlarmeInfoBySerial(device.getDeviceSerial(), position).getMinLimit());
                editMax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        editMax.setCursorVisible(hasFocus);
                    }
                });
                editMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        editMin.setCursorVisible(hasFocus);
                    }
                });
                customDia.setTitle("设置报警值");
                customDia.setView(viewDia);
                customDia.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        limitAlarme = new Alarme();
                        limitAlarme.setDevice_manager_name(DeviceManagerUtil.getDeviceManagerName(DeviceManagerActivity.this));
                        limitAlarme.setDeviceSerial(device.getDeviceSerial());
                        alarmePosition=position;
                        switch (position) {
                            case 0:
                                limitAlarme.setCHL1_maxLimit(Float.valueOf(editMax.getText().toString()));
                                limitAlarme.setCHL1_minLimit(Float.valueOf(editMin.getText().toString()));
                                myBinder.setAlarmLimit(limitAlarme.getCHL1_maxLimit(),limitAlarme.getCHL1_minLimit(),position);
                                break;
                            case 1:
                                limitAlarme.setCHL2_maxLimit(Float.valueOf(editMax.getText().toString()));
                                limitAlarme.setCHL2_minLimit(Float.valueOf(editMin.getText().toString()));
                                myBinder.setAlarmLimit(limitAlarme.getCHL2_maxLimit(),limitAlarme.getCHL2_minLimit(),position);
                                break;
                            case 2:
                                limitAlarme.setCHL3_maxLimit(Float.valueOf(editMax.getText().toString()));
                                limitAlarme.setCHL3_minLimit(Float.valueOf(editMin.getText().toString()));
                                break;
                            case 3:
                                limitAlarme.setCHL4_maxLimit(Float.valueOf(editMax.getText().toString()));
                                limitAlarme.setCHL4_minLimit(Float.valueOf(editMin.getText().toString()));
                                break;
                        }

                        dialog.dismiss();

                    }

                });
                customDia.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                customDia.create().show();
            }
        });
    }

    //设置adapter
    private void initList() {
        adapter = new ChannelSimpleAdapter(DeviceManagerActivity.this, device, channelList);
        mListView.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(mListView);
    }


    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.left_img:
                DeviceManagerActivity.this.finish();
                break;
            //修改设备的名称
            case R.id.device_more_info_rl:
                Intent intent = new Intent(DeviceManagerActivity.this, EditTelDialog.class);
                intent.putExtra(Constant.FLAG, Constant.CHANGE_DEVICE_NAME);
                intent.putExtra(Constant.DEVICE_SERIAL, device.getDeviceSerial());
                if (device.getDeviceName() != null && !device.getDeviceName().isEmpty()) {
                    intent.putExtra(Constant.DEVICE_NAME, device.getDeviceName());
                } else {
                    intent.putExtra(Constant.DEVICE_NAME, "未命名");
                }
                startActivityForResult(intent, 1234);
                break;
        }
    }

    private void parseIntent() {
        device = (Device) getIntent().getSerializableExtra(Constant.DEVICE_INFO_KEY);
        switch (device.getToatlChannelCount()) {
            case 0:
                break;
            case 1:
                channelList.add(device.getCHL1_type());
                break;
            case 2:
                channelList.add(device.getCHL1_type());
                channelList.add(device.getCHL2_type());
                break;
            case 3:
                channelList.add(device.getCHL1_type());
                channelList.add(device.getCHL2_type());
                channelList.add(device.getCHL3_type());
                break;
            case 4:
                channelList.add(device.getCHL1_type());
                channelList.add(device.getCHL2_type());
                channelList.add(device.getCHL3_type());
                channelList.add(device.getCHL4_type());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == Constant.EDITGROUP_NAME_RESULT_CODE) {
            tv_deviceName.setText(SqliteUtil.getInstance(DeviceManagerActivity.this).getDeviceName(device.getDeviceSerial()));
            setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(broadcastReceiver);//取消注册广播
    }

    /*service回调方法****************************************/

    @Override
    public void setTime(boolean isSuccess) {
        if (isSuccess) {
            tvSettime.setText("时间已同步");
            progressBarSettime.setVisibility(View.GONE);
        } else {
            tvSettime.setText("时间同步失败");
            progressBarSettime.setVisibility(View.GONE);
        }
    }

    @Override
    public void getPowerSuccess(int power) {

        tvGetpower.setText("设备电量：" + power);
        progressBarGetpower.setVisibility(View.GONE);
        linearlayoutSettime.callOnClick();


    }

    @Override
    public void getPowerFailed() {
        tvGetpower.setText("设备电量：" + "获取失败");
        progressBarGetpower.setVisibility(View.GONE);
    }

    @Override
    public void setAlarmType(boolean isSuccess) {
        //tvSetAlarmtype.setText("设置报警方式成功");
        switch (alarmWhich) {
            case 0:
                Alarme alarme = new Alarme();
                alarme.setDevice_manager_name(device.getDevice_manager_name());
                alarme.setDeviceSerial(device.getDeviceSerial());
                alarme.setAlarmType(1);
                SqliteUtil.getInstance(getApplicationContext()).upDateAlarme(alarme);
                UIHelper.ToastMessage("设置成功");
                initSettingMsg();
                break;
            case 1:
                alarme = new Alarme();
                alarme.setDevice_manager_name(device.getDevice_manager_name());
                alarme.setDeviceSerial(device.getDeviceSerial());
                alarme.setAlarmType(2);
                SqliteUtil.getInstance(getApplicationContext()).upDateAlarme(alarme);
                UIHelper.ToastMessage("设置成功");
                initSettingMsg();
                break;
        }
    }

    @Override
    public void getMtkSuccess(int mtk) {
        tvGetmtk.setText("获取mtk值：" + mtk);
        progressBarGetmtk.setVisibility(View.GONE);
        linearlayoutGetpower.callOnClick();
    }

    @Override
    public void getMtkFailed() {
        tvGetmtk.setText("获取mtk值：" + "获取失败");
        UIHelper.ToastMessage("设置成功");
        progressBarGetmtk.setVisibility(View.GONE);
    }

    @Override
    public void setSaveTypeSuccess() {
        switch (saveWhich) {
            case 0:
                Alarme alarme = new Alarme();
                alarme.setDevice_manager_name(device.getDevice_manager_name());
                alarme.setDeviceSerial(device.getDeviceSerial());
                alarme.setSavetype(1);
                SqliteUtil.getInstance(getApplicationContext()).upDateAlarme(alarme);
                UIHelper.ToastMessage("设置成功");
                initSettingMsg();
                break;
            case 1:
                alarme = new Alarme();
                alarme.setDevice_manager_name(device.getDevice_manager_name());
                alarme.setDeviceSerial(device.getDeviceSerial());
                alarme.setSavetype(2);
                SqliteUtil.getInstance(getApplicationContext()).upDateAlarme(alarme);
                UIHelper.ToastMessage("设置成功");
                initSettingMsg();
                break;

        }
    }

    @Override
    public void setSaveTypeFailed() {

        tvSetSavetype.setText("设备存储满后：" + "设置失败");
        UIHelper.ToastMessage("设置存储方式失败");
    }

    @Override
    public void setIntervalSuccess() {
        SPUtils.put(this,"interval"+device.getDeviceSerial(),interval);
        tvSetinterval.setText("设置间隔成功："+interval);

    }

    @Override
    public void setINtervalFailed() {
        UIHelper.ToastMessage("设置间隔失败");
        tvSetinterval.setText("设置间隔失败");
    }

    @Override
    public void setTempertureLimitSuccess() {
        UIHelper.ToastMessage("设置成功");
        if (SqliteUtil.getInstance(DeviceManagerActivity.this).hasAlarmByDeviceSerial(device.getDeviceSerial())) {
            SqliteUtil.getInstance(DeviceManagerActivity.this).upDateAlarme(limitAlarme, alarmePosition);
        } else {
            SqliteUtil.getInstance(DeviceManagerActivity.this).saveAlarme(limitAlarme);
        }
        initList();

    }

    @Override
    public void setTempertureLimitFailed() {
        UIHelper.ToastMessage("设置报警上下限失败");
        alarmePosition=-1;
    }

    @Override
    public void setShowWayFailed() {
        tvSetshowway.setText("设置显示方式失败");
    }

    @Override
    public void setShowWaySuccess() {
        if (showWhich==0){
            setShowWay(true);
        }else {
            setShowWay(false);
        }
        tvSetshowway.setText("设置显示方式成功："+getShowWay());
    }

    private String getShowWay(){
        boolean isDegree= (boolean) SPUtils.get(this,"isDegree"+device.getDeviceSerial(),true);
        if (isDegree){
            return "摄氏度显示";
        }else {
            return "华氏度显示";
        }
    }

    private void setShowWay(boolean isDegree){
            SPUtils.put(this,"isDegree"+device.getDeviceSerial(),isDegree);

    }

}

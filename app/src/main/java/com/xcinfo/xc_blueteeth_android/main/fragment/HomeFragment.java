package com.xcinfo.xc_blueteeth_android.main.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.GetScreenParameter;
import com.xcinfo.xc_blueteeth_android.common.utils.StatusColorUtils;
import com.xcinfo.xc_blueteeth_android.main.activity.MainActivity;
import com.xcinfo.xc_blueteeth_android.main.activity.manngerlistactivity.ManngerSelecterActivity;
import com.xcinfo.xc_blueteeth_android.main.activity.searchdevice.RefreshState;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.device_class.DeviceClassActivity;
import com.xcinfo.xc_blueteeth_android.main.activity.searchdevice.SearchDeviceActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.WarnHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements RefreshState{
    public static final String RESULT = "result";


    private LinearLayout deviceLinearlayout;//设备状态列表的LinearLayout
    private TextView normalDeviceTv;
    private TextView errorDeviceTv;
    private TextView exceptionDevice;
    private TextView offlineDevice;
    private TextView normalDeviceTv_number;
    private TextView errorDeviceTv_number;
    private TextView exceptionDevice_number;
    private TextView offlineDevice_number;
    private View adapterView;

    private int mOfflineNum = 5;//离线设备数
    private int mRunNormalNum = 4;//正常设备数
    private int mRunAlertNum = 6;//报警设备数
    private int mRunExceptionNum = 3;//异常设备数
    private int mDetectorSum = 20;//总设备数

    //四个状态栏
    private RelativeLayout normalRelativeLayout;
    private RelativeLayout waringRelativeLayout;
    private RelativeLayout exceptionRelativeLayout;
    private RelativeLayout offlineRelativeLayout;
    private RelativeLayout disConnectedRelativeLayout;

    //图表下的标记
    private RelativeLayout titleRelativelayout;
    //图标控件
    private PieChart mChart;

    //当前deviceMannger
    private static String currectMannger = "";
    private List<DeviceManager> manngerList = new ArrayList<>();
    private List<String> manngerNameList = new ArrayList<>();

    //顶部toolBar
    private LinearLayout centeritle;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView iv_select;
    private TextView tv_center;
    private TextView tv_status;

    //下拉刷新
    private SwipeRefreshLayout refreshLayout;

    //广播接收
    private BroadcastReceiver broadcastReceiver;

    private IntentFilter filter;


    float value[] = {mOfflineNum, mRunNormalNum, mRunAlertNum, mRunExceptionNum};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initDeviceState() {
        List<Device> devices= SqliteUtil.getInstance(getContext()).getAllDeviceListInManager(getCurrectMannger());
        for (Device device:devices){
            Log.d("@@initdevice",device.getDeviceName()+"  channeltype"+device.getChannelType());
            switch (device.getChannelType()){
                case 1:
                    WarnHelper.isWarn(getContext(),device.getDeviceSerial(),0,device);
                    break;
                case 2:
                    WarnHelper.isWarn(getContext(),device.getDeviceSerial(),0,device);
                    WarnHelper.isWarn(getContext(),device.getDeviceSerial(),1,device);
                    break;
            }

        }
        sendBroad();


    }

    private void sendBroad(){
        //发送跟新界面广播
        Intent intent=new Intent();
        intent.setAction(BlueToothCommuicationService.BLE_REFRESHUI);
        getActivity().sendBroadcast(intent);
        Log.d("@@homefragment","发送广播");

    }

    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void initContentView(View viewContent) {

        setViewSize(viewContent);//动态设置控件大小
        initView(viewContent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("@@HomeFragment", "onViewCreated");
        refreshView();
        //StatusColorUtils.setColor(getActivity(), getResources().getColor(R.color.white));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void initView(View viewContent) {
        normalDeviceTv = (TextView) viewContent.findViewById(R.id.device_tv_normal);
        TextPaint t = normalDeviceTv.getPaint();
        t.setFakeBoldText(true);
        errorDeviceTv = (TextView) viewContent.findViewById(R.id.device_tv_error);
        t = errorDeviceTv.getPaint();
        t.setFakeBoldText(true);
        exceptionDevice = (TextView) viewContent.findViewById(R.id.device_tv_exception);
        t = exceptionDevice.getPaint();
        t.setFakeBoldText(true);
        offlineDevice = (TextView) viewContent.findViewById(R.id.device_tv_offline);
        t = offlineDevice.getPaint();
        t.setFakeBoldText(true);


        normalDeviceTv_number = (TextView) viewContent.findViewById(R.id.device_number_normal);
        errorDeviceTv_number = (TextView) viewContent.findViewById(R.id.device_number_error);
        exceptionDevice_number = (TextView) viewContent.findViewById(R.id.device_number_exception);
        offlineDevice_number = (TextView) viewContent.findViewById(R.id.device_number_offline);


        normalRelativeLayout = (RelativeLayout) viewContent.findViewById(R.id.relativelayout_normal);
        normalRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceClassActivity.actionStart(getActivity(), currectMannger, 0);
            }
        });
        waringRelativeLayout = (RelativeLayout) viewContent.findViewById(R.id.relativelayout_warning);
        waringRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceClassActivity.actionStart(getActivity(), currectMannger, 1);
            }
        });
        exceptionRelativeLayout = (RelativeLayout) viewContent.findViewById(R.id.relativelayout_exception);
        exceptionRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceClassActivity.actionStart(getActivity(), currectMannger, 2);
            }
        });
        offlineRelativeLayout = (RelativeLayout) viewContent.findViewById(R.id.relativelayout_offline);
        offlineRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceClassActivity.actionStart(getActivity(), currectMannger, 3);
            }
        });

        disConnectedRelativeLayout= (RelativeLayout) viewContent.findViewById(R.id.relativelayout_disconnected);

        titleRelativelayout= (RelativeLayout) viewContent.findViewById(R.id.relative_title);


        mChart = (PieChart) viewContent.findViewById(R.id.piechart);

        centeritle = (LinearLayout) viewContent.findViewById(R.id.linearlayout_center);
        centeritle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelect();
            }
        });
        iv_left = (ImageView) viewContent.findViewById(R.id.iv_left);
        final Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.roate_anim);
        AccelerateInterpolator accelerateInterpolator=new AccelerateInterpolator();
        animation.setInterpolator(accelerateInterpolator);
        animation.setFillAfter(false);

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getMyBinder().getHistoryInformation();
                v.startAnimation(animation);
                Toast.makeText(getContext(), "请求历史数据", Toast.LENGTH_SHORT).show();
            }
        });
        iv_right = (ImageView) viewContent.findViewById(R.id.iv_right);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BlueToothCommuicationService.getHasConnectedDevice()!=null){
                    MainActivity.getMyBinder().disConnect();
                }
                SearchDeviceActivity.ActionStart(HomeFragment.this,getContext());
            }
        });
        tv_center = (TextView) viewContent.findViewById(R.id.tv_center);
        iv_select = (ImageView) viewContent.findViewById(R.id.iv_select);
        tv_status = (TextView) viewContent.findViewById(R.id.tv_status);

        //下拉刷新
        refreshLayout = (SwipeRefreshLayout) viewContent.findViewById(R.id.swipelayout_homefragment);
        refreshLayout.setColorSchemeResources(R.color.blue_main_ui);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPieChart();
                initDeviceState();

            }
        });

    }




    /**
     * 调整view大小
     *
     * @param viewContent
     */
    private void setViewSize(View viewContent) {
        deviceLinearlayout = (LinearLayout) viewContent.findViewById(R.id.device_list);
        LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) deviceLinearlayout.getLayoutParams();
        mLayoutParams.width = (int) (GetScreenParameter.getScreenWidth(getContext()) * 0.9);
        adapterView = viewContent.findViewById(R.id.adapter_view);
        ViewGroup.LayoutParams layoutParams = adapterView.getLayoutParams();
        layoutParams.height = (int) (GetScreenParameter.getScreenHeight(getContext()) * 0.04);
    }


    @Override
    public void initData() {

        super.initData();
        initBroadCast();

        tv_status.setText("未连接");
        changeConnectState(false);
        manngerList = SqliteUtil.getInstance(getContext()).getDeviceManagerList();
        for (DeviceManager mannger : manngerList) {
            manngerNameList.add(mannger.getName());
        }
        if (manngerList.size() > 0) {
            currectMannger = manngerList.get(0).getName();
            initDeviceState();//检查设备状态
            tv_center.setText(currectMannger);
            iv_select.setVisibility(View.VISIBLE);
            initPieChart();
        } else {
            tv_center.setText("本地无任何数据");
            Toast.makeText(getContext(), "数据库为空", Toast.LENGTH_SHORT).show();
            iv_select.setVisibility(View.GONE);
            tv_status.setVisibility(View.GONE);
            centeritle.setClickable(false);
        }


    }

    private void initBroadCast() {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshView();
            }
        };

        filter=new IntentFilter();
        filter.addAction(BlueToothCommuicationService.BLE_REFRESHUI);
        filter.addAction(BlueToothCommuicationService.BLE_DISCONNECTED);

        getActivity().registerReceiver(broadcastReceiver,filter);

    }


    /**
     * 切换设备
     */
    public void startSelect() {
        Intent intent = new Intent(getContext(), ManngerSelecterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ManngerSelecterActivity.MANNGERLIST, (ArrayList<String>) manngerNameList);
        intent.putExtra(ManngerSelecterActivity.DATABUNDEL, bundle);
        startActivityForResult(intent, ManngerSelecterActivity.CHOOSEMANNGER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case ManngerSelecterActivity.CHOOSEMANNGER:
                Log.d("@@CHOOSEMANNGER:", data.getStringExtra(HomeFragment.RESULT));
                currectMannger = data.getStringExtra(HomeFragment.RESULT);
                initPieChart();
                break;
        }
    }


    //其他位置获取当前mannger的方法
    public static String getCurrectMannger() {
        return currectMannger;
    }
    //请求历史数据刷新界面

    public  void refreshView(){
        if (BlueToothCommuicationService.getHasConnectedDevice()!=null) {
            changeConnectState(true);
            tv_status.setText("已连接");
            tv_center.setText(BlueToothCommuicationService.getHasConnectedDevice().getName());
            centeritle.setClickable(false);
            iv_select.setVisibility(View.GONE);

                currectMannger = BlueToothCommuicationService.getHasConnectedDevice().getName();
                initPieChart();
                //MainActivity.getMyBinder().getHistoryInformation();


        } else {
            changeConnectState(false);
            Log.d("@@refreshView","未连接");
            tv_status.setText("未连接");
            centeritle.setClickable(true);
            iv_select.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getRefresh() {
        Log.d("@@homefragment","getRefresh");
        refreshView();
        MainActivity.getMyBinder().getScope();
    }



    private void initPieChart() {
        tv_center.setText(currectMannger);
        mRunNormalNum = SqliteUtil.getInstance(getContext()).getAllDeviceListByState(currectMannger, 0).size();
        mRunAlertNum = SqliteUtil.getInstance(getContext()).getAllDeviceListByState(currectMannger, 1).size();
        mRunExceptionNum = SqliteUtil.getInstance(getContext()).getAllDeviceListByState(currectMannger, 2).size();
        mOfflineNum = SqliteUtil.getInstance(getContext()).getAllDeviceListByState(currectMannger, 3).size();

        normalDeviceTv_number.setText("" + mRunNormalNum);
        errorDeviceTv_number.setText("" + mRunAlertNum);
        exceptionDevice_number.setText("" + mRunExceptionNum);
        offlineDevice_number.setText("" + mOfflineNum);

        mDetectorSum = mRunNormalNum + mRunAlertNum + mRunExceptionNum + mOfflineNum;

        PieData mPieData = getPieData(4, value);
        showChart(mChart, mPieData, 1000);

    }


    //根据连接状态改变界面
    private void changeConnectState(boolean isConnected){
        if (isConnected){
             normalRelativeLayout.setVisibility(View.VISIBLE);
             waringRelativeLayout.setVisibility(View.VISIBLE);
             exceptionRelativeLayout.setVisibility(View.VISIBLE);
             offlineRelativeLayout.setVisibility(View.VISIBLE);
             disConnectedRelativeLayout.setVisibility(View.GONE);
             titleRelativelayout.setVisibility(View.VISIBLE);
             mChart.setVisibility(View.VISIBLE);
             //tv_center.setVisibility(View.VISIBLE);
        }else {
            normalRelativeLayout.setVisibility(View.GONE);
            waringRelativeLayout.setVisibility(View.GONE);
            exceptionRelativeLayout.setVisibility(View.GONE);
            offlineRelativeLayout.setVisibility(View.GONE);
            disConnectedRelativeLayout.setVisibility(View.VISIBLE);
            titleRelativelayout.setVisibility(View.GONE);
            mChart.setVisibility(View.GONE);
            //tv_center.setVisibility(View.GONE);
        }
    }

    /**
     * 图表相关
     *
     * @param pieChart
     * @param pieData
     * @param time
     */


    private void showChart(PieChart pieChart, PieData pieData, int time) {
        pieChart.setTransparentCircleColor(Color.WHITE);//透明圆环颜色

        pieChart.setHoleColorTransparent(false);// 设置中间空心圆孔的颜色是否透明

        pieChart.setHoleRadius(60f);  //半径

        pieChart.setTransparentCircleRadius(63f); // 半透明圈

//        pieChart.setHoleRadius(0);  //实心圆

        pieChart.setDescription("");//描述信息，处于圆饼图下部，不需要则设为""

        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字

        pieChart.setDrawHoleEnabled(true);//设置饼图中心是否是空心的

        pieChart.setRotationAngle(90); // 初始旋转角度

        pieChart.setRotationEnabled(true); // 可以手动旋转

        pieChart.setUsePercentValues(true);  //显示成百分比

        pieChart.setCenterText(String.valueOf(mDetectorSum) + "台\n" + "台数");  //饼状图中间的文字

        pieChart.setData(pieData);        //设置数据

//        右上角的色块提示演示
        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //最右边显示
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        mLegend.setEnabled(false);//隐藏比例图

        pieChart.animateXY(time, time);  //设置动画
        // mChart.spin(2000, 0, 360);

        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private PieData getPieData(int count, float[] value) {
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容

        for (int i = 0; i < count; i++) {
            xValues.add("");  //下方显示
        }

        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
//        float quarterly1 = 14;
//        float quarterly2 = 14;
//        float quarterly3 = 34;
//        float quarterly4 = 38;
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (mOfflineNum != 0) {

            yValues.add(new Entry(value[0], 0));//饼图对应数据
            colors.add(Color.rgb(201, 201, 202));//灰色
        }
        if (mRunNormalNum != 0) {
            yValues.add(new Entry(value[1], 1));//饼图对应数据
            colors.add(Color.rgb(94, 201, 91));//绿色
        }
        if (mRunAlertNum != 0) {
            yValues.add(new Entry(value[2], 2));//饼图对应数据
            colors.add(Color.rgb(239, 91, 87));//红色
        }
        if (mRunExceptionNum != 0) {
            yValues.add(new Entry(value[3], 3));//饼图对应数据
            colors.add(Color.rgb(238, 177, 49));//黄色
        }
//        if (mRepairNum != 0) {
//            yValues.add(new Entry(value[4], 4));//饼图对应数据
//            colors.add(Color.rgb(82, 144, 237));//蓝色
//        }


        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "Quarterly Revenue 2014"/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());//给数据添加百分号
        return pieData;
    }



}

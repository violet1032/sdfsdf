package com.xcinfo.xc_blueteeth_android.main.monitor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BasicFragment;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.DbCallback;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.StringUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.DeviceManagerActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.ChannelListAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.RecyclerAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.WarnHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycy on 2017/03/19.
 */
public class CurrentFragment extends BasicFragment implements DbCallback {
    private ImageView left_img;
    //标题
    private TextView tv_title;
    //标题栏右边的文字
    private TextView tv_right;
    //当前的实时值
    private TextView tv_current_value;
    //当前通道的单位
    private TextView tv_unit;
    private TextView tv_device_state;
    private RecyclerView mRecycler;
    private TextView tv_channel_type;
    private TextView tv_chartUnit;
    private LineChart mLineChart;
    private Button btn_fixMap;
    private Button btn_listView;
    private ListView mListview;
    private TextView tvRange;

    //广播接收
    BroadcastReceiver broadcastReceiver;

    IntentFilter filter;

    /*************************变量*****************************************/
    private Device device;
    private List<GroupChannel>channels;
    private List<HistoryData>dataList;
    private HistoryData historyData;
    private Handler mHandler;

//    private LineData mLineData;                            //数据源
//    private XAxis xAxis;                                 //X坐标轴
//    private YAxis yAxis;                                 //Y坐标轴
//    private List<Entry> yAxisDataList;                   //y轴上坐标值数据
//    private int mapShowDataNum=0;                   //图表上显示的点的个数
    private int channelId=0;
    private int showModel=0;                                //0是以图表显示，1以列表的方式显示



    @Override
    public int getContentView() {
        return R.layout.fragment_current;
    }

    private void initHaneler(){
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        tvRange.setText(TimeUtil.getToHourTime().substring(10)+" --"+TimeUtil.getNowTime().substring(10));
                        if(dataList!=null&&dataList.size()>0){
                        if(showModel==0){
                            tv_chartUnit.setVisibility(View.VISIBLE);
                            mLineChart.setVisibility(View.VISIBLE);
                            createChart();
                            mListview.setVisibility(View.GONE);
                        }else{
                            tv_chartUnit.setVisibility(View.GONE);
                            mLineChart.setVisibility(View.GONE);
                            mListview.setVisibility(View.VISIBLE);
                            mListview.setAdapter(new ChannelListAdapter(getActivity(),dataList,tv_channel_type.getText().toString(),tv_unit.getText().toString()));
                        }}
                        break;
                    //显示进度条
                    case Constant.SHOW_PROGRESS:
                        UIHelper.showLoadingDialog(getActivity(),null);
                        break;
                    //关闭进度条
                    case Constant.STOP_PROGRESS:
                        UIHelper.stopLoadingDialog(null);
                        break;
                    case Constant.NO_DATA:
                        UIHelper.stopLoadingDialog(null);
                        UIHelper.ToastMessage("暂时没有数据更新");
                        break;
                }
            }
        };
    }
    /*
            * 初始化需要的参数
            * */
    private void initArguments(){
        device= (Device) getArguments().getSerializable(Constant.DEVICE_INFO_KEY);
        channels= device.getGroupChannels();
    }

    //检查报警
    private void isWarn(final int channelId){

        if (device.getTime()!=null){
            WarnHelper.isWarn(getActivity(),device.getDeviceSerial(),channelId,device);
        }else {
            Log.d("@@iswarn","gettime==null");
        }


    }
    @Override
    public void initWidget(View parentView) {
        initArguments();
        initHaneler();
        //控件实例化
        left_img=(ImageView)parentView.findViewById(R.id.left_img);//左边的回退按钮
        tv_title= (TextView) parentView.findViewById(R.id.title_tv);//标题
        tv_right= (TextView) parentView.findViewById(R.id.right_tv);//单位
        tv_unit= (TextView) parentView.findViewById(R.id.current_channel_data_type_unit_tv);
        tv_current_value= (TextView) parentView.findViewById(R.id.current_channel_data_high_tv);//实时值
        tv_channel_type =(TextView)parentView.findViewById(R.id.current_channel_type_tv);//类型
        tv_device_state =(TextView)parentView.findViewById(R.id.channel_data_status_tv);//状态
        mRecycler =(RecyclerView)parentView.findViewById(R.id.current_rv_states);//横向的列表
        tv_chartUnit =(TextView)parentView.findViewById(R.id.tv_lineChart_unit);//表格的类型和单位
        mLineChart =(LineChart)parentView.findViewById(R.id.channel_fix_map_linechart);
        btn_fixMap =(Button)parentView.findViewById(R.id.current_fix_map_btn);//固定曲线
        btn_listView =(Button)parentView.findViewById(R.id.current_list_btn);//列表模式显示
        mListview =(ListView)parentView.findViewById(R.id.channel_data_lv);//展示历史数据的ListView
        tvRange= (TextView) parentView.findViewById(R.id.tv_time_range);

        //设置控件监听
        left_img.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        btn_fixMap.setOnClickListener(this);
        btn_listView.setOnClickListener(this);
        initView();
    }
    @Override
    public void initData() {
        super.initData();

        //初始化状态
        initDeviceState();
        initChannelInfo(channelId);
        initBroadCast();

    }

    private void initBroadCast() {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initArguments();
                initDeviceState();
            }
        };

        filter=new IntentFilter();
        filter.addAction(BlueToothCommuicationService.BLE_REFRESHUI);
        //filter.addAction(BlueToothCommuicationService.BLE_DISCONNECTED);

        getActivity().registerReceiver(broadcastReceiver,filter);

    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        }catch (Exception e){

        }

        super.onDestroy();
    }

    private void initView(){
        changeShowModel(true);
        //初始化title
        initTitle();

    }

    private void initTitle(){
        tv_title.setText(device.getDeviceName());
        tv_title.setVisibility(View.VISIBLE);
        tv_right.setText("管理");
        tv_right.setVisibility(View.VISIBLE);
        if(device.getGroupChannels().size()>0){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            final RecyclerAdapter adapter=new RecyclerAdapter(getActivity(),channels);
            adapter.setOnItemClickListener(new RecyclerAdapter.MyonItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    channelId=position;
                    //getChannelDateFromDB();
                    adapter.setSelect(position);
                    initChannelInfo(position);
                    adapter.notifyDataSetChanged();

                }
            });
            mRecycler.setAdapter(adapter);
            mRecycler.setLayoutManager(linearLayoutManager);
        }
    }
    /*
    * 设置通道信息
    * */
    private void initChannelInfo(int channelId){
        switch (channelId){
            case 0:
                tv_current_value.setText(""+device.getCHL1_current());
                tv_unit.setText(device.getCHL1_unit());

                tv_channel_type.setText(device.getCHL1_type());
                tv_chartUnit.setText(device.getCHL1_type()+"("+device.getCHL1_unit()+")");
                if (DeviceManagerUtil.getIsFahrenheit(getActivity())){
                    tv_unit.setText("℉");
                    tv_chartUnit.setText(device.getCHL1_type()+"("+"℉"+")");

                }
                break;
            case 1:
                tv_current_value.setText(""+device.getCHL2_current());
                tv_unit.setText(device.getCHL2_unit());
                tv_channel_type.setText(device.getCHL2_type());
                tv_chartUnit.setText(device.getCHL2_type()+"("+device.getCHL2_unit()+")");
                break;
            case 2:
                tv_current_value.setText(""+device.getCHL3_current());
                tv_unit.setText(device.getCHL3_unit());
                tv_channel_type.setText(device.getCHL3_type());
                tv_chartUnit.setText(device.getCHL3_type()+"("+device.getCHL3_unit()+")");
                break;
            case 3:
                tv_current_value.setText(""+device.getCHL4_current());
                tv_unit.setText(device.getCHL4_unit());
                tv_channel_type.setText(device.getCHL4_type());
                tv_chartUnit.setText(device.getCHL4_type()+"("+device.getCHL4_unit()+")");
                break;
        }
        getChannelDateFromDB();
    }
    private void initDeviceState(){
        switch (device.getDeviceState()){
            case 0:
                tv_device_state.setText("正常");
                tv_device_state.setTextColor(getResources().getColor(R.color.channel_status_normal));
                break;
            case 1:
                tv_device_state.setText("报警");
                tv_device_state.setTextColor(getResources().getColor(R.color.channel_status_warning));
                break;
            case 2:
                tv_device_state.setText("异常");
                tv_device_state.setTextColor(getResources().getColor(R.color.channel_status_abnormal));
                break;
            case 3:
                tv_device_state.setText("离线");
                tv_device_state.setTextColor(getResources().getColor(R.color.channel_status_off));
                break;
        }
    }


    /*
    * 从数据库查询记录
    * */

    private void getChannelDateFromDB(){
        SqliteUtil.getInstance(getActivity()).getChannelDataMaxAndMin("serial"+device.getDeviceSerial(),channelId,this);
    }
    /*
    * 点击事件
    * */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.left_img:
                getActivity().finish();
                break;
            case R.id.current_list_btn:
                changeShowModel(false);
                showModel=1;
                Message message=new Message();
                message.what=1;
                mHandler.sendMessage(message);
                break;
            case R.id.current_fix_map_btn:
                changeShowModel(true);
                showModel=0;
                Message message1=new Message();
                message1.what=1;
                mHandler.sendMessage(message1);
                break;
            case R.id.right_tv:
                if(device!=null){
                    Intent intent=new Intent(getActivity(), DeviceManagerActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(Constant.DEVICE_INFO_KEY,device);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,0);
                }else{

                }

                break;
        }
    }
    private void createChart(){
        Log.d("@@","createChart");
        mLineChart.clear();
        mLineChart.setDescription("");
        mLineChart.setNoDataTextDescription("暂时尚无数据");

        mLineChart.setTouchEnabled(false);

        // 可拖曳
        mLineChart.setDragEnabled(false);

        // 可缩放
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setPinchZoom(false);

        // 设置图表的背景颜色
        mLineChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.BLUE);

        // 先增加一个空的数据，随后往里面动态添加
        mLineChart.setData(data);

        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mLineChart.getLegend();

        // 可以修改图表注解部分的位置
        // l.setPosition(LegendPosition.LEFT_OF_CHART);

        // 线性，也可是圆
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(6f);// 字体
        // 颜色
        l.setTextColor(Color.WHITE);
        l.setEnabled(false);

        //y轴坐标

        // x坐标轴
        XAxis xl = mLineChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setLabelsToSkip(0);
        // 几个x坐标轴之间才绘制？
        xl.setSpaceBetweenLabels(5);
        // 如果false，那么x坐标轴将不可见
        xl.setEnabled(true);

        // 将X坐标轴放置在底部，默认是在顶部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 图表左边的y坐标轴线
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.removeAllLimitLines();         //先移除所有警戒线
//        if (warnRulesList != null) {
//            LimitLine limitLine = null;
//            for (int i = 0; i < warnRulesList.size(); i++) {
//                //Y轴的警戒线
//                limitLine = new LimitLine(Float.parseFloat(warnRulesList.get(i).getLimitValue()), "");
//                limitLine.setLineColor(Color.RED);
//                limitLine.setLineWidth(1f);
//                limitLine.setTextSize(8f);
//                leftAxis.addLimitLine(limitLine);        //设置警戒线
//            }
//        }
//        if (history.getCoordinateAxis() > 0) {
//            int count = (history.getCoordinateMax() - history.getCoordinateMin()) / history.getCoordinateAxis();
//            leftAxis.setLabelCount(count + 1, true);
//        } else {
//            UIHelper.ToastMessage("服务器数据异常");
//        }
        leftAxis.setLabelCount(5, true);
        mLineChart.getAxisRight().setEnabled(false);//不显示右边的纵坐标轴
        leftAxis.setGridColor(getResources().getColor(R.color.transparent));
        leftAxis.setStartAtZero(false);
        //Log.d("@@","maxValue:"+historyData.getMaxValue());
        if(historyData.getMaxValue()<=20){
            leftAxis.setAxisMaxValue(historyData.getMaxValue()+3);    //设置Y轴坐标最大为多少
        }else if(historyData.getMaxValue()>20&&historyData.getMaxValue()<=50){
            leftAxis.setAxisMaxValue(historyData.getMaxValue()+6);    //设置Y轴坐标最大为多少
        }else {
            leftAxis.setAxisMaxValue(historyData.getMaxValue());
        }

        leftAxis.setAxisMinValue(0);    //设置Y轴坐标最小为多少

        leftAxis.setAxisLineWidth(1f);   //设置Y轴宽度
        leftAxis.setTextSize(8f);        //Y轴字体大小
        leftAxis.setAxisLineColor(Color.rgb(5, 97, 188));
        // 不一定要从0开始
        leftAxis.setStartAtZero(true);

        leftAxis.setDrawGridLines(true);

//        YAxis rightAxis = mLineChart.getAxisRight();
//        // 不显示图表的右边y坐标轴线
//        rightAxis.setEnabled(false);
        addEntry();
    }
    private void addEntry() {
        LineData data = mLineChart.getData();
        data.setDrawValues(false);//是否显示节点上的数据
        // 每一个LineDataSet代表一条线，每张统计图表可以同时存在若干个统计折线，这些折线像数组一样从0开始下标。
        // 本例只有一个，那么就是第0条折线
        LineDataSet set = data.getDataSetByIndex(0);
        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
        if (set == null) {
            //Log.d("@@","createLineDataSet");
            set = createLineDataSet();
            data.addDataSet(set);
        }
        // 先添加一个x坐标轴的值
        // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1
        data.addXValue(" ");
        int dataNum = dataList.size();
        for (int i = 0; i < dataNum; i++) {
            if (i % (dataNum /5+ 1) == 0) {
                //Log.d("@@",dataList.get(i).getTime().substring(5,16));
                data.addXValue(dataList.get(i).getTime().substring(5,16));
            } else {
                data.addXValue("");
            }
            Entry entry = new Entry(dataList.get(i).getValue(), set.getEntryCount());
            //Log.d("@@",dataList.get(i).getValue()+"  ");
            data.addEntry(entry, 0);
        }
        // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。
        // 因为本例只有一个统计折线，那么就是第一个，其下标为0.
        // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。


        // 像ListView那样的通知数据更新
        mLineChart.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
//        mLineChart.setVisibleXRangeMaximum(dataNum);
        mLineChart.setVisibleXRangeMaximum(dataNum);

        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        mLineChart.moveViewToX(data.getXValCount());

        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }
    // 初始化数据集，添加一条统计折线，可以简单的理解是初始化y坐标轴线上点的表征
    private LineDataSet createLineDataSet() {
        LineDataSet set = new LineDataSet(null, "历史");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(getResources().getColor(R.color.blue_main_ui));
        //设置线宽
        set.setLineWidth(0.7f);
        //设置圆的大小
        set.setCircleSize(1.5f);
        set.setColor(Color.rgb(5, 97, 188));// 显示颜色
        set.setCircleColor(Color.rgb(5, 97, 188));// 圆形的颜色
        set.setHighLightColor(Color.BLUE);
        set.setDrawValues(false);
        set.setDrawCircles(true);
        set.setValueFormatter(new MyFormat());
        return set;
    }
    public class MyFormat implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
            return value + "";
        }
    }
//    private void createChart(LineChart mLineChart) {
//        mLineChart.clear();
//        mLineData = getLineData();
//        showChart(mLineChart, mLineData);
//        mLineChart.invalidate(); //刷新重绘图表
//    }
//    private void showChart(LineChart mLineChart, LineData lineData) {
//        xAxis = null;
//        yAxis = null;
//        xAxis = mLineChart.getXAxis();
//        yAxis = mLineChart.getAxisLeft();
//        mLineChart.setDrawBorders(false);  //是否在折线图上添加边框
//        mLineChart.setDescription("");// 数据描述
//
//        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
//        mLineChart.setNoDataTextDescription("");
//
//        mLineChart.setDrawGridBackground(false); // 是否显示表格颜色
//
//        mLineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度
//
//        mLineChart.setTouchEnabled(false); // 设置是否可以触摸
//
//        mLineChart.setDragEnabled(false);// 是否可以拖拽
//
//        mLineChart.setScaleEnabled(false);// 是否可以缩放
//
//        // if disabled, scaling can be done on x- and y-axis separately
//        mLineChart.setPinchZoom(false);
//
//        mLineChart.setBackgroundColor(Color.TRANSPARENT);// 设置背景
//        if (false) {
//            mapShowDataNum = dataList.size();
//        }
//        mLineChart.setVisibleXRangeMaximum(mapShowDataNum);//一个界面最多显示的条数
//        Log.e("mapShowDataNum", mapShowDataNum + "'");
//        mLineChart.moveViewToX(dataList.size());//移动界面视图到最右
//
//        // add data
//        mLineChart.setData(lineData); // 设置数据
//        Legend mLegend = mLineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
//
//        // modify the legend ...
//        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
//        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
//        mLegend.setFormSize(6f);// 字体
//        mLegend.setTextColor(Color.TRANSPARENT);// 颜色
//        //mLegend.setTypeface(mTf);// 字体
//        mLegend.setEnabled(false);
//
//        mLineChart.animateX(0); // 立即执行的动画,x轴
//
//        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        yAxis.removeAllLimitLines();         //先移除所有警戒线
////        if (warnRulesList != null) {
////            LimitLine limitLine = null;
////            List<Float> listLow = new ArrayList<Float>();
////            List<Float> listHigh = new ArrayList<Float>();
////            for (int i = 0; i < warnRulesList.size(); i++) {
////                //Y轴的警戒线
////                if (warnRulesList.get(i).getCompareType() == 1) {             //下限值
////                    listLow.add(Float.parseFloat(warnRulesList.get(i).getLimitValue()));
////                } else if (warnRulesList.get(i).getCompareType() == 0) {       //上限值
////                    listHigh.add(Float.parseFloat(warnRulesList.get(i).getLimitValue()));
////                }
////            }
////            if (listLow.size() != 0) {
////                Collections.sort(listLow);
////                limitLine = new LimitLine(listLow.get(listLow.size() - 1), "");
////                limitLine.setLineColor(Color.RED);
////                limitLine.setLineWidth(1f);
////                limitLine.setTextSize(8f);
////                yAxis.addLimitLine(limitLine);        //设置警戒线
////            }
////            if (listHigh.size() != 0) {
////                Collections.sort(listHigh);
////                limitLine = new LimitLine(listHigh.get(0), "");
////                limitLine.setLineColor(Color.RED);
////                limitLine.setLineWidth(1f);
////                limitLine.setTextSize(8f);
////                yAxis.addLimitLine(limitLine);        //设置警戒线
////            }
////        }
//
////        if (historyData.getCoordinateAxis() > 0) {
////            int count = (historyData.getCoordinateMax() - historyData.getCoordinateMin()) / historyData.getCoordinateAxis();
////            yAxis.setLabelCount(count + 1, true);
////        } else {
////            UIHelper.ToastMessage("服务器数据异常");
////        }
//
//        mLineChart.getAxisRight().setEnabled(false);
//        yAxis.setGridColor(getResources().getColor(R.color.transparent));
//        yAxis.setStartAtZero(false);
//        yAxis.setAxisMaxValue(historyData.getMaxValue());    //设置Y轴坐标最大为多少
//        yAxis.setAxisMinValue(0);    //设置Y轴坐标最小为多少
//        yAxis.setAxisLineWidth(1f);   //设置Y轴宽度
//        yAxis.setTextSize(8f);        //Y轴字体大小
//        yAxis.setAxisLineColor(Color.rgb(5, 97, 188));
//        //x轴的操作
//        xAxis.setLabelsToSkip(0);
//        xAxis.setGridColor(getResources().getColor(R.color.transparent));
//        xAxis.setAxisLineColor(Color.rgb(34, 116, 196));
//        xAxis.setAxisLineWidth(1f);
//        xAxis.setTextSize(8f);
//    }
//
//    private LineData getLineData() {
//        ArrayList<String> xValues = new ArrayList<String>();
//        yAxisDataList = new ArrayList<Entry>();
//         //固定曲线
//            int dataNum = dataList.size();
//            for (int i = 0; i < dataList.size(); i++) {
//                if (i % (dataNum / 5 + 1) == 0) {
//                    xValues.add(TimeUtil.timeFormat(dataList.get(i).getTime()));
//                } else {
//                    xValues.add("");
//                }
//                yAxisDataList.add(new Entry(dataList.get(i).getValue(), i));
//                }
//
//
//
//        // y轴的数据集合
//        LineDataSet lineDataSet = new LineDataSet(yAxisDataList, "测试折线图" /*显示在比例图上*/);
//        //用y轴的集合来设置参数
//        lineDataSet.setLineWidth(0.7f); // 线宽
//        lineDataSet.setCircleSize(1.5f);// 显示的圆形大小
//        lineDataSet.setColor(Color.rgb(5, 97, 188));// 显示颜色
//        lineDataSet.setCircleColor(Color.rgb(5, 97, 188));// 圆形的颜色
//        lineDataSet.setHighLightColor(Color.BLUE); // 高亮的线的颜色
//        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
//        lineDataSets.add(lineDataSet); // add the datasets
//        // create a data object with the datasets
//        LineData lineData = new LineData(xValues, lineDataSets);
//        lineData.setDrawValues(false);//是否显示节点上的数据
//        lineData.setValueFormatter(new MyFormat());
//        return lineData;
//    }
//
//    public class MyFormat implements ValueFormatter {
//        @Override
//        public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
//            return value + "";
//        }
//    }

    @Override
    public void showProcess() {
        Message message=new Message();
        message.what=Constant.SHOW_PROGRESS;
        mHandler.sendMessage(message);
    }

    @Override
    public void onSuccess(List<HistoryData> dataList) {
        this.dataList=dataList;
        device.setTime(dataList.get(dataList.size()-1).getTime());
        //isWarn(channelId);//检查报警(现在是放在service中的)
        if(dataList.size()>0){
            Message message=new Message();
            message.what=1;
            mHandler.sendMessage(message);
        }else{
            //暂时没有数据的处理
            Log.d("@@onSuccess","数据条数小雨");
        }
    }

    @Override
    public void stopProcess() {
        Message message=new Message();
        message.what=Constant.STOP_PROGRESS;
        mHandler.sendMessage(message);
    }

    @Override
    public void onFailure() {

    }
    @Override
    public void onHistorySuccess(HistoryData history) {
        historyData=history;
        //查询最大最小值结束
        SqliteUtil.getInstance(getActivity()).getChannelDataListByTime("serial"+device.getDeviceSerial(),channelId,TimeUtil.getToHourTime(),TimeUtil.getNowTime(),this);
        Log.d("@@ByTime",TimeUtil.getToHourTime()+TimeUtil.getNowTime());
    }


    @Override
    public void noData() {
       // UIHelper.ToastMessage("暂时没有数据更新");
        Message message=new Message();
        message.what=Constant.NO_DATA;
        mHandler.sendMessage(message);
    }

    private void changeShowModel(boolean flag){
        if(flag){
            btn_fixMap.setTextColor(getResources().getColor(R.color.white));
            btn_fixMap.setBackgroundResource(R.drawable.shape_round_monirotning_click);
            btn_listView.setTextColor(getResources().getColor(R.color.main_balck));
            btn_listView.setBackgroundResource(R.drawable.shape_round_monirotning);
        }else{
            btn_fixMap.setTextColor(getResources().getColor(R.color.main_balck));
            btn_fixMap.setBackgroundResource(R.drawable.shape_round_monirotning);
            btn_listView.setTextColor(getResources().getColor(R.color.white));
            btn_listView.setBackgroundResource(R.drawable.shape_round_monirotning_click);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==Constant.EDITGROUP_NAME_RESULT_CODE){
            if(device!=null)
            tv_title.setText(SqliteUtil.getInstance(getActivity()).getDeviceName(device.getDeviceSerial()));
        }
    }
}


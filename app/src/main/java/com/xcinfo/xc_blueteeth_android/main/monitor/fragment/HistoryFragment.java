package com.xcinfo.xc_blueteeth_android.main.monitor.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.xcinfo.widget.wheelpicker.ui.DatePickerDialog;
import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.common.activity.BasicFragment;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.DbCallback;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.ChannelListAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.RecyclerAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;

import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycy on 2017/03/19
 */
public class HistoryFragment extends BasicFragment implements DbCallback {

    //控件
    private ImageView left_img;
    //标题
    private TextView tv_title;
    //标题栏右边的文字
    private RecyclerView mRecycler;//湿度温度选项的recyclerview
    private TextView tv_chartUnit;
    private LineChart mLineChart;
    private Button btn_fixMap;
    private Button btn_listView;
    private ListView mListview;
    private TextView tv_maxValue;
    private TextView tv_minValue;
    private TextView tv_minVlaueUnit;
    private TextView tv_maxVlaueUnit;
    private Button btn_query;
    private LinearLayout relayout_endTime;
    private LinearLayout layout_startTime;
    private TextView tv_endTime;
    private TextView tv_startTime;
    private TextView tv_oneDay;
    private TextView tv_oneWeek;
    private TextView tvRecoedCount;

    /*************************变量*****************************************/
    private Device device;
    private List<GroupChannel>channels;
    private List<HistoryData>dataList;
    private HistoryData historyData;
    private Handler mHandler;

    private LineData mLineData;                            //数据源
    private XAxis xAxis;                                 //X坐标轴
    private YAxis yAxis;                                 //Y坐标轴
    private List<Entry> yAxisDataList;                   //y轴上坐标值数据
    private int mapShowDataNum=0;                   //图表上显示的点的个数
    private int channelId=0;
    private int showModel=0;                                //0是以图表显示，1以列表的方式显示
    private String channelType;
    private int chooseDate=1;

    //

    @Override
    public int getContentView() {
        return R.layout.fragment_history;
    }

    private void initHaneler(){
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        if (dataList!=null)
                        tvRecoedCount.setText("当前条数："+dataList.size());
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
                                mListview.setAdapter(new ChannelListAdapter(getActivity(),dataList,channelType,tv_maxVlaueUnit.getText().toString()));
                            }
                        }else{
                            UIHelper.ToastMessage("暂时没有数据");
                        }

                        break;
                    case DatePickerDialog.MESSAGE_WHAT_CHOOSE_DATE:
                        String dateTime=msg.obj.toString();
                        //起始时间
                        if(chooseDate==1){
                            if(!TimeUtil.isOverNowTime(dateTime)){
                                tv_startTime.setText(dateTime.substring(0,dateTime.length()-3));
                            }else{
                                UIHelper.ToastMessage("时间不能是未来");
                                tv_startTime.setText(TimeUtil.getToHourTime().substring(0,16));
                            }
                        }
                        //结束时间
                        if(chooseDate==2){
                            long result;
                            if(!TimeUtil.isOverNowTime(dateTime)){
                                tv_endTime.setText(dateTime.substring(0,dateTime.length()-3));
                            }else{
                                UIHelper.ToastMessage("时间不能是未来");
                                tv_endTime.setText(TimeUtil.getNowDatetime().substring(0,16));
                            }
                            result=TimeUtil.dealTime(tv_startTime.getText().toString(),tv_endTime.getText().toString());
                            if(result<0){
                                UIHelper.ToastMessage("起始时间不能大于终止时间");
                                initTime();
                            }
                         }
                        break;
                    //显示进度条
                    case Constant.SHOW_PROGRESS:
                        Log.e("show",TimeUtil.getNowDatetime());
                        UIHelper.showLoadingDialog(getActivity(),null);
                        break;
                    //关闭进度条
                    case Constant.STOP_PROGRESS:
                        Log.e("stop",TimeUtil.getNowDatetime());
                        UIHelper.stopLoadingDialog(null);
                        break;
                    //刷新界面
                    case Constant.REFRESH_UI:
                        Log.d("@@refreshUI"," "+historyData.getMaxValue()+" "+historyData.getMinValue());

                        if(historyData.getMaxValue()==0.0){
                            tv_maxValue.setText("N/A");
                        }else{
                            tv_maxValue.setText(""+historyData.getMaxValue());
                        }
                        if(historyData.getMinValue()==0.0){
                            tv_minValue.setText("N/A");
                        }else{
                            tv_minValue.setText(""+historyData.getMinValue());
                        }
                        break;
                    case Constant.NO_DATA:
                        UIHelper.ToastMessage("暂时没有内容更新");
                        break;
                }
            }
        };
    }
    @Override
    public void initWidget(View parentView) {
        initArguments();
        initHaneler();
        //控件实例化
        left_img=(ImageView)parentView.findViewById(R.id.left_img);//左边的回退按钮
        tv_title= (TextView) parentView.findViewById(R.id.title_tv);//标题

        tv_maxValue =(TextView)parentView.findViewById(R.id.history_channel_data_high_tv);//历史记录最大值
        tv_maxVlaueUnit =(TextView)parentView.findViewById(R.id.history_channel_data_type_tv1);
        tv_minValue =(TextView)parentView.findViewById(R.id.history_channel_data_low_tv);//历史记录最低值
        tv_minVlaueUnit =(TextView)parentView.findViewById(R.id.history_channel_data_type_tv2);

        mRecycler =(RecyclerView)parentView.findViewById(R.id.history_rv_states);//横向的列表
        tv_chartUnit =(TextView)parentView.findViewById(R.id.history_channel_map_tv);//表格的类型和单位
        mLineChart =(LineChart)parentView.findViewById(R.id.history_fix_map_linechart);
        btn_fixMap =(Button)parentView.findViewById(R.id.history_fix_map_btn);//固定曲线
        btn_listView =(Button)parentView.findViewById(R.id.history_list_btn);//列表模式显示
        mListview =(ListView)parentView.findViewById(R.id.monitoring_data_lv);//展示历史数据的ListView
        /*************************************时间查询*************************************************/
        btn_query =(Button)parentView.findViewById(R.id.history_search_time_btn);//查询
        relayout_endTime =(LinearLayout)parentView.findViewById(R.id.history_end_time_ll);//起始时间
        layout_startTime =(LinearLayout)parentView.findViewById(R.id.history_start_time_ll);//截止时间
        tv_startTime =(TextView)parentView.findViewById(R.id.history_start_date_tv);
        tv_endTime =(TextView)parentView.findViewById(R.id.history_end_date_tv);
        tv_oneDay =(TextView)parentView.findViewById(R.id.history_search_time_day);//一天
        tv_oneWeek =(TextView)parentView.findViewById(R.id.history_search_time_week);//一周
        tvRecoedCount= (TextView) parentView.findViewById(R.id.tv_record_count);//总条数


        //设置控件监听
        left_img.setOnClickListener(this);
        btn_fixMap.setOnClickListener(this);
        btn_listView.setOnClickListener(this);
        btn_query.setOnClickListener(this);
        relayout_endTime.setOnClickListener(this);
        layout_startTime.setOnClickListener(this);
        tv_oneDay.setOnClickListener(this);
        tv_oneWeek.setOnClickListener(this);

        initView();
    }

    @Override
    public void initData() {
        super.initData();

    }

    private void initView(){
        changeShowModel(true);
        //初始化title
        initTitle();
        initTime();
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
    private void initTitle(){
        initChannelInfo(channelId);
        //查询数据库的最大最小值
        SqliteUtil.getInstance(getActivity()).getChannelDataMaxAndMin(device.getDevice_manager_name()+device.getDeviceSerial(),channelId,this);
        tv_title.setText(device.getDeviceName());
        tv_title.setVisibility(View.VISIBLE);
        if(device.getGroupChannels().size()>0){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            final RecyclerAdapter adapter=new RecyclerAdapter(getActivity(),channels);
            adapter.setOnItemClickListener(new RecyclerAdapter.MyonItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    channelId=position;
                    //getChannelDateFromDB(position);
                    adapter.setSelect(position);
                    initChannelInfo(position);
                    adapter.notifyDataSetChanged();

                    //重新查询数据
                    channelId=position;
                    getChannelMaxDateFromDB();
                }
            });
            mRecycler.setAdapter(adapter);
            mRecycler.setLayoutManager(linearLayoutManager);
        }
    }
    /*
          * 初始化需要的参数
          * */
    private void initArguments(){
        device= (Device) getArguments().getSerializable(Constant.DEVICE_INFO_KEY);
        channels= device.getGroupChannels();
    }
    private void initChannelInfo(int channelId){
        switch (channelId){
            case 0:
                channelType=device.getCHL1_type();
                tv_chartUnit.setText(device.getCHL1_type()+"("+device.getCHL1_unit()+")");
                tv_maxVlaueUnit.setText(device.getCHL1_unit());
                tv_minVlaueUnit.setText(device.getCHL1_unit());
                if (DeviceManagerUtil.getIsFahrenheit(getActivity())){
                    tv_chartUnit.setText(device.getCHL1_type()+"("+"℉"+")");
                    tv_maxVlaueUnit.setText("℉");
                    tv_minVlaueUnit.setText("℉");
                }
                break;
            case 1:;
                channelType=device.getCHL2_type();
                tv_chartUnit.setText(device.getCHL2_type()+"("+device.getCHL2_unit()+")");
                tv_maxVlaueUnit.setText(device.getCHL2_unit());
                tv_minVlaueUnit.setText(device.getCHL2_unit());
                break;
            case 2:
                channelType=device.getCHL3_type();
                tv_chartUnit.setText(device.getCHL3_type()+"("+device.getCHL3_unit()+")");
                tv_maxVlaueUnit.setText(device.getCHL3_unit());
                tv_minVlaueUnit.setText(device.getCHL3_unit());
                break;
            case 3:
                channelType=device.getCHL4_type();
                tv_chartUnit.setText(device.getCHL4_type()+"("+device.getCHL4_unit()+")");
                tv_maxVlaueUnit.setText(device.getCHL4_unit());
                tv_minVlaueUnit.setText(device.getCHL4_unit());
                break;
        }
        getChannelMaxDateFromDB();
    }

    private void createChart(){
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
            set = createLineDataSet();
            data.addDataSet(set);
        }
        // 先添加一个x坐标轴的值
        // 因为是从0开始，data.getXValCount()每次返回的总是全部x坐标轴上总数量，所以不必多此一举的加1
        data.addXValue(" ");
        int dataNum = dataList.size();
        for (int i = 0; i < dataNum; i++) {
            if (i % (dataNum /5+ 1) == 0) {
                data.addXValue(dataList.get(i).getTime().substring(5,16));
            } else {
                data.addXValue("");
            }
            Entry entry = new Entry(dataList.get(i).getValue(), set.getEntryCount());
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

     /*
    * 从数据库查询记录
    * */

    private void getHistoryDataByTime(){
        SqliteUtil.getInstance(getActivity()).getChannelDataListByTime("serial"+device.getDeviceSerial(),channelId,tv_startTime.getText()+":00",tv_endTime.getText()+":00",this);
    }
    private void getChannelMaxDateFromDB(){
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
            case R.id.history_list_btn:
                changeShowModel(false);
                showModel=1;
                Message message=new Message();
                message.what=1;
                mHandler.sendMessage(message);
                break;
            case R.id.history_fix_map_btn:
                changeShowModel(true);
                showModel=0;
                Message message1=new Message();
                message1.what=1;
                mHandler.sendMessage(message1);
                break;
            case R.id.right_tv:

                break;
            //查询历史记录
            case R.id.history_search_time_btn:
                getHistoryDataByTime();
                break;

            //截止时间
            case R.id.history_end_time_ll:
                DatePickerDialog.startActivity(getActivity(),mHandler,false);
                chooseDate=2;
                break;
            //起始时间
            case R.id.history_start_time_ll:
                DatePickerDialog.startActivity(getActivity(),mHandler,false);
                chooseDate=1;
                break;

            //一天
            case R.id.history_search_time_day:

                break;

            //一周
            case R.id.history_search_time_week:
                break;
        }
    }

    @Override
    public void onFailure() {

    }


    @Override
    public void showProcess() {
        Message message=new Message();
        message.what=Constant.SHOW_PROGRESS;
        mHandler.sendMessage(message);
    }

    @Override
    public void onSuccess(List<HistoryData> dataList) {
        this.dataList=dataList;
        if(this.dataList!=null&&this.dataList.size()>0){
            Message message=new Message();
            message.what=1;
            mHandler.sendMessage(message);
        }else{
            UIHelper.ToastMessage("暂无数据更新");
        }
    }

    @Override
    public void stopProcess() {
        Message message=new Message();
        message.what=Constant.STOP_PROGRESS;
        mHandler.sendMessage(message);
    }

    @Override
    public void onHistorySuccess(HistoryData history) {
        Log.d("@@onhistorySuccess","refreshUI");
        this.historyData=history;
        Message message=new Message();
        message.what=Constant.REFRESH_UI;
        mHandler.sendMessage(message);

        getHistoryDataByTime();
    }
    private void initTime(){
        tv_startTime.setText(TimeUtil.getToHourTime().substring(0,16));
        tv_endTime.setText(TimeUtil.getNowDatetime().substring(0,16));
    }

    @Override
    public void noData() {
        //UIHelper.ToastMessage("暂时没有数据更新");
        Message message=new Message();
        message.what=Constant.NO_DATA;
        mHandler.sendMessage(message);
    }
}

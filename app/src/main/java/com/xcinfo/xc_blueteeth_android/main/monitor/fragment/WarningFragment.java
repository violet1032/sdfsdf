package com.xcinfo.xc_blueteeth_android.main.monitor.fragment;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BasicFragment;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.DbCallback;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.AlarmeAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.RecyclerAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import java.util.List;


/**
 * Created by ycy on 2017/03/19.
 */
public class WarningFragment extends BasicFragment implements DbCallback{

    private ImageView left_img;
    //标题
    private TextView tv_title;
    private TextView tv_warn_deal;
    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwiprefresh;
    private ListView warn_listview;
    private ImageView image_no_data;

    /*************************变量*****************************************/
    private Device device;
    private List<GroupChannel>channels;
    private Handler mHandler;
    private int channelId=0;
    private int showModel=0;                                //0是以图表显示，1以列表的方式显示


    RecyclerAdapter adapter;

    List<ChannelWarnRecord> temperatureList;
    List<ChannelWarnRecord> humidityList;

    @Override
    public int getContentView() {
        return R.layout.fragment_warning;
    }

    @Override
    public void initWidget(View parentView) {
        initArguments();
        initHaneler();
        //控件实例化
        left_img=(ImageView)parentView.findViewById(R.id.left_img);//左边的回退按钮
        left_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        tv_title= (TextView) parentView.findViewById(R.id.title_tv);//标题
        tv_warn_deal= (TextView) parentView.findViewById(R.id.monitoring_cneter_warn_deal_time_tv);//报警次数
        mRecycler =(RecyclerView)parentView.findViewById(R.id.warn_data_type_recly);//横向的列表
        mSwiprefresh =(SwipeRefreshLayout)parentView.findViewById(R.id.warn_pull_layout);//下拉刷新控件
        warn_listview =(ListView)parentView.findViewById(R.id.warn_listview);
        image_no_data= (ImageView) parentView.findViewById(R.id.image_noData);
    }


    @Override
    public void initData() {
        super.initData();

        initView();
    }
    /*
            * 初始化需要的参数
            * */
    private void initArguments(){
        device= (Device) getArguments().getSerializable(Constant.DEVICE_INFO_KEY);
        channels= device.getGroupChannels();
    }
    private void initHaneler(){
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
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
                        image_no_data.setVisibility(View.VISIBLE);
                        break;

                }
            }
        };
    }
    private void initView(){
        Log.d("@@initWarnFragment",""+channels.size());
        if (device.getToatlChannelCount()==1){
            temperatureList=SqliteUtil.getInstance(getActivity()).getWarnRecordBySerail(device.getDeviceSerial(),0);
        }else if(device.getToatlChannelCount()==2){
            temperatureList=SqliteUtil.getInstance(getActivity()).getWarnRecordBySerail(device.getDeviceSerial(),0);
            humidityList=SqliteUtil.getInstance(getActivity()).getWarnRecordBySerail(device.getDeviceSerial(),1);
        }
        //初始化title
        initTitle();

    }

    private void initTitle(){
        if(device.getDeviceName().isEmpty()){
            tv_title.setText(device.getDeviceSerial());
        }else{
            tv_title.setText(device.getDeviceName());
        }
        tv_title.setVisibility(View.VISIBLE);
        if (getWarnCount()>0){
            final AlarmeAdapter alarmeAdapter=new AlarmeAdapter(getActivity(),temperatureList);
            warn_listview.setAdapter(alarmeAdapter);
            warn_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                    DialogUtil.updateWarnRecord(getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ChannelWarnRecord record=(ChannelWarnRecord)parent.getItemAtPosition(position);
                            SqliteUtil.getInstance(getActivity()).upDateWarnRecord(record.getDeviceSerial(),record.getChannelId(),record.getRecordTime());
                            temperatureList.remove(position);
                            alarmeAdapter.notifyDataSetChanged();
                            dialog.dismiss();


                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                }
            });
            mSwiprefresh.setVisibility(View.VISIBLE);
            warn_listview.setVisibility(View.VISIBLE);
        }
        tv_warn_deal.setText(""+getWarnCount());
        if(device.getGroupChannels().size()>0){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
            adapter=new RecyclerAdapter(getActivity(),channels);
            adapter.setOnItemClickListener(new RecyclerAdapter.MyonItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    channelId=position;
                    adapter.setSelect(position);
                    adapter.notifyDataSetChanged();
                    if (position==0){
                        if (temperatureList==null){
                            warn_listview.setVisibility(View.GONE);
                        }
                        if (temperatureList!=null){
                            final AlarmeAdapter alarmeAdapter1=new AlarmeAdapter(getActivity(),temperatureList);

                            warn_listview.setAdapter(alarmeAdapter1);
                            warn_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                                    DialogUtil.updateWarnRecord(getActivity(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ChannelWarnRecord record=(ChannelWarnRecord)parent.getItemAtPosition(position);
                                            SqliteUtil.getInstance(getActivity()).upDateWarnRecord(record.getDeviceSerial(),record.getChannelId(),record.getRecordTime());
                                            temperatureList.remove(position);
                                            alarmeAdapter1.notifyDataSetChanged();
                                            dialog.dismiss();


                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });
                            warn_listview.setVisibility(View.VISIBLE);
                        }

                    }else if (position==1){

                        if (humidityList==null){
                            warn_listview.setVisibility(View.GONE);
                        }
                        if (humidityList!=null){
                            final AlarmeAdapter alarmeAdapter2=new AlarmeAdapter(getActivity(),humidityList);
                            warn_listview.setAdapter(alarmeAdapter2);
                            warn_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                                    DialogUtil.updateWarnRecord(getActivity(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ChannelWarnRecord record=(ChannelWarnRecord)parent.getItemAtPosition(position);
                                            SqliteUtil.getInstance(getActivity()).upDateWarnRecord(record.getDeviceSerial(),record.getChannelId(),record.getRecordTime());
                                            temperatureList.remove(position);
                                            alarmeAdapter2.notifyDataSetChanged();
                                            dialog.dismiss();


                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });
                            warn_listview.setVisibility(View.VISIBLE);
                        }

                    }

                }
            });
            mRecycler.setAdapter(adapter);
            mRecycler.setLayoutManager(linearLayoutManager);
        }
    }

    //获取报警总次数
    private int getWarnCount() {
//        if (device.getToatlChannelCount()==1){
//            return temperatureList.size();
//        }else if(device.getToatlChannelCount()==2){
//            return temperatureList.size()+humidityList.size();
//        }
        int count=0;
        if (temperatureList!=null)
            count=count+temperatureList.size();
        if (humidityList!=null)
            count=count+humidityList.size();
        return count;
    }

    @Override
    public void showProcess() {
        Message message=new Message();
        message.what=Constant.STOP_PROGRESS;
        mHandler.sendMessage(message);
    }

    @Override
    public void onSuccess(List<HistoryData> dataList) {

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

    }

    @Override
    public void noData() {
        Message message=new Message();
        message.what=Constant.NO_DATA;
        mHandler.sendMessage(message);
    }
}

package com.xcinfo.xc_blueteeth_android.main.monitor.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.DialogUtil;
import com.xcinfo.xc_blueteeth_android.main.fragment.HomeFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.AlarmeAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.WarnHelper;

import java.util.List;

/**
 * 报警信息
 * created by ：ycy on 2017/3/10.
 * email 1490258886@qq.com
 */

public class AlameFragment extends BaseFragment{

    private ListView mListView;
    private List<ChannelWarnRecord>channelWarnRecords;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout image_noData;

    private String currectMannger;

    AlarmeAdapter alarmeAdapter;


    private static AlameFragment alameFragment=null;
    public static AlameFragment getInstance(){
        if(alameFragment==null){
            alameFragment=new AlameFragment();
        }
        return alameFragment;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_alarme;
    }

    @Override
    public void initContentView(View viewContent) {
        mListView= (ListView) viewContent.findViewById(R.id.mListView);
        swipeRefreshLayout= (SwipeRefreshLayout) viewContent.findViewById(R.id.alarme_swiprefreshlayout);
        image_noData= (RelativeLayout) viewContent.findViewById(R.id.imgae_no_data);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue_main_ui));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMyData();
            }
        });
        initMyData();
    }
    private void initMyData(){
        Log.d("@@getcurrectmannger",HomeFragment.getCurrectMannger());
        channelWarnRecords= SqliteUtil.getInstance(getActivity()).getAllWarnRecordByDeviceManagerName(HomeFragment.getCurrectMannger());
        if(channelWarnRecords!=null&&channelWarnRecords.size()>0){
            image_noData.setVisibility(View.GONE);
            alarmeAdapter=new AlarmeAdapter(getActivity(),channelWarnRecords);

            mListView.setAdapter(alarmeAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    DialogUtil.updateWarnRecord(getContext(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ChannelWarnRecord record=channelWarnRecords.get(position);
                            SqliteUtil.getInstance(getContext()).upDateWarnRecord(record.getDeviceSerial(),record.getChannelId(),record.getRecordTime());
                            channelWarnRecords.remove(position);
                            alarmeAdapter.notifyDataSetChanged();
                            if (channelWarnRecords.size()==0){
                                SqliteUtil.getInstance(getActivity()).upDateDeviceStateBySerial(record.getDeviceSerial(),0);
                                sendBroad();
                            }
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
        }else{
            image_noData.setVisibility(View.VISIBLE);
        }
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private void sendBroad(){
        //发送跟新界面广播
        Intent intent=new Intent();
        intent.setAction(BlueToothCommuicationService.BLE_REFRESHUI);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMyData();
    }
}

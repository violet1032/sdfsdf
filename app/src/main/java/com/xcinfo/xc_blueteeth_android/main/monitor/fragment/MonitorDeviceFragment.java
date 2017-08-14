package com.xcinfo.xc_blueteeth_android.main.monitor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.GroupCallback;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.fragment.HomeFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.RefreshListener;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.EditGroupActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.MonitoringDetailsActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.MonitorDeviceRecyclerAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.PopWindowAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.activity.searchdevice.DividerItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by ：ycy on 2017/3/10.
 * email 1490258886@qq.com
 */

public class MonitorDeviceFragment extends BaseFragment implements View.OnClickListener,RefreshListener,GroupCallback {
    private static MonitorDeviceFragment monitorDeviceFragment=null;
    private TextView tv_device_group;//分组的textview
    private PopupWindow mPopupWindow;
    private ListView mLvPop;
    private Button btEditTeam;
    private TextView tvAllDetector;
    private PopWindowAdapter mPopAdapter;

    private RecyclerView recyclerView;

    private static boolean tv_allDeviceSelect=true;
    private List<Device>deviceList;
    private List<Group>groupNames;
    private List<Group>groupList=new ArrayList<>();

    String currectMannger;

    //广播接收
    BroadcastReceiver broadcastReceiver;

    IntentFilter filter;



    public static MonitorDeviceFragment getInstance(){
        if(monitorDeviceFragment==null){
            monitorDeviceFragment=new MonitorDeviceFragment();
        }
        return monitorDeviceFragment;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_monitor_device;
    }

    @Override
    public void initContentView(View viewContent) {
        tv_device_group=(TextView)viewContent.findViewById(R.id.tv_device_group);
        tv_device_group.setOnClickListener(this);
        recyclerView=(RecyclerView)viewContent.findViewById(R.id.monitor_recyclerview);
    }



    @Override
    public void initData() {
        super.initData();
        initBroadCast();
        initListView();

    }

    private void initBroadCast() {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initListView();
            }
        };

        filter=new IntentFilter();
        filter.addAction(BlueToothCommuicationService.BLE_REFRESHUI);
        //filter.addAction(BlueToothCommuicationService.BLE_DISCONNECTED);

        getActivity().registerReceiver(broadcastReceiver,filter);

    }


    private void initListView(){
        currectMannger=HomeFragment.getCurrectMannger();
        getAllDevicesList();
        if(deviceList!=null&&deviceList.size()>0){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(linearLayoutManager);
            initPopuptWindow();
            refreshRecycler(deviceList);
        }
    }
    private void refreshRecycler(final List<Device>deviceList){
        MonitorDeviceRecyclerAdapter adapter=new MonitorDeviceRecyclerAdapter(getContext(),deviceList);
        for(int i=0;i<deviceList.size();i++){
//            Log.e("tag", TimeUtil.getNowDatetime());
//            Log.e("tag",deviceList.get(i).getDeviceName());
        }
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL,R.drawable.monitor_recycler_item_decoration));
        adapter.setOnItemClickListener(new MonitorDeviceRecyclerAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getContext(), MonitoringDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constant.DEVICE_INFO_KEY,deviceList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //弹窗的事件
            case R.id.tv_device_group:
                /**
                 * 暂时关闭自定义分组功能
                 */
//                initPopuptWindow();
//                mPopupWindow.showAsDropDown(v,0,0);
//                break;
        }
    }
    private void getAllDevicesList(){
        deviceList=SqliteUtil.getInstance(getContext()).getAllDeviceListInManager(currectMannger);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            initListView();
    }


    protected void initPopuptWindow() {
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        View popupWindow_view = LayoutInflater.from(getContext()).inflate(R.layout.popwindow, null);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        mPopupWindow = new PopupWindow(popupWindow_view, 350, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true(这句话待测试)
        popupWindow_view.setFocusable(true);

        // 设置动画效果
//        mPopupWindow.setAnimationStyle(R.style.AnimationFade);
        //实例化popWindow上的控件
        groupNames= SqliteUtil.getInstance(getContext()).getGroupListByDeviceManager("蓝牙主机1");
        if(groupNames!=null)
            groupNames.clear();
        if(groupList!=null&&groupList.size()>0)
            groupList.clear();
        groupNames=SqliteUtil.getInstance(getActivity()).getGroupListByDeviceManager(currectMannger);
        for(int i=0;i<groupNames.size();i++){
            SqliteUtil.getInstance(getActivity()).getDeviceListByGroupName(groupNames.get(i).getGroupName(),this);

        }

        mLvPop = (ListView) popupWindow_view.findViewById(R.id.popwindow_lv);
        btEditTeam = (Button) popupWindow_view.findViewById(R.id.popwindow_bt);
        tvAllDetector = (TextView) popupWindow_view.findViewById(R.id.popwindow_allDetector);
        if(deviceList!=null){
            tv_device_group.setText("全部设备("+deviceList.size()+"台)");
        }else{
            tv_device_group.setText("全部设备");
        }
        if(tv_allDeviceSelect){
            tvAllDetector.setTextColor(getResources().getColor(R.color.blue_main_ui));
        }else{
            tvAllDetector.setTextColor(getResources().getColor(R.color.white));
        }
        if(groupList!=null&&groupList.size()>0){
            mPopAdapter = new PopWindowAdapter(getActivity(), groupList);
        }

        mLvPop.setAdapter(mPopAdapter);


        //全部设备的点击事件
        tvAllDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            PopWindowAdapter.selectItem=-1;
                getAllDevicesList();
                refreshRecycler(deviceList);
            mPopupWindow.dismiss();
            }
        });


        /**************************************item的点击事件************************************************************/
        mLvPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_allDeviceSelect=false;
                PopWindowAdapter.selectItem=i;
                tv_device_group.setText(groupList.get(i).getGroupName()+"("+groupList.get(i).getDeviceList().size()+"台)");
//                if(deviceList!=null)
//                    deviceList.clear();
                refreshRecycler(SqliteUtil.getInstance(getActivity()).getDeviceListByGroupName(groupList.get(i).getGroupName(),currectMannger));
                mPopupWindow.dismiss();
            }
        });
        //编辑我的分组
        btEditTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constant.EDIT_GROUP_KEY, (Serializable) groupNames);
                Intent intent=new Intent(getContext(), EditGroupActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 点击其他地方消失
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                return false;
            }
        });


    }
    //分组管理页面的回调
    @Override
    public void refresh(int fromWhere) {
        initListView();
    }

    @Override
    public void onSuccess(Group group) {
        groupList.add(group);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void stopProgress() {

    }
}

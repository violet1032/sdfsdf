package com.xcinfo.xc_blueteeth_android.main.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.AlameFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.MonitorDeviceFragment;

import java.lang.reflect.Field;


public class MoniterFragment extends BaseFragment implements View.OnClickListener {
    private Button btn_monitor_device;
    private Button btn_alarm;

    private TextView tv_title;

    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    public int getContentView() {
        return R.layout.fragment_moniter;
    }


    @Override
    public void initContentView(View viewContent) {
        initFragment();
        tv_title= (TextView) viewContent.findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("冷链监控");
        //顶部二个按钮
        btn_monitor_device= (Button) viewContent.findViewById(R.id.btn_monitor_device);
        btn_alarm= (Button) viewContent.findViewById(R.id.btn_waring_info );
        changeButton(true);
        //设置监听
        btn_monitor_device.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();

    }

    private void initFragment(){
        fm=getChildFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.main_content_fragment, MonitorDeviceFragment.getInstance());
        ft.add(R.id.main_content_fragment, AlameFragment.getInstance());
        ft.hide(AlameFragment.getInstance());//先隐藏后面Fragment
        ft.commit();
    }
    private void changeButton(boolean flag){
       if(flag){
           btn_monitor_device.setBackgroundResource(R.drawable.btn_selected_shape);
           btn_monitor_device.setTextColor(getResources().getColor(R.color.white));
           btn_alarm.setBackgroundResource(R.drawable.btn_unselected_shape);
           btn_alarm.setTextColor(getResources().getColor(R.color.main_black));
       }else{
           btn_monitor_device.setBackgroundResource(R.drawable.btn_unselected_shape);
           btn_monitor_device.setTextColor(getResources().getColor(R.color.main_balck));
           btn_alarm.setBackgroundResource(R.drawable.btn_selected_shape);
           btn_alarm.setTextColor(getResources().getColor(R.color.white));
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //监测设备
            case R.id.btn_monitor_device:
                changeButton(true);
                FragmentManager moFm=getChildFragmentManager();
                FragmentTransaction moFt=moFm.beginTransaction();
                moFt.hide(AlameFragment.getInstance());
                moFt.show(MonitorDeviceFragment.getInstance());
                moFt.commit();
                break;
            //报警信息
            case R.id.btn_waring_info:
                changeButton(false);
                FragmentManager arFm=getChildFragmentManager();
                FragmentTransaction arFt=arFm.beginTransaction();
                arFt.hide(MonitorDeviceFragment.getInstance());
                arFt.show(AlameFragment.getInstance());
                arFt.commit();
                break;
        }
    }


}

package com.xcinfo.xc_blueteeth_android.main.monitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.ChooseDeviceAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.NewGroupDialog;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import org.kymjs.kjframe.ui.BindView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChooseDeviceActivity extends BaseActivity {
    @BindView(id = R.id.left_img,click = true)
    private ImageView left_img;
    @BindView(id=R.id.tv_title,click = false)
    private TextView tv_title;
    @BindView(id=R.id.tv_right,click = true)
    private TextView tv_right;
    @BindView(id=R.id.choose_device_list)
    private ListView mListView;
    @BindView(id=R.id.tv_selected_count)
    private TextView tv_selected_count;
    private static int num=0;

    private List<Device>deviceList;
    private List<Device>devices=new ArrayList<>();
    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_choose_device);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        left_img.setVisibility(View.VISIBLE);
        tv_right.setText("下一步");
        tv_title.setText("选择设备");
        tv_title.setVisibility(View.VISIBLE);
        initList();
    }
    private boolean getDeviceList(){
        //deviceList=SqliteUtil.getInstance(ChooseDeviceActivity.this).getDeviceListByGroupName("",getDeviceManagerName());
        if(deviceList!=null&&deviceList.size()>0)
            return true;
     return false;
    }

    private void initList(){
        if(getDeviceList()){
            final ChooseDeviceAdapter adapter=new ChooseDeviceAdapter(ChooseDeviceActivity.this,deviceList);
            mListView.setAdapter(adapter);
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("tag",""+num);
                    Device device=new Device();
                    device.setDeviceSerial(deviceList.get(position).getDeviceSerial());
                    if (adapter.getIsSelected().get(position)) {
                        adapter.getIsSelected().put(position, false);
                        adapter.setIsSelected(adapter.getIsSelected());
                        num--;
                        devices.remove(device);
                } else {
                        adapter.getIsSelected().put(position, true);
                        adapter.setIsSelected(adapter.getIsSelected());
                        num++;
                        devices.add(device);
                }
                    tv_selected_count.setText("已选中"+num+"台设备");
                    if(num>0){
                        tv_right.setVisibility(View.VISIBLE);
                    }else{
                        tv_right.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    /*
  *
  * 获得蓝牙主机名
  * */
    private String getDeviceManagerName(){
        return (String) SPUtils.get(ChooseDeviceActivity.this, Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1");
    }
    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()){
            case R.id.left_img:
                ChooseDeviceActivity.this.finish();
                break;
            case R.id.tv_right:
                Intent intent=new Intent(ChooseDeviceActivity.this, NewGroupDialog.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(Constant.DEVICE_INFO_KEY, (Serializable) devices);
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Constant.EDITGROUP_NAME_RESULT_CODE){
            setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
            ChooseDeviceActivity.this.finish();
        }
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


}

package com.xcinfo.xc_blueteeth_android.main.monitor.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.GroupCallback;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.ExpandableAdapter;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.DialogEditTeam;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.DialogUtils;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.MonitorDeviceFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import org.kymjs.kjframe.ui.BindView;

import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends BaseActivity implements GroupCallback{
    @BindView(id = R.id.left_img,click = true)
    private ImageView left_img;
    @BindView(id=R.id.tv_title,click = false)
    private TextView tv_title;
    @BindView(id=R.id.tv_right,click = true)
    private TextView tv_right;
    @BindView(id=R.id.tv_add_group,click = true)
    private TextView tv_add_group;

    private ExpandableListView mExpandableListView;

/********************************变量*************************************************/
    private Handler mHandler;
    //分组列表
    private List<Group>groupsName;
    //分组下面的设备列表
    private List<Device>deviceList;
    private List<Group>groupList=new ArrayList<>();

    private String []name;
    private  String nowGroupName="";
    //默认进来这个页面是可以删除等操作  点击编辑以后可以修改分组的名称等等
    private boolean isEdit=false;

//    String []name=new String[]{groupsName.get(grop)};
    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_edit_group);
    }

    /*
 *
 * 获得蓝牙主机名
 * */
//    private String getDeviceManagerName(){
//        return (String) SPUtils.get(EditGroupActivity.this,Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1");
//    }
    private void initArguments(){
        if(groupsName!=null)
            groupsName.clear();
            groupsName= (List<Group>) getIntent().getSerializableExtra(Constant.EDIT_GROUP_KEY);

        for(int i=0;i<groupsName.size();i++){
            SqliteUtil.getInstance(EditGroupActivity.this).getDeviceListByGroupName(groupsName.get(i).getGroupName(),this);
        }
        initList();

    }
    private void refreshListView(){
        if(groupsName!=null)
            groupsName.clear();
        if(groupList!=null&&groupList.size()>0)
            groupList.clear();
        groupsName=SqliteUtil.getInstance(EditGroupActivity.this).getGroupListByDeviceManager(DeviceManagerUtil.getDeviceManagerName(EditGroupActivity.this));
        for(int i=0;i<groupsName.size();i++){
            SqliteUtil.getInstance(EditGroupActivity.this).getDeviceListByGroupName(groupsName.get(i).getGroupName(),this);
            //Log.e("tag",""+groupsName.get(i).getGroupName());
        }
        initList();
    }
    private void initList(){
        if(groupList.size()>0){
            //不可修改名字的时候,不显示前面的小红点
            if(isEdit){
                ExpandableAdapter adapter=new ExpandableAdapter(EditGroupActivity.this,groupList,true);
                mExpandableListView.setAdapter(adapter);
                mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                        ImageView image_delete=(ImageView)v.findViewById(R.id.image_delete);
                        //ImageView image_arrow=(ImageView)v.findViewById(R.id.image_expand);
                        //image_arrow.setVisibility(View.INVISIBLE);
                        //删除分组
                        image_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtils.createDoubleBtnDialog(EditGroupActivity.this,"删除分组", "删除分组后将没有办法恢复，是否删除分组！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       switch (which){
                                           //取消
                                           case -2:
                                               dialog.dismiss();
//                                               Log.e("tag","cancel");
                                               break;
                                           //确定
                                           case -1:
                                               dialog.dismiss();
//                                               Log.e("tag","ok");
                                               switch (SqliteUtil.getInstance(EditGroupActivity.this).deleteGroup(groupList.get(groupPosition).getGroupName(),
                                                       (String) SPUtils.get(EditGroupActivity.this,Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1"))){
                                                   case 0:
                                                       if(SqliteUtil.getInstance(EditGroupActivity.this).upDateDeviceGroup("",groupList.get(groupPosition).getGroupName()))
                                                           UIHelper.ToastMessage("删除分组成功");
                                                       MonitorDeviceFragment.getInstance().refresh(001);
                                                       refreshListView();
                                                       break;
                                                   //失败
                                                   case -1:
                                                       break;
                                               }
                                               break;
                                       }
                                    }
                                });
                            }
                        });

                        Intent intent=new Intent(EditGroupActivity.this, DialogEditTeam.class);
                        intent.putExtra("groupName",groupList.get(groupPosition).getGroupName());
                        startActivityForResult(intent,1);
                        return true;
                    }
                });
                adapter.notifyDataSetChanged();
            }else{
                ExpandableAdapter adapter=new ExpandableAdapter(EditGroupActivity.this,groupList,false);
                mExpandableListView.setAdapter(adapter);
                mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        ImageView arrow=(ImageView)v.findViewById(R.id.image_expand);
                       // ImageView image_arrow=(ImageView)v.findViewById(R.id.image_expand);
                        //image_arrow.setVisibility(View.VISIBLE);
                        if(parent.isGroupExpanded(groupPosition)){
                            Animation animation=AnimationUtils.loadAnimation(EditGroupActivity.this,R.anim.open_anim);
                            animation.setFillAfter(true);
                            arrow.setAnimation(animation);
                        }else{
                            Animation a=AnimationUtils.loadAnimation(EditGroupActivity.this,R.anim.close_anim);
                            a.setFillAfter(true);
                            arrow.setAnimation(a);
                        }
                        return false;
                    }
                });
                mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                        //移动分组
                        name=new String[groupsName.size()];
                        for(int i=0;i<groupsName.size();i++){
                            name[i]=groupsName.get(i).getGroupName();
                        }
                        AlertDialog.Builder builder=new AlertDialog.Builder(EditGroupActivity.this);
                        builder.setTitle("移动分组至");
                        builder.setSingleChoiceItems(name, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                nowGroupName=name[which];
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (nowGroupName != null && !nowGroupName.isEmpty()) {
                                    if(SqliteUtil.getInstance(EditGroupActivity.this).upDateDeviceGroupBySerial(nowGroupName,groupList.get(groupPosition).getDeviceList().get(childPosition).getDeviceSerial()
                                            ,groupsName.get(groupPosition).getGroupName())){
                                                    UIHelper.ToastMessage("移动分组成功");
                                                    setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
                                                    refreshListView();
                                                }else{
                                                    UIHelper.ToastMessage("移动分组失败");
                                                }
                                            }
                                            dialog.dismiss();
                            }
                        })
                                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
//                            DialogUtils.createListDialog(EditGroupActivity.this, "移动分组至", name, -1, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if(which>=0){
//                                        for(int i=0;i<name.length;i++){
//                                            nowGroupName=name[i];
//                                        }
//                                    }
//                                    switch (which){
//                                        //取消按钮
//                                        case -2:
//                                            dialog.dismiss();
//                                            break;
//                                        //确定按钮
//                                        case -1:
//                                            if(nowGroupName!=null&&!nowGroupName.isEmpty()){
////                                                Log.e("tag",groupList.get(groupPosition).getDeviceList().get(childPosition).getDeviceSerial());
////                                                Log.e("tag+now",nowGroupName);
//                                                if(SqliteUtil.getInstance(EditGroupActivity.this).upDateDeviceGroupBySerial(nowGroupName,groupList.get(groupPosition).getDeviceList().get(childPosition).getDeviceSerial()
//                                                ,groupsName.get(groupPosition).getGroupName()
//                                                )){
////                                                    SqliteUtil.getInstance(EditGroupActivity.this).upDateGroupName()
//                                                    UIHelper.ToastMessage("移动分组成功");
//                                                    setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
//                                                    refreshListView();
//                                                }else{
//                                                    UIHelper.ToastMessage("移动分组失败");
//                                                }
//                                            }
//                                            dialog.dismiss();
//                                            break;
//                                    }
//                                }
//                            });

                        return true;
                    }
                });
                adapter.notifyDataSetChanged();
            }

        }else{
            // 没有数据
        }
    }
    @Override
    public void initWidget() {
        super.initWidget();
        mExpandableListView= (ExpandableListView) findViewById(R.id.mExpandableListView);
        initArguments();
        left_img.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("编辑");
        tv_title.setText("分组管理");
        tv_title.setVisibility(View.VISIBLE);
    }


    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()){
            case R.id.tv_right:
                if(tv_right.getText().equals("查看")){
                    tv_right.setText("编辑");
                    isEdit=false;
                    Intent intent=new Intent();
                    intent.setAction("com.xcinfo.xc_blueteeth_android.main.monitor.activity");
                    intent.putExtra("about_arrow","show_arrow");
                    sendBroadcast(intent);
                }else{
                    tv_right.setText("查看");
                    isEdit=true;
                    Intent intent=new Intent();
                    intent.setAction("com.xcinfo.xc_blueteeth_android.main.monitor.activity");
                    intent.putExtra("about_arrow","hide_arrow");
                    sendBroadcast(intent);
                }
                initList();
                break;
            case R.id.left_img:
                EditGroupActivity.this.finish();
                break;
            //添加分组
            case R.id.tv_add_group:
                if(SqliteUtil.getInstance(EditGroupActivity.this).getDeviceListByGroupName("", DeviceManagerUtil.getDeviceManagerName(EditGroupActivity.this)).size()>0)
                startActivityForResult(new Intent(EditGroupActivity.this,ChooseDeviceActivity.class),0);
                else
                UIHelper.ToastMessage("没有未分组设备可选");
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==Constant.EDITGROUP_NAME_RESULT_CODE){
            refreshListView();
        }
        if(requestCode==0&&resultCode==Constant.EDITGROUP_NAME_RESULT_CODE){
            refreshListView();
        }
    }
}

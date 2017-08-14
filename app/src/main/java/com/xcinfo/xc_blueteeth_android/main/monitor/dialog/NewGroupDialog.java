package com.xcinfo.xc_blueteeth_android.main.monitor.dialog;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import java.util.List;

public class NewGroupDialog extends Activity implements View.OnClickListener{
    private Button btnConfirm;//确认

    private Button btnCancel;//取消

    private EditText edTeamName;//改变分组名称的输入框

    private ImageButton imgClear;//清空编辑框

    private String mStrGroupName;//从列表页面传来的群组名称
    private int mGroupId;//分组的Id
    private String deviceManagerName;
    private List<Device>deviceList;


    public static final int RESULT_CHANGE_GOURP_NAME = 001;
    private TextView tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editteam);
        initData();
        initWidget();
    }

    public void initData() {
        deviceList= (List<Device>) getIntent().getSerializableExtra(Constant.DEVICE_INFO_KEY);
    }

    public void initWidget() {
        btnConfirm= (Button) findViewById(R.id.dialog_editteam_confirm);
        btnCancel= (Button) findViewById(R.id.dialog_editteam_cancel);
        imgClear= (ImageButton) findViewById(R.id.dialog_editteam_clear);
        edTeamName= (EditText) findViewById(R.id.dialog_editteam_edTeamName);
        tv_title =(TextView)findViewById(R.id.dialog_editteam_title);
        tv_title.setText("添加分组");
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        imgClear.setOnClickListener(this);
    }

    /*
    *
    * 获得蓝牙主机名
    * */
    private String getDeviceManagerName(){
        return (String) SPUtils.get(NewGroupDialog.this, Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1");
    }

    private void createGroup(String groupName, Group group){
        if(SqliteUtil.getInstance(NewGroupDialog.this).hasGroupName(groupName,getDeviceManagerName())){
            UIHelper.ToastMessage("分组已经存在");
            NewGroupDialog.this.finish();
        }else{
            //创建成功
            if(SqliteUtil.getInstance(NewGroupDialog.this).saveGroup(group,getDeviceManagerName())){
                for(int i=0;i<deviceList.size();i++){
                    SqliteUtil.getInstance(NewGroupDialog.this).upDateDeviceGroup(groupName,"");
                }
                UIHelper.ToastMessage("创建分组成功");
                setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
                NewGroupDialog.this.finish();
            }else{
                UIHelper.ToastMessage("创建分组失败");
            }

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_editteam_confirm:
                Group group=new Group();
                if(edTeamName.getText().length()!=0){
                    group.setGroupName(edTeamName.getText().toString());

                }else{
                    group.setGroupName("未知分组");
                }
                group.setDeviceCount(deviceList.size());
                createGroup(group.getGroupName(),group);
                break;
            case R.id.dialog_editteam_cancel:
                NewGroupDialog.this.finish();
                break;
            case R.id.dialog_editteam_clear:
                edTeamName.setText("");
                break;

        }
    }



}

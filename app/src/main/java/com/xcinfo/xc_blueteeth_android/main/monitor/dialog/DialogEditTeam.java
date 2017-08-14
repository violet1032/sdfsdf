package com.xcinfo.xc_blueteeth_android.main.monitor.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import org.kymjs.kjframe.ui.BindView;

public class DialogEditTeam extends Activity implements View.OnClickListener{

    private Button btnConfirm;//确认

    private Button btnCancel;//取消

    private EditText edTeamName;//改变分组名称的输入框

    private ImageButton imgClear;//清空编辑框

    private String mStrGroupName;//从列表页面传来的群组名称
    private int mGroupId;//分组的Id
    private String deviceManagerName;

    public static final int RESULT_CHANGE_GOURP_NAME = 001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editteam);
        initData();
        initWidget();
    }


    public void initData() {

        parceData();
    }

    public void initWidget() {
        btnConfirm= (Button) findViewById(R.id.dialog_editteam_confirm);
        btnCancel= (Button) findViewById(R.id.dialog_editteam_cancel);
        imgClear= (ImageButton) findViewById(R.id.dialog_editteam_clear);
        edTeamName= (EditText) findViewById(R.id.dialog_editteam_edTeamName);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        imgClear.setOnClickListener(this);
        edTeamName.setText(mStrGroupName);
        edTeamName.setSelection(mStrGroupName.length());


    }

    /*
    *
    * 获得蓝牙主机名
    * */
    private String getDeviceManagerName(){
        return (String) SPUtils.get(DialogEditTeam.this,Constant.DEVICE_MANAGER_NAME_KEY,"蓝牙主机1");
    }
    /**
     * 请求修改分组名称
     *
     * @param groupNewName
     */
    private boolean changeGroupName(final String groupNewName) {
        switch (SqliteUtil.getInstance(DialogEditTeam.this).upDateGroupName(groupNewName,mStrGroupName,getDeviceManagerName())){
            case 0:
                if(SqliteUtil.getInstance(DialogEditTeam.this).upDateDeviceGroup(groupNewName,mStrGroupName))
                    return true;
                break;
            case Constant.GROUP_NAME_IS_EXIST:
                UIHelper.ToastMessage("该分组名已经存在");
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_editteam_confirm:
                if(changeGroupName(edTeamName.getText().toString())) {
                    UIHelper.ToastMessage("修改分组名称成功");
                    setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
                    DialogEditTeam.this.finish();
                }else{
                    DialogEditTeam.this.finish();
                }
                break;
            case R.id.dialog_editteam_cancel:
                DialogEditTeam.this.finish();
                break;
            case R.id.dialog_editteam_clear:
                edTeamName.setText("");
                break;

        }
    }
    private void parceData() {
        mStrGroupName = getIntent().getStringExtra("groupName");

    }


}

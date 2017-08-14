package com.xcinfo.xc_blueteeth_android.main.monitor.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTelDialog extends Activity implements View.OnClickListener{

    private TextView tv_title;
    private TextView tv_hint;
    private EditText edit;
    private Button btn_cancel;
    private Button btn_confirm;

    private int flag=0;
    private String deviceSerial;
    private String deviceName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tel_dialog);
        initWidght();
    }
    private void initWidght(){
        parseData();
        tv_title =(TextView)findViewById(R.id.dialog_editteam_title);
        tv_hint =(TextView)findViewById(R.id.dialog_editteam_subtitle);
        btn_confirm =(Button)findViewById(R.id.dialog_editteam_confirm);
        btn_cancel =(Button)findViewById(R.id.dialog_editteam_cancel);
        edit =(EditText)findViewById(R.id.dialog_editteam_edTeamName);

        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        if(flag==Constant.CHANGE_DEVICE_NAME){
            tv_title.setText("修改设备名称");
            tv_hint.setText("请输入新的设备名称");
            edit.setText(deviceName);
            edit.setSelection(deviceName.length());
        }
        if(flag==Constant.CHANGE_TEL){
            tv_title.setText("设置报警联系人");
            tv_hint.setText("请输入新的手机号码");
            if(DeviceManagerUtil.getTel(EditTelDialog.this)!=null&&!DeviceManagerUtil.getTel(EditTelDialog.this).isEmpty()){
                edit.setText(DeviceManagerUtil.getTel(EditTelDialog.this));
                edit.setSelection(DeviceManagerUtil.getTel(EditTelDialog.this).length());
            }
        }
    }

    private void parseData(){
        flag=getIntent().getIntExtra(Constant.FLAG,0);
        if(flag==Constant.CHANGE_DEVICE_NAME){
            deviceSerial=getIntent().getStringExtra(Constant.DEVICE_SERIAL);
            deviceName=getIntent().getStringExtra(Constant.DEVICE_NAME);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_editteam_confirm:
                //修改设备的名字
                if(flag== Constant.CHANGE_DEVICE_NAME){
                    if(SqliteUtil.getInstance(EditTelDialog.this).upDateDeviceNameBySerial(deviceSerial,edit.getText().toString())){
                        setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
                        EditTelDialog.this.finish();
                    }
                    else
                        UIHelper.ToastMessage("修改失败");
                }
                //修改电话号码
                if(flag==Constant.CHANGE_TEL){
                    if(isPhoneNumber(edit.getText().toString())){
                        DeviceManagerUtil.putTel(EditTelDialog.this,edit.getText().toString());
                        setResult(Constant.EDITGROUP_NAME_RESULT_CODE);
                        EditTelDialog.this.finish();
                    }else{
                        UIHelper.ToastMessage("手机号码不符合规范");
                    }
                }
                break;
            case R.id.dialog_editteam_cancel:
                EditTelDialog.this.finish();
                break;
        }
    }
    private boolean isPhoneNumber(String tel){
        Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
        Matcher m = p.matcher(tel);
        return m.matches();
    }
}

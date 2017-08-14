package com.xcinfo.xc_blueteeth_android.main.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseFragment;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.service.BlueToothCommuicationService;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.activity.MainActivity;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.DialogUtils;
import com.xcinfo.xc_blueteeth_android.main.monitor.dialog.EditTelDialog;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {


    private TextView tv_title;
   //振动
    private ToggleButton btn_toggle_1;
    //响铃
    private ToggleButton btn_toggle_2;
    //发送短信
    private ToggleButton btn_toggle_3;
    //上传
    private ToggleButton btn_toggle_4;
    //上传
    private ToggleButton btn_toggle_5;

    private TextView edit_tel;
    //检查更新
    private RelativeLayout check_version;
    private TextView tv_checkVision;
    private ImageView iv_uploadData;
    @Override
    public int getContentView() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initContentView(View viewContent) {
        tv_title= (TextView) viewContent.findViewById(R.id.tv_title);
        tv_title.setText("设置");
        tv_title.setVisibility(View.VISIBLE);
        btn_toggle_1= (ToggleButton) viewContent.findViewById(R.id.btn_toggle_1);
        btn_toggle_2= (ToggleButton) viewContent.findViewById(R.id.btn_toggle_2);
        btn_toggle_3= (ToggleButton) viewContent.findViewById(R.id.btn_toggle_3);
        btn_toggle_4= (ToggleButton) viewContent.findViewById(R.id.btn_toggle_4);
        btn_toggle_5= (ToggleButton) viewContent.findViewById(R.id.btn_toggle_5);
        edit_tel= (TextView) viewContent.findViewById(R.id.ed_tel);
        if(DeviceManagerUtil.getTel(getContext())!=null&&!DeviceManagerUtil.getTel(getContext()).isEmpty()){
            edit_tel.setText(DeviceManagerUtil.getTel(getContext()));
        }
        edit_tel.setOnClickListener(this);
        initToggleButton();

        check_version= (RelativeLayout) viewContent.findViewById(R.id.check_version);
        tv_checkVision= (TextView) viewContent.findViewById(R.id.tv_version_name);
        tv_checkVision.setText(String.valueOf(DeviceManagerUtil.getVision(getContext())));

        final Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.roate_anim);
        AccelerateInterpolator accelerateInterpolator=new AccelerateInterpolator();
        animation.setInterpolator(accelerateInterpolator);
        animation.setFillAfter(false);
        iv_uploadData= (ImageView) viewContent.findViewById(R.id.iv_uploaddata);
        iv_uploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.getMyBinder()!=null){
                    List<Device> devices=SqliteUtil.getInstance(getActivity()).getAllDeviceListInManager(HomeFragment.getCurrectMannger());
                    for (final Device device:devices){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SqliteUtil.getInstance(getActivity()).upLoadingData(device,getActivity());
                            }
                        }).start();

                    }
                }else {
                    Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                }
                v.startAnimation(animation);

            }
        });
    }

    private void initToggleButton(){

        if(DeviceManagerUtil.isVirbrator(getContext())){
            btn_toggle_1.toggleOn();
        }else{
            btn_toggle_1.toggleOff();
        }

        if(DeviceManagerUtil.getIsFahrenheit(getContext())){
            btn_toggle_5.toggleOn();
        }else{
            btn_toggle_5.toggleOff();
        }

        if(DeviceManagerUtil.isRing(getContext())){
            btn_toggle_2.toggleOn();
        }else{
            btn_toggle_2.toggleOff();
        }

        if(DeviceManagerUtil.isSendSMS(getContext())){
            btn_toggle_3.toggleOn();
        }else{
            btn_toggle_3.toggleOff();
        }

        if(DeviceManagerUtil.isAutouploading(getContext())){
            btn_toggle_4.toggleOn();
        }else{
            btn_toggle_4.toggleOff();
        }
        //振动
        btn_toggle_1.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                DeviceManagerUtil.putVibrator(getContext(),on);
            }
        });
        //响铃
        btn_toggle_2.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                DeviceManagerUtil.putRing(getContext(),on);
            }
        });
        //发送短信
        btn_toggle_3.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if(on){
                    DeviceManagerUtil.putSMS(getContext(),on);
                    //检测是否是手机号码
//                    if(!isPhoneNumber(edit_tel.getText().toString()))
//                       UIHelper.ToastMessage("手机号码不正确将不能及时通知");
//                    else{
//                        DeviceManagerUtil.putTel(getContext(),edit_tel.getText().toString());
//                    }
                }
            }
        });

        btn_toggle_4.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                DeviceManagerUtil.putAutouploading(getContext(),on);
            }
        });

        btn_toggle_5.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                DeviceManagerUtil.putIsFahrenheit(getContext(),on);
                SqliteUtil.getInstance(getActivity()).setIsFahrenheit(on);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1234&&resultCode==Constant.EDITGROUP_NAME_RESULT_CODE){
            edit_tel.setText(DeviceManagerUtil.getTel(getContext()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //检查更新
            case R.id.check_version:
                MainActivity.getMyBinder().getVision();
                break;
            case R.id.ed_tel:
                Intent intent=new Intent(getContext(),EditTelDialog.class);
                intent.putExtra(Constant.FLAG,Constant.CHANGE_TEL);
                startActivityForResult(intent,1234);
                break;

        }
    }

}

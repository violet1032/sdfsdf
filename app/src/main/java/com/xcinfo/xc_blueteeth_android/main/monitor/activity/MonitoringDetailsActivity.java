package com.xcinfo.xc_blueteeth_android.main.monitor.activity;




import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.activity.BaseActivity;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.RefreshListener;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.CurrentFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.HistoryFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.WarningFragment;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.Constant;

import org.kymjs.kjframe.ui.BindView;

import java.util.List;

/**
 * Created by yhq on 2016/6/2.
 */
public class MonitoringDetailsActivity extends BaseActivity implements RefreshListener {
    private String TAG="MonitoringDetailsAct";
    //底部三个按钮的各自的根布局
    @BindView(id = R.id.monitoring_lay_realtime, click = true)
    private LinearLayout layCurrent; //实时
    @BindView(id = R.id.monitoring_lay_history, click = true)
    private LinearLayout layHistory;//历史
    @BindView(id = R.id.monitoring_lay_warning, click = true)
    private LinearLayout layAlert;//报警
    //底部三个按钮的图标部分
    @BindView(id = R.id.details_buttom_iv_1)
    private ImageView ivbt1;
    @BindView(id = R.id.details_buttom_iv_2)
    private ImageView ivbt2;
    @BindView(id = R.id.details_buttom_iv_3)
    private ImageView ivbt3;
    //底部三个按钮的文字部分
    @BindView(id = R.id.details_buttom_tv_realtime)
    private TextView tvbtRealTime;
    @BindView(id = R.id.details_buttom_tv_history)
    private TextView tvbtHistory;
    @BindView(id = R.id.details_buttom_tv_warning)
    private TextView tvbtWarning;






    private CurrentFragment currentFragment;//实时
    private HistoryFragment historyFragment;//历史
    private WarningFragment warningFragment;//报警

    private int lastSelectBottom =1; //之前底部导航选中项
    private int currentSelectBottom = 1; //现在底部导航选中项

    private Device deviceInfo;
    private List<GroupChannel>channels;




//    public boolean isChangeInfo = false;                   //设备信息是否改变
//    private boolean mIsHasHistory;//是否有权限查看历史和报警信息
//    private boolean mHasSwitch;//是否有开关控制功能（包括了是否过期和是否维保的判断）
//    public static MonitoringDetailsActivity sActivity;


    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.activity_monitoring_details);
        parseIntent();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeButtonColor(lastSelectBottom,currentSelectBottom);

    }

    @Override
    public void initData() {
        super.initData();
        setDefaultFragment();

    }
    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()){
            case R.id.monitoring_lay_realtime:
                currentSelectBottom=1;
                changeButtonColor(lastSelectBottom,currentSelectBottom);
                lastSelectBottom=1;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.DEVICE_INFO_KEY, deviceInfo);
                if(currentFragment==null)
                    currentFragment = new CurrentFragment();
                parseFragment(currentFragment);
                break;
            case R.id.monitoring_lay_history:
                currentSelectBottom=2;
                changeButtonColor(lastSelectBottom,currentSelectBottom);
                lastSelectBottom=2;
                if(historyFragment==null)
                    historyFragment=new HistoryFragment();
                parseFragment(historyFragment);
                break;
            case R.id.monitoring_lay_warning:
                currentSelectBottom=3;
                changeButtonColor(lastSelectBottom,currentSelectBottom);
                lastSelectBottom=3;
               if(warningFragment==null)
                   warningFragment=new WarningFragment();
                parseFragment(warningFragment);
                break;
        }
    }
    private void setDefaultFragment() {
        currentFragment = new CurrentFragment();
        parseFragment(currentFragment);
    }

    /**
     * 解析intent
     */
    private void parseIntent() {
        deviceInfo= (Device) getIntent().getSerializableExtra(Constant.DEVICE_INFO_KEY);
        Log.e(TAG,deviceInfo.getDeviceName());
        channels = deviceInfo.getGroupChannels();
    }
    private void parseFragment(Fragment fragment){
        Bundle bundle=new Bundle();
        bundle.putSerializable(Constant.DEVICE_INFO_KEY,deviceInfo);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content,fragment).commit();
    }
//
//

//
//    @Override
//    public void initWidget() {
//        super.initWidget();
//        layCurrent.setClickable(false);
//
//        NoAuthorityOrTimeOutToButton();  //设备过期则设置不能点击
//        checkHasGPS();//根据hasGPS是否支持gps功能
//        checkHasSwitch();//根据isHasSwitch检查是否支持开关控制功能
//    }
//
//    /**
//     * 检查是否有Gps设备
//     */
//    private void checkHasGPS() {
//        boolean hasGps = detectorInfo.getHasGps();
//        if (hasGps) {
//            isHasGps = true;
//            if (detectorInfo.getRealLatitude() == -1 && detectorInfo.getRealLongitude() == -1){
//                ivbt4.setImageResource(R.drawable.monitoring_buttom_timeout_4);
//                tvbtGps.setTextColor(getResources().getColor(R.color.gray_text));
//            }
//        } else {
//            ivbt4.setImageResource(R.drawable.monitoring_buttom_timeout_4);
//            tvbtGps.setTextColor(getResources().getColor(R.color.gray_text));
//            isHasGps = false;
//        }
//    }
//
//    /**
//     * 检查是否有开关控制功能、维保情况下也不能使用该功能、过期也不能使用
//     */
//    private void checkHasSwitch() {
//        if (detectorInfo.isHasSwitch()) {
//            mHasSwitch = true;
//            //判断是否过期
//            if (Long.parseLong(TimeUtil.dateStr2TimeStamp(detectorInfo.getExpiry(), "yyyy-MM-dd HH:mm:ss")) < TimeUtil.getTsTimes()[0]) {
//                mHasSwitch = false;
//                ivBottomSwitch.setImageResource(R.drawable.monitoring_detail_bottom_switch_cantpress);
//                tvBottomSwitch.setTextColor(getResources().getColor(R.color.gray_text));
//            }
//            //判断是否维保
//            if (detectorInfo.getStatus().name.equals("维保")) {
//                mHasSwitch = false;
//                ivBottomSwitch.setImageResource(R.drawable.monitoring_detail_bottom_switch_cantpress);
//                tvBottomSwitch.setTextColor(getResources().getColor(R.color.gray_text));
//            }
//
//        } else {
//            mHasSwitch = false;
//            ivBottomSwitch.setImageResource(R.drawable.monitoring_detail_bottom_switch_cantpress);
//            tvBottomSwitch.setTextColor(getResources().getColor(R.color.gray_text));
//        }
//
//
//    }
//

//
//    @Override
//    public void widgetClick(View v) {
//        super.widgetClick(v);
//        FragmentManager fm = getFragmentManager();
//        // 开启一个fragment事务
//        FragmentTransaction transaction = fm.beginTransaction();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("detectorInfo", detectorInfo);
//        bundle.putBoolean("isChangeInfo", isChangeInfo);
//        switch (v.getId()) {
//            //实时
//            case R.id.monitoring_lay_realtime:
//                currSelectBottom = 1;
//                ChangeButton(lastSelectBottom, currSelectBottom);
//                currentFragment = new CurrentFragment();
//                bundle.putString("homeOrDetector", getIntent().getStringExtra("homeOrDetector"));
//                currentFragment.setArguments(bundle);
//                transaction.replace(R.id.monitoring_fragment, currentFragment);
//                lastSelectBottom = 1;
//                break;
//            //历史
//            case R.id.monitoring_lay_history:
//                if (!mIsHasHistory) {
//                    UIHelper.ToastMessage("您没有权限查看历史数据");
//                    break;
//                }
//
//                if (isTimeOut) {
//                    UIHelper.ToastMessage("您的设备已过期，请及时充值");
//                } else {
//                    currSelectBottom = 2;
//                    ChangeButton(lastSelectBottom, currSelectBottom);
//                    historyFragment = new HistoryFragment();
//                    historyFragment.setArguments(bundle);
//                    transaction.replace(R.id.monitoring_fragment, historyFragment);
//                    lastSelectBottom = 2;
//                }
//                break;
//            //报警
//            case R.id.monitoring_lay_warning:
//
//                if (!mIsHasHistory) {
//                    UIHelper.ToastMessage("您没有权限查看报警信息");
//                    break;
//                }
//                if (isTimeOut) {
//                    UIHelper.ToastMessage("您的设备已过期，请及时充值");
//                } else {
//                    currSelectBottom = 3;
//                    ChangeButton(lastSelectBottom, currSelectBottom);
//                    warningFragment = new WarningFragment();
//                    warningFragment.setArguments(bundle);
//                    transaction.replace(R.id.monitoring_fragment, warningFragment);
//                    lastSelectBottom = 3;
//                }
//                break;
//            //Gps
//            case R.id.monitoring_lay_gps:
//                if (!mIsHasHistory) {
//                    UIHelper.ToastMessage("您没有权限查看Gps信息");
//                    break;
//                }
//                if (!isHasGps) {
//                    UIHelper.ToastMessage("您的设备不支持GPS功能");
//                } else if (isTimeOut) {
//                    UIHelper.ToastMessage("您的设备已过期，请及时充值");
//                } else {
//                    //有Gps装置但是无Gps数据信息
//                    Log.e("latandLong+", detectorInfo.getRealLatitude() + ";;;" + detectorInfo.getRealLongitude());
//                    if (detectorInfo.getRealLatitude() == -1 && detectorInfo.getRealLongitude() == -1) {
//                        UIHelper.ToastMessage("您的设备暂无位置信息");
//                        break;
//                    }
//                    currSelectBottom = 4;
//                    ChangeButton(lastSelectBottom, currSelectBottom);
//                    gpsFragment = new GPSFragment();
//                    gpsFragment.setArguments(bundle);
//                    transaction.replace(R.id.monitoring_fragment, gpsFragment);
//                    lastSelectBottom = 4;
//                }
//                break;
//            //开关
//            case R.id.monitoring_switch_ll:
//                if (!mHasSwitch) {
//                    UIHelper.ToastMessage("开关控制功能无法使用");
//                    return;
//                }
//                bundle.putString("homeOrDetector", getIntent().getStringExtra("homeOrDetector"));
//                switchFragment = new SwitchFragment();
//                currSelectBottom = 5;
//                ChangeButton(lastSelectBottom, currSelectBottom);
//                lastSelectBottom = 5;
//                switchFragment.setArguments(bundle);
//                transaction.replace(R.id.monitoring_fragment, switchFragment);
//                break;
//        }
//        transaction.commit();
//    }
//
//    //改变底部导航选中颜色
    private void changeButtonColor(int last, int curr) {
        switch (last) {
            case 1:
                ivbt1.setImageResource(R.drawable.monitoring_buttom_1);
                tvbtRealTime.setTextColor(getResources().getColor(R.color.black));
                layCurrent.setClickable(true);
                break;
            case 2:
                ivbt2.setImageResource(R.drawable.monitoring_buttom_2);
                tvbtHistory.setTextColor(getResources().getColor(R.color.black));
                layHistory.setClickable(true);
                break;
            case 3:
                ivbt3.setImageResource(R.drawable.monitoring_buttom_3);
                tvbtWarning.setTextColor(getResources().getColor(R.color.black));
                layAlert.setClickable(true);
                break;
        }
        switch (curr) {
            case 1:
                ivbt1.setImageResource(R.drawable.monitoring_buttom_1_click);
                tvbtRealTime.setTextColor(getResources().getColor(R.color.details_bottom_blue));
                layCurrent.setClickable(false);
                break;
            case 2:
                ivbt2.setImageResource(R.drawable.monitoring_buttom_2_click);
                tvbtHistory.setTextColor(getResources().getColor(R.color.details_bottom_blue));
                layHistory.setClickable(false);
                break;
            case 3:
                ivbt3.setImageResource(R.drawable.monitoring_buttom_3_click);
                tvbtWarning.setTextColor(getResources().getColor(R.color.details_bottom_blue));
                layAlert.setClickable(false);
                break;

        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (resultCode) {
//            case Constant.RESULT_CODE_103:
////                getDetectorInfo(detectorInfo.getOnlyId());
////                detectorInfo = data.getExtras().getParcelable("detectorInfo");
//                getDetectorInfo(detectorInfo.getOnlyId());
//                switch (requestCode) {
//                    case Constant.REQUEST_CODE_201:
//                        Log.e("Moni++++", "进来了");
//                        isChangeInfo = true;
//                        currentFragment = new CurrentFragment();
//
//                        break;
//                }
//                break;
//        }
//    }
//
//    private void changeFragment(android.app.Fragment tarFragment, int viewId) {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("detectorInfo", detectorInfo);
//        bundle.putBoolean("isChangeInfo", isChangeInfo);
//        bundle.putString("homeOrDetector", getIntent().getStringExtra("homeOrDetector"));
//        FragmentManager fragmentManager = getFragmentManager();
//        //开启一个fragment事务
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        //用fragment替换界面
//        tarFragment.setArguments(bundle);
//        fragmentTransaction.replace(viewId, tarFragment);
//        fragmentTransaction.commit();
//    }
//
//    private void NoAuthorityOrTimeOutToButton() {
//        if (!mIsHasHistory || isTimeOut) {
//            ivbt2.setImageResource(R.drawable.monitoring_buttom_timeout_2);
//            ivbt3.setImageResource(R.drawable.monitoring_buttom_timeout_3);
//            ivbt4.setImageResource(R.drawable.monitoring_buttom_timeout_4);
//            tvbtHistory.setTextColor(getResources().getColor(R.color.gray_text));
//            tvbtWarning.setTextColor(getResources().getColor(R.color.gray_text));
//            tvbtGps.setTextColor(getResources().getColor(R.color.gray_text));
//        }
//
////        if (mIsHasHistory){
////            ivbt2.setImageResource(R.drawable.monitoring_buttom_timeout_2);
////            ivbt3.setImageResource(R.drawable.monitoring_buttom_timeout_3);
////            ivbt4.setImageResource(R.drawable.monitoring_buttom_timeout_4);
////            tvbtHistory.setTextColor(getResources().getColor(R.color.gray_text));
////            tvbtWarning.setTextColor(getResources().getColor(R.color.gray_text));
////            tvbtGps.setTextColor(getResources().getColor(R.color.gray_text));
////        }else{
////            if (isTimeOut) {
////
////            }
////        }
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            if (isChangeInfo) {
//                this.setResult(Constant.RESULT_CODE_DETECTORDETAIL_CHANGED);
//            }
//            Log.e("isChangeInfo", isChangeInfo + "");
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//
//    /**
//     * 获取设备详细信息（这里用来刷新detector实体）
//     *
//     * @param deviceId
//     */
//    private void getDetectorInfo(int deviceId) {
//        FHttpCallBack fHttpCallBack = new FHttpCallBack() {
//            @Override
//            public void onPreStart() {
//                super.onPreStart();
//                UIHelper.showLoadingDialog(MonitoringDetailsActivity.this);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                UIHelper.ToastMessage("更新实体类失败");
//                UIHelper.stopLoadingDialog();
//
//            }
//
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                UIHelper.stopLoadingDialog();
//                Result result = new Result();
//                try {
//                    result.parse(t);
//                    if (result.isOk()) {
//                        Detector detector = new Detector();
//                        detector.parse(t);
//                        detectorInfo = detector;
//                        changeFragment(currentFragment, R.id.monitoring_fragment);
//                    }
//                } catch (AppException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ApiDevice.getDetectorInfo(deviceId, fHttpCallBack);
//    }
//
//    private void getDetectorInfoForSwitch(int deviceId) {
//        FHttpCallBack fHttpCallBack = new FHttpCallBack() {
//            @Override
//            public void onPreStart() {
//                super.onPreStart();
//                UIHelper.showLoadingDialog(MonitoringDetailsActivity.this);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                UIHelper.ToastMessage("更新实体类失败");
//                UIHelper.stopLoadingDialog();
//
//            }
//
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                UIHelper.stopLoadingDialog();
//                Result result = new Result();
//                try {
//                    result.parse(t);
//                    if (result.isOk()) {
//                        Detector detector = new Detector();
//                        detector.parse(t);
//                        detectorInfo = detector;
//                    }
//                } catch (AppException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ApiDevice.getDetectorInfo(deviceId, fHttpCallBack);
//    }
//
//    @Override
//    public void refresh(int fromWhere) {
//        getDetectorInfoForSwitch(detectorInfo.getOnlyId());
//    }

    @Override
    public void refresh(int fromWhere) {

    }
}

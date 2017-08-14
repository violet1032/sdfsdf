package com.xcinfo.xc_blueteeth_android.common.activity;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.config.AppConfig;
import com.xcinfo.xc_blueteeth_android.common.config.AppManager;
import com.xcinfo.xc_blueteeth_android.common.utils.StatusColorUtils;

import org.kymjs.kjframe.KJActivity;


/**
 * 所有Activity基类
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2016/2/15 17:35
 * <p/>
 * 版本:
 */
public class BaseActivity extends KJActivity {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        //设置状态栏颜色
        setStatusBar();
    }


    @Override
    public void setRootView() {

    }

    public static void startActivity() {

    }

    @Override
    protected void threadDataInited() {
        super.threadDataInited();
    }

    @Override
    public void initDataFromThread() {
        super.initDataFromThread();
    }

    @Override
    public void initData() {
        super.initData();
        //将activity添加到堆栈
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    public void initWidget() {
        super.initWidget();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View view = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        AppConfig.getInstance();

    }

    @Override
    public void registerBroadcast() {
        super.registerBroadcast();
    }

    @Override
    public void unRegisterBroadcast() {
        super.unRegisterBroadcast();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
    }





    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    protected void setStatusBar() {
        StatusColorUtils.setColor(this, getResources().getColor(R.color.blue_statusbar));
    }
}

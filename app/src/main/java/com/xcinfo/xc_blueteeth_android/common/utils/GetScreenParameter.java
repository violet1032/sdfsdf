package com.xcinfo.xc_blueteeth_android.common.utils;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by com.亚东 on 2017/2/16.
 */

public class GetScreenParameter {
    public static int getScreenWidth(Context mContext){
        WindowManager wm= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }
    public static int getScreenHeight(Context mContext){
        WindowManager wm= (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }
}

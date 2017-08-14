package com.xcinfo.xc_blueteeth_android.common.api;

import android.graphics.Bitmap;
import android.util.Log;


import com.xcinfo.xc_blueteeth_android.common.config.AppConfig;
import com.xcinfo.xc_blueteeth_android.common.utils.JsonUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;

import org.json.JSONException;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.utils.StringUtils;

import java.util.Map;




/**
 * Created by Administrator on 2016/5/18.
 */
public class FHttpCallBack extends HttpCallBack {
    private final String TAG = "FHttpCallBack";
    public static String url;
    public static Map<String, Object> map;
    public static HttpCallBack callBack;

    @Override
    public void onFailure(int errorNo, String strMsg) {
        if (AppConfig.DEBUG) {
            Log.e(TAG, "errorNo:" + errorNo);
            Log.e(TAG, "strMsg:" + strMsg);
            if (strMsg.contains("NoConnection")) {
                UIHelper.ToastMessage("本地无网络连接或服务器断开连接");
            } else if (errorNo == 404) {
                UIHelper.ToastMessage("服务器走远了，稍等片刻马上回来");
            } else {
                UIHelper.ToastMessage("请求失败 errorNo:" + errorNo + " strMsg:" + strMsg);
            }

            Log.e("onFailure", "请求失败 errorNo:" + errorNo + " strMsg:" + strMsg);

        }
        super.onFailure(errorNo, strMsg);
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    @Override
    public void onLoading(long count, long current) {
        super.onLoading(count, current);
    }

    @Override
    public void onPreStart() {
        super.onPreStart();
    }

    @Override
    public void onSuccess(Bitmap t) {
        super.onSuccess(t);
    }

    @Override
    public void onSuccess(byte[] t) {
        super.onSuccess(t);
    }

    @Override
    public void onSuccess(Map<String, String> headers, byte[] t) {
        // 获取cookie
        String str = new String(t);
        try {
            JsonUtils jsonUtils = new JsonUtils(str);
            String cookie = jsonUtils.getString("JSESSIONID");
            if (!StringUtils.isEmpty(cookie)) {
                // 将cookie保存在本地
                AppConfig.getInstance().mPreSet("cookie", cookie);
                // 记录保存cookie的时间
                AppConfig.getInstance().setCookieTime(System.currentTimeMillis());
                if (AppConfig.DEBUG)
                    Log.e(TAG, "保存cookie:::" + cookie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (AppConfig.DEBUG) {
            Log.e(TAG, "返回内容:" + new String(t));
        }

        // 返回内容如果是"需要登录"，则弹出需要登录的提示
      /*  try {
            Result result = new Result();
            result.parse(new String(t));
            if (result.getMsg().equals(AppContext.appContext.getString(R.string
                    .not_login))) {
                // 执行登录操作
                UIHelper.ToastMessage(AppContext.appContext.getString(R.string
                        .not_login));
            } else {
                super.onSuccess(headers, t);
            }
        } catch (AppException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onSuccess(String t) {
        Log.e("onSuccess", t);
        super.onSuccess(t);
    }

}

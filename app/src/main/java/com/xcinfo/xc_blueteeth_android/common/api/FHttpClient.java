package com.xcinfo.xc_blueteeth_android.common.api;

import android.util.Log;


import com.xcinfo.xc_blueteeth_android.common.config.AppConfig;
import com.xcinfo.xc_blueteeth_android.common.config.AppContext;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.Request;

import java.util.Map;



/**
 * Created by Administrator on 2016/5/18.
 */
public class FHttpClient extends KJHttp {
    private final String TAG = "FHttpClient";
    private boolean setCookie = false;
    private boolean setDizCuzCookie = false;
    private boolean login = false;

    public FHttpClient(HttpConfig httpConfig) {
        super(httpConfig);
    }

    public Request<byte[]> post(String url, Map<String, Object> httpParams, HttpCallBack
            callback) {
        if (!AppContext.online) {
            return null;
        }

        if (!login) {
            FHttpCallBack.url = url;
            FHttpCallBack.callBack = callback;
            FHttpCallBack.map = httpParams;
        }

        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }

        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        Log.e("dizCookie", setDizCuzCookie + "");
        String dizCuzCookie = (String) SPUtils.get(AppContext.applicationContext, "dizcuz_cookie", "noCookie");
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        Log.e("dizCookie", "dizCuzCookie:" + dizCuzCookie);
        //判断是带哪里的cookie
        if (setDizCuzCookie) {
            params.putHeaders("Cookie", dizCuzCookie);
        } else {
            params.putHeaders("Cookie", cookie);
        }
        if (AppConfig.DEBUG)
            if (setDizCuzCookie) {
                Log.e(TAG, "http请求cookie:" + dizCuzCookie);
            } else {
                Log.e(TAG, "http请求cookie:" + cookie);
            }
//            }
//        } else {
//            setCookie = false;
//        }
//        }
        return super.post(url, params, callback);
    }

    public Request<byte[]> get(String url, Map<String, Object> httpParams, HttpCallBack
            callback) {
        if (!AppContext.online) {
            return null;
        }

        if (!login) {
            FHttpCallBack.url = url;
            FHttpCallBack.callBack = callback;
            FHttpCallBack.map = httpParams;
        }

        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }

        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        Log.e("cookie+++", "cookie2:" + cookie);
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        params.putHeaders("Cookie", cookie);
        if (AppConfig.DEBUG)
            Log.e(TAG, "http请求cookie:" + cookie);
//            }
//        } else {
//            setCookie = false;
//        }
//        }
        return super.get(url, params, callback);
    }

    public Request<byte[]> getDizCuz(String url, Map<String, Object> httpParams, String cookie, HttpCallBack
            callback) {
        if (!AppContext.online) {
            return null;
        }

        if (!login) {
            FHttpCallBack.url = url;
            FHttpCallBack.callBack = callback;
            FHttpCallBack.map = httpParams;
        }

        HttpParams params = new HttpParams();
        params.put("", "");
        for (String key :
                httpParams.keySet()) {
            if (httpParams.get(key) != null)
                params.put(key, httpParams.get(key).toString());
            else if (AppConfig.DEBUG)
                UIHelper.ToastMessage(key + "参数为空");
        }

//        String cookie = "JSESSIONID=" + AppConfig.getInstance().getmPre().getString("cookie", null);
        Log.e("cookie+++", "cookie2:" + cookie);
        if (AppConfig.DEBUG) {
            Log.e(TAG, "http请求参数:" + params.getUrlParams());
            Log.e(TAG, "http请求地址:" + url);
        }
        params.putHeaders("Cookie", cookie);
        if (AppConfig.DEBUG)
            Log.e(TAG, "http请求cookie:" + cookie);
//            }
//        } else {
//            setCookie = false;
//        }
//        }
        return super.get(url, params, callback);
    }

    public void post(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie) {
        this.setCookie = setCookie;
        post(url, params, callback);
    }

    public void post(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie, boolean login) {
        this.setCookie = setCookie;
        this.login = login;
        post(url, params, callback);
    }


    public void get(String url, String cookie, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie, boolean login) {
        this.setCookie = setCookie;
        this.login = login;
        getDizCuz(url, params, cookie, callback);

    }

    public void get(String url, String cookie, Map<String, Object> params, HttpCallBack callback, boolean
            setCookie) {
        this.setCookie = setCookie;
        getDizCuz(url, params, cookie, callback);

    }

    public void postDizCuz(String url, Map<String, Object> params, HttpCallBack callback, boolean
            setDizCuzCookie) {
        this.setDizCuzCookie = setDizCuzCookie;
        post(url, params, callback);
    }

}

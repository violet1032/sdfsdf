package com.xcinfo.xc_blueteeth_android.common.config;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;


import com.xcinfo.xc_blueteeth_android.common.api.FHttpClient;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.servicetest.MyService;
import com.xcinfo.xc_blueteeth_android.servicetest.ServiceActivity;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.http.HttpConfig;

import java.io.File;


/**
 * Created by Administrator on 2016/5/17.
 */
public class AppContext extends Application {
    public static int VERSION_UPDATE = 0;//默认是会检查升级，如果点了下次再说或者立即更新，则变为1，就本次应用使用完毕之前就不会再请求更新版本接口
    public static SQLiteDatabase db;
    public static Context applicationContext;
    public static AppContext appContext;
    public static int notificationId;



    // 设备屏幕宽高
    public static int screenHeight, screenWidth;
    public static boolean online = true;
    // http相关
    public static FHttpClient http;

    // 获取网络图片对象
    public static KJBitmap bitmap;

    @Override
    public void onCreate() {
        super.onCreate();


        applicationContext = getApplicationContext();
        appContext = this;

        // 获取屏幕尺寸
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        // http实例化
        HttpConfig config = new HttpConfig();
        HttpConfig.DEBUG = true;
        HttpConfig.TIMEOUT = AppConfig.getInstance().getHttpTimeout();
        config.cacheTime = AppConfig.getInstance().getHttpCacheTime();
        http = new FHttpClient(config);
        BitmapConfig bitmapConfig = new BitmapConfig();
        bitmapConfig.cacheTime = AppConfig.getInstance().getBitmapCacheTime();
        bitmap = new KJBitmap(bitmapConfig);

        /**
         * 创建bin文件目录
         */
        String binFilDir=getFilesDir().toString()+"/binFiles";

        File binFiles=new File(getBinFileDir());
        if (!binFiles.exists()){
            Log.d("@@makeFileDir","binFiles");
            if(binFiles.mkdirs()){
                Log.d("@@makeFileDir","success");
            }else {
                Log.d("@@makeFileDir","failed");
            }
        }
        File[] files=binFiles.listFiles();
        if (files.length==0){
            Log.d("@@文件夹为空","failed");
        }else {
            for (int i = 0; i < files.length; i++) {
                Log.d("@@文件：",files[i].getName()+" path: "+files[i].getPath());
            }
        }

    }


    public static String getBinFileDir(){
        return "/sdcard/binFiles";
    }



}

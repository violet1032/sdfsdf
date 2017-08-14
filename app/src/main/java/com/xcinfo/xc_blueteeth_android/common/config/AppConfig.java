package com.xcinfo.xc_blueteeth_android.common.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.xcinfo.xc_blueteeth_android.R;

import org.kymjs.kjframe.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2016/5/17.
 */
public class AppConfig {


    public static final boolean DEBUG = true;

    public final static int TEXT_SCALE = 3; // 大号字体放大号数
    private final static String IS_TEXT_SCALE = "is_text_scale"; // 字体是否放大
    private final static boolean IS_TEXT_SCALE_DEFAULT = false; // 默认为正常字体

    /**
     * http缓存时间
     */
    private final static String HTTP_CACHE_TIME = "http_cache_time";
    //    private final static int HTTP_CACHE_TIME_DEFAULT = 1000 * 60 * 5;// 默认http缓存时间5分钟
    private final static int HTTP_CACHE_TIME_DEFAULT = 0;// 默认http缓存时间0ms
    /**
     * 图片缓存时间
     */
    private final static String BITMAP_CACHE_TIME = "bitmap_cache_time";
    private final static int BITMAP_CACHE_TIME_DEFAULT = 1000 * 60 * 10;// 默认图片缓存时间10分钟
    /**
     * http超时时间
     */
    private final static String HTTP_TIMEOUT = "http_time_out";
    private final static int HTTP_TIMEOUT_DEFAULT = 1000 * 10;// 默认http超时时间10s
    /**
     * cookie过期时间
     */
    private final static String COOKIE_CACHE_TIME = "cookie_cache_time";
    private final static int COOKIE_CACHE_TIME_DEFAULT = 1000 * 60 * 60 * 24 * 7;// 默认cookie过期时间7天

    /**
     * 背景图片地址
     */
    private final static String BG_PATH = "bg_path";

    /**
     * cookie保存时间
     */
    private final static String COOKIE_TIME = "cookie_time";


    private final static String APP_CONFIG = "config";

    /*
     * 图片保存地址
    **/

    public static final String SAVE_IMAGE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + AppContext.applicationContext.getResources().getString(
            R.string.app_name_en)
            + File.separator
            + "image"
            + File.separator;


   /* *//**
     * 临时文件保存地址
     *//*
    public static final String SAVE_TEMP_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + AppContext.applicationContext.getResources().getString(
            R.string.app_name_en)
            + File.separator
            + "temp"
            + File.separator;
*/
    private Context mContext;

    private static AppConfig appConfig;

    private SharedPreferences mPre;

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    public static AppConfig getInstance() {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = AppContext.applicationContext;
        }
        return appConfig;
    }

    private AppConfig() {
        //mPre = getSharedPreferences(AppContext.applicationContext);
    }

    /**
     * 获取图片保存地址
     *
     * @return
     */
   /* public static String getSaveImagePath() {
        // 判断有没有sd卡
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            // 如果没有文件夹则创建
            Log.e("", "有sdcard:::" + SAVE_IMAGE_PATH);
            File file = new File(SAVE_IMAGE_PATH);
            if (!file.exists()) {
                Log.e("", "创建文件夹");
                file.mkdirs();
            }
            return SAVE_IMAGE_PATH;
        } else {
            String path = "/sdcard/" + AppContext.appContext.getResources().getString(R.string
                    .app_name_en) + "/image/";
            File file = new File(path);
            Log.e("", "无sdcard:::" + path);
            if (!file.exists()) {
                Log.e("", "创建了文件夹");
                file.mkdirs();
            }
            return path;
        }
    }*/

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }
    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void mPreSet(String key, String value) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void mPreSet(String key, int value) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void mPreSet(String key, boolean value) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void mPreSet(String key, float value) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void mPreSet(String key, long value) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void mPreRemove(String key) {
        SharedPreferences.Editor editor = mPre.edit();
        editor.remove(key);
        editor.commit();
    }

    public SharedPreferences getmPre() {
        if (mPre != null)
            return mPre;
        return getSharedPreferences(AppContext.appContext);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

    /**
     * 设置http请求缓存时间
     *
     * @param time
     */
    public void setHttpCacheTime(int time) {
        set(HTTP_CACHE_TIME, time + "");
    }

    /**
     * 获取http请求缓存时间
     *
     * @return 默认返回10s
     */
    public int getHttpCacheTime() {
        String str = get(HTTP_CACHE_TIME);
        if (StringUtils.isEmpty(str))
            return HTTP_CACHE_TIME_DEFAULT;
        else
            return StringUtils.toInt(str);
    }

    /**
     * 设置http请求超时时间
     *
     * @param time
     */
    public void setHttpTimeout(int time) {
        set(HTTP_TIMEOUT, time + "");
    }

    /**
     * 获取http请求超时时间
     *
     * @return 默认返回10s
     */
    public int getHttpTimeout() {
        String str = get(HTTP_TIMEOUT);
        if (StringUtils.isEmpty(str))
            return HTTP_TIMEOUT_DEFAULT;
        else
            return StringUtils.toInt(str);
    }

    /**
     * 设置图片缓存时间
     *
     * @param time
     */
    public void setBitmapCacheTime(int time) {
        set(BITMAP_CACHE_TIME, time + "");
    }

    /**
     * 获取图片缓存时间
     *
     * @return 默认返回1分钟
     */
    public int getBitmapCacheTime() {
        String str = get(BITMAP_CACHE_TIME);
        if (StringUtils.isEmpty(str))
            return BITMAP_CACHE_TIME_DEFAULT;
        else
            return StringUtils.toInt(str);
    }

    /**
     * 设置cookie过期时间
     *
     * @param time
     */
    public void setCookieCacheTime(int time) {
        set(COOKIE_CACHE_TIME, time + "");
    }

    /**
     * 获取cookie过期时间
     *
     * @return 默认返回1天
     */
    public int getCookieCacheTime() {
        String str = get(COOKIE_CACHE_TIME);
        if (StringUtils.isEmpty(str))
            return COOKIE_CACHE_TIME_DEFAULT;
        else
            return StringUtils.toInt(str);
    }

    /**
     * 设置cookie时间
     *
     * @param time
     */
    public void setCookieTime(long time) {
        set(COOKIE_TIME, time + "");
    }

    /**
     * 获取cookie时间
     *
     * @return 默认返回0
     */
    public long getCookieTime() {
        String str = get(COOKIE_TIME);
        if (StringUtils.isEmpty(str))
            return 0;
        else
            return StringUtils.toLong(str);
    }

    /**
     * 设置字体是否放大
     *
     * @param b
     */
    public void setTextScale(boolean b) {
        set(IS_TEXT_SCALE, b + "");
    }

    /**
     * 获取字体是否为放大
     *
     * @return
     */
    public boolean getTextScale() {
        String str = get(IS_TEXT_SCALE);
        if (StringUtils.isEmpty(str))
            return IS_TEXT_SCALE_DEFAULT;
        else
            return StringUtils.toBool(str);
    }

    /**
     * 判断cookie是否过期
     *
     * @return
     */
    public boolean isCookieOutTime() {
        long lastTime = AppConfig.getInstance().getCookieTime();
        if (System.currentTimeMillis() > lastTime + AppConfig.getInstance().getCookieCacheTime()) {
            // 如果cookie过期
            return true;
        }
        return false;
    }

    /**
     * 设置背景图片地址
     *
     * @param path
     */
    public void setBgPath(String path) {
        set(BG_PATH, path + "");
    }

    /**
     * 获取背景图片地址
     *
     * @return
     */
    public String getBgPath() {
        String str = get(BG_PATH);
        return str;
    }

    /*
     * 属性设置依次扩展
     */

    /*
     *将bitmap图片已png的格式保存在本地SD卡
     */
//    public static void saveBitmap(Bitmap bm) {
//        String filename = System.currentTimeMillis() + "";
//        File file = new File(AppConfig.getSaveImagePath() + filename + ".png");
//        if (file.exists()) {
//            file.delete();
//        }
//        FileOutputStream out;
//        try {
//            out = new FileOutputStream(file);
//            if (bm.compress(Bitmap.CompressFormat.PNG, 90, out)) {
//                out.flush();
//                out.close();
//               // UIHelper.ToastMessage("已将该图片保存至" + AppConfig.getSaveImagePath() + filename + ".png");
//
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

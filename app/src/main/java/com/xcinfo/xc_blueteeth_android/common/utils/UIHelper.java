package com.xcinfo.xc_blueteeth_android.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.config.AppConfig;
import com.xcinfo.xc_blueteeth_android.common.config.AppContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;




/**
 * 界面ui控件等工具类
 * <p/>
 * description:
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 20:18
 * <p/>
 * version:1.0
 */
public class UIHelper {
    private static AlertDialog dlg;


    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>";
    public final static String WEB_STYLE = linkCss
            + "<style>* {font-size:13px;line-height:23px;color:#999;font-family:STXihei;} a {color:#3E62A6;} img {max-width:310px;} "
            + "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#ffeeeeee;padding:2px;} </style>";


    /**
     * 获取屏幕尺寸
     */
    public static int getDisplayWidth() {
        DisplayMetrics metrics = AppContext.applicationContext.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        return screenWidth;
    }

    public static int getDisplayHeight() {
        DisplayMetrics metrics = AppContext.applicationContext.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        return screenHeight;

    }

    /**
     * 获得状态栏的高度（单位为px）
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getDisplayWidth();
        int height = getDisplayHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getDisplayWidth();
        int height = getDisplayHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 显示加载等待界面
     *
     * @param context
     */
    public static void showLoadingDialog(Context context,String title) {


        if (dlg == null) {
            dlg = new AlertDialog.Builder(context, R.style.CustomDialog).create();
            if (!TextUtils.isEmpty(title))
            dlg.setTitle(title);
            dlg.show();
            dlg.setCancelable(false);
            Window window = dlg.getWindow();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            window.setContentView(view);
        } else {
            if (!dlg.isShowing()) {
                dlg = new AlertDialog.Builder(context, R.style.CustomDialog).create();
                if (!TextUtils.isEmpty(title))
                    dlg.setTitle(title);
                dlg.show();
                dlg.setCancelable(false);
                Window window = dlg.getWindow();
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.dialog_loading, null);
                window.setContentView(view);
            }
        }
    }

    /**
     * 取消加载界面
     */
    public static void stopLoadingDialog(String title) {
        if (dlg != null) {
            if (!TextUtils.isEmpty(title))
            dlg.setTitle(title);
            dlg.dismiss();

        }
    }

    /**
     * 编辑栏错误提示
     *
     * @param str
     * @return
     * @Description
     * @author zipeng
     */
    public static CharSequence edtError(String str) {
        return Html.fromHtml("<font color=#ff0000>" + str + "</font>");
    }

    /**
     * 编辑栏错误提示
     *
     * @param editText
     * @param str
     */
    public static void edtError(EditText editText, String str) {
        if (editText != null)
            editText.setError(edtError(str));
    }

//    /**
//     * 发送App异常崩溃报告
//     *
//     * @param cont
//     * @param crashReport
//     */
//    public static void sendAppCrashReport(final Context cont,
//                                          final String crashReport) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
//        builder.setIcon(android.R.drawable.ic_dialog_info);
//        builder.setTitle(R.string.app_error);
//        builder.setMessage(R.string.app_error_message);
//        builder.setPositiveButton(R.string.submit_report,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 发送异常报告
//                        Intent i = new Intent(Intent.ACTION_SEND);
//                        // i.setType("text/plain"); //模拟器
//                        i.setType("message/rfc822"); // 真机
//                        i.putExtra(Intent.EXTRA_EMAIL,
//                                new String[]{"zhangdeyi@oschina.net"});
//                        i.putExtra(Intent.EXTRA_SUBJECT, "客户端 - 错误报告");
//                        i.putExtra(Intent.EXTRA_TEXT, crashReport);
//                        cont.startActivity(Intent.createChooser(i, "发送错误报告"));
//                        // 退出
//                        AppManager.getAppManager().AppExit(cont);
//                    }
//                });
//        builder.setNegativeButton(R.string.sure,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        // 退出
//                        AppManager.getAppManager().AppExit(cont);
//                    }
//                });
//        builder.show();
//    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(String msg) {
        Toast toast = Toast.makeText(AppContext.applicationContext, msg,
                Toast.LENGTH_SHORT);
        //可以控制toast显示的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示错误页面
     *
     * @param viewGroup
     * @param activity
     */
/*    public static void showErrorLayout(ViewGroup viewGroup,
                                       final Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity
                .getApplicationContext());
        View v = inflater.inflate(R.layout.error_hint, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // 隐藏所有子控件
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(View.GONE);
        }

        viewGroup.addView(v, layoutParams);

        LinearLayout back = (LinearLayout) v
                .findViewById(R.id.error_hint_lay_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

    }*/

    /**
     * 发送通知
     *
     * @param context    点击通知后跳转到的界面
     * @param tickerText 任务栏显示的内容
     * @param title      通知栏显示的标题
     * @param content    通知栏显示的内容
     * @Description
     * @author zipeng
     */
/*    public static void sendNotification(Context context, Intent intent,
                                        String tickerText, String title, String content) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Notification n = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(tickerText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .build();

//        n.icon = R.drawable.ic_launcher;
//        n.tickerText = tickerText;
//        n.when = System.currentTimeMillis();
//        n.defaults = Notification.DEFAULT_SOUND;
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
        // n.flags=Notification.FLAG_ONGOING_EVENT;
        nm.notify(AppContext.notificationId, n);
        AppContext.notificationId++;
    }*/

    /**
     * 将dip单位的数值转化为px单位的值
     *
     * @param dpValue
     * @return
     * @Description
     * @author zipeng
     */
    public static int dip2px(float dpValue) {
        final float scale = AppContext.appContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }

    /**
     * 将px单位的数值转化为dip单位的值
     *
     * @param dpValue
     * @return
     * @Description
     * @author zipeng
     */
    public static int px2dip(float dpValue) {
        final float scale = AppContext.appContext.getResources().getDisplayMetrics().density;
        return (int) ((dpValue) / scale);

    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, AppContext.appContext.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(float pxVal) {
        return (pxVal / AppContext.appContext.getResources().getDisplayMetrics().scaledDensity);
    }


    /**
     * 获取宽度
     *
     * @param view
     * @return
     */
    public static int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    /**
     * 获取高度
     *
     * @param view
     * @return
     */
    public static int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * @param bmp
     * @param file
     * @Description 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩
     * @author Administrator
     */
    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while ((float) baos.toByteArray().length / 1024 > (float) 120) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 减小字号
     *
     * @param view
     */
    public static void setTextSizeL(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextSizeL(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(px2dip(tv.getTextSize()) - AppConfig.TEXT_SCALE);
        } else
            return;
    }

    /**
     * 增加字号
     *
     * @param view
     */
    public static void setTextSizeH(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextSizeH(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(px2dip(tv.getTextSize()) + AppConfig.TEXT_SCALE);
        } else
            return;
    }

    //static AssetManager mgr = AppContext.appContext.getAssets();//得到AssetManager
    //static Typeface tf = Typeface.createFromAsset(mgr, "DroidSansFallback.ttf");//根据路径得到Typeface

    /**
     * 设置字体
     *
     * @param view
     */
/*    public static void setTextFont(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextFont(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTypeface(tf);//设置字体
        } else
            return;
    }*/




    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}

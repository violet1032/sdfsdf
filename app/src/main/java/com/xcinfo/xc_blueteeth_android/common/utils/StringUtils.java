package com.xcinfo.xc_blueteeth_android.common.utils;


import android.util.Log;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 字符串辅助类
 * <p/>
 * description:继承kj框架中的StringUtils,自定义在此扩展
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 20:37
 * <p/>
 * version:1.0
 */
public class StringUtils extends org.kymjs.kjframe.utils.StringUtils {

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new
            ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault());
                }
            };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new
            ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                }
            };



    private final static String[] months = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};
    private final static String[] monthsCN = new String[]{"一月", "二月", "三月", "四月",
            "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};

    /**

    public static String getConstellation(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算年龄
     *
     * @param birthday
     * @return
     */
    public static int getAge(String birthday) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date mydate = null;
        try {
            mydate = myFormatter.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000) + 1;
        int year = (int) (day / 365);
        return year;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将时间戳转换为yyyy-MM-dd hh:mm
     *
     * @return
     */
    public static String date_fromat_change(long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.getDefault());
        return sdf.format(new Date(l));
    }

    /**
     * 将时间戳转换为yyyy-MM-dd hh:mm:ss
     *
     * @return
     */
    public static String date_fromat_change_2(long l) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        return sdf.format(new Date(l));
    }

    /**
     * 将形如Thu Apr 30 12:04:00 CST 2015的时间转化为long
     *
     * @param str
     * @return
     */
    public static long date_fromat_change(String str) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        try {
            Date date = sdf1.parse(str);
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
            // String sDate = sdf.format(date);
            // System.out.println(sDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将形如yyyy-MM-dd HH:mm:ss,或者yyyy-MM-dd 的时间转化为long
     *
     * @param str
     * @return
     */
    public static long date_fromat_change_4(String str) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.UK);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
                Locale.UK);
        try {
            Date date = sdf1.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            try {
                Date date = null;
                date = sdf2.parse(str);
                return date.getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 将形如yyyy/MM/dd HH:mm的时间转化为long
     *
     * @param str
     * @return
     */
    public static long date_fromat_change_3(String str) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm",
                Locale.UK);
        try {
            Date date = sdf1.parse(str);
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
            // String sDate = sdf.format(date);
            // System.out.println(sDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return 0;
    }

    public static boolean isEmpty(CharSequence input) {
        if (input != null && !"".equals(input)) {
            if (input.equals("null"))
                return true;
            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if (c != 32 && c != 9 && c != 13 && c != 10) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        return firendly_time(time, true);
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate, boolean isMinite) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        return firendly_time(time, isMinite);
    }

    /**
     * 以友好的方式显示时间，包括分钟
     *
     * @return
     */
    public static String friendly_time(long longtime) {
        Date time = new Date(longtime);
        return firendly_time(time, true);
    }

    /**
     * 以友好的方式显示时间，包括分钟
     *
     * @return
     */
    public static String friendly_time(long longtime, boolean isMinite) {
        Date time = new Date(longtime);
        return firendly_time(time, isMinite);
    }

    public static String firendly_time(Date time, boolean isMinite) {
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(time);

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            long l = cal.getTimeInMillis() - time.getTime();
            if (l > 0) {
                // 如果是此时之前的时间
                int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
                if (hour == 0) {
                    ftime = Math
                            .max((cal.getTimeInMillis() - time.getTime()) / 60000,
                                    1)
                            + "分钟前";
                } else {
                    ftime = hour + "小时";
                    if (isMinite)
                        ftime = ftime
                                + Math.max(
                                (cal.getTimeInMillis() - time.getTime() - hour * 3600000) / 60000,
                                1) + "分钟前";

                }
            } else {
                // 如果是此时之后的时间
                int hour = (int) ((time.getTime() - cal.getTimeInMillis()) / 3600000);
                if (hour == 0) {
                    ftime = Math
                            .max((time.getTime() - cal.getTimeInMillis()) / 60000,
                                    1)
                            + "分钟后";
                } else {
                    ftime = hour + "小时";
                    if (isMinite)
                        ftime = ftime
                                + Math.max(
                                (time.getTime() - cal.getTimeInMillis() - hour * 3600000) / 60000,
                                1) + "分钟后";

                }
            }
            return ftime;
        }

        // 是否是同一年
        boolean isAyear = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        if (sdf.format(new Date(cal.getTimeInMillis()))
                .equals(sdf.format(time)))
            isAyear = true;

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;

        long l = cal.getTimeInMillis() - time.getTime();
        if (l > 0) {
            // 如果是此时之前的时间
            int days = (int) (ct - lt);
            if (days == 0) {
                int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
                if (hour == 0)
                    ftime = Math
                            .max((cal.getTimeInMillis() - time.getTime()) / 60000,
                                    1)
                            + "分钟前";
                else
                    ftime = hour + "小时";
                if (isMinite)
                    ftime = ftime
                            + Math.max(
                            (cal.getTimeInMillis() - time.getTime() - hour * 3600000) / 60000,
                            1) + "分钟前";
            } else if (days == 1) {
                ftime = "昨天 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (days == 2) {
                ftime = "前天 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (days > 2 && isAyear) {
                ftime = (cal2.get(Calendar.MONTH) + 1) + "月"
                        + cal2.get(Calendar.DAY_OF_MONTH) + "日 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (!isAyear) {
                ftime = cal2.get(Calendar.YEAR) + "年"
                        + (cal2.get(Calendar.MONTH) + 1) + "月"
                        + cal2.get(Calendar.DAY_OF_MONTH) + "日 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            }
        } else {
            // 如果是此时之后的时间
            int days = (int) (lt - ct);
            if (days == 0) {
                int hour = (int) ((time.getTime() - cal.getTimeInMillis()) / 3600000);
                if (hour == 0)
                    ftime = Math
                            .max((time.getTime() - cal.getTimeInMillis()) / 60000,
                                    1)
                            + "分钟后";
                else
                    ftime = hour + "小时";
                if (isMinite)
                    ftime = ftime
                            + Math.max(
                            (time.getTime() - cal.getTimeInMillis() - hour * 3600000) / 60000,
                            1) + "分钟后";
            } else if (days == 1) {
                ftime = "明天";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (days == 2) {
                ftime = "后天";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (days > 2 && isAyear) {
                ftime = (cal2.get(Calendar.MONTH) + 1) + "月"
                        + cal2.get(Calendar.DAY_OF_MONTH) + "日 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            } else if (!isAyear) {
                ftime = cal2.get(Calendar.YEAR) + "年"
                        + (cal2.get(Calendar.MONTH) + 1) + "月"
                        + cal2.get(Calendar.DAY_OF_MONTH) + "日 ";
                if (isMinite)
                    ftime = ftime + cal2.get(Calendar.HOUR) + "点"
                            + cal2.get(Calendar.MINUTE) + "分";
            }
        }
        return ftime;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time_2(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 传入yyyy-MM-dd HH:mm:ss格式的时间，获取是该月的多少日
     *
     * @param str
     * @return
     */
    public static int getDay(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 传入yyyy-MM-dd HH:mm:ss格式的时间，获取月份
     *
     * @param str
     * @return
     */
    public static int getMonth(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 传入yyyy-MM-dd HH:mm:ss格式的时间，获取英文月份
     *
     * @param str
     * @return
     */
    public static String getMonthEN(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int i = calendar.get(Calendar.MONTH);
            return months[i];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 传入yyyy-MM-dd HH:mm:ss格式的时间，获取中文月份
     *
     * @param str
     * @return
     */
    public static String getMonthCN(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int i = calendar.get(Calendar.MONTH);
            return monthsCN[i];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 电话号码隐藏第 hideStart到 hideEnd  位字符
     *
     * @param phone
     * @return
     */
    public static String hidePhoneNumber(String phone, int hideStart, int hideEnd) {
        if (phone.length() > hideEnd) {
            String str = phone.substring(hideStart, hideEnd);
            phone = phone.replace(str, "****");
            return phone;
        } else {
            Log.e("hidePhoneError", "隐藏的电话号码末尾位数大于电话号码位数总长度，请检查hidePhoneNumber()");
            return null;
        }
    }

    /**
     * 获取图片完整Url地址
     *
     * @param url
     * @param thumb
     * @return
     */
  /*  public static String getImgHttpUrl(String url, boolean thumb) {
        if (!url.startsWith("http")) {
            // 如果地址是已"/"开头 ，则把斜杠去掉
            if (url.startsWith("/"))
                url = url.substring(1);

            // 如果已有swust-home/则去掉
            if (url.indexOf("swust-home") >= 0)
                url = url.replace("swust-home/", "");
            url = URLs.APP_STORE_HOST + url;
            if (thumb) {
                // 获取图片名字
                int s = 0;
                while (url.indexOf("/", s + 1) > 0) {
                    s = url.indexOf("/", s + 1);
                }
                String name = url.substring(s + 1);
                String rename = "thumb_" + name;
                url = url.replace(name, rename);
            }
        }
        return url;
    }*/

//    /**
//     * 获取新闻的分享地址
//     *
//     * @param newsid
//     * @return
//     */
//    public static String getNewsSharedURL(int newsid) {
//        return URLs.APP_STORE_HOST + "viewNews?newsid=" + newsid;
//    }


//    public static String newContentInit(String body) {
//        // 去掉所有的class和style样式属性
//        body = body.replaceAll("class.?=.?\"[a-z]*\"", "");
//        body = body.replaceAll("style.?=.?\"[ ;a-z0-9:-]*\"", "");
//
//        // 添加web样式
//        body = UIHelper.WEB_STYLE + body;
//        return body;
//    }

//    /**
//     * 获取性别文字信息
//     *
//     * @param genderEnum
//     * @return
//     */
//    public static String getGender(GenderEnum genderEnum) {
//        switch (genderEnum) {
//            case FEMALE:
//                return AppContext.appContext.getString(R.string.gender_female);
//            case MALE:
//                return AppContext.appContext.getString(R.string.gender_male);
//            case UNKNOWN:
//                return AppContext.appContext.getString(R.string.gender_unknow);
//        }
//        return "";
//    }

//    /**
//     * 获取性别文字信息
//     *
//     * @param genderEnum
//     * @return
//     */
//    public static String getGender(E_GENDER_TYPE genderEnum) {
//        switch (genderEnum) {
//            case female:
//                return AppContext.appContext.getString(R.string.gender_female);
//            case male:
//                return AppContext.appContext.getString(R.string.gender_male);
//            case unkonw:
//                return AppContext.appContext.getString(R.string.gender_unknow);
//        }
//        return "";
//    }
}

package com.example.rivenlee.recorddialog.utils;

import java.util.Calendar;

/**
 * author: rivenlee
 * date: 2018/11/1
 * email: rivenlee0@gmail.com
 */
public class DateUtils {

    public static Calendar calendar = Calendar.getInstance();

    /**
     * @return yyyy-mm-dd
     */
    public static String getDate() {
        return getYear() + "-" + getMonth() + "-" + getDay();
    }

    public static String getYear() {
        return calendar.get(Calendar.YEAR) + "";
    }

    public static String getMonth() {
        int month = calendar.get(Calendar.MONTH) + 1;
        return month + "";
    }

    public static String getDay() {
        return calendar.get(Calendar.DATE) + "";
    }

    public static String secondToTime(long second) {
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second / 60;            //转换分钟
        second = second % 60;                //剩余秒数
        if (hours != 0) {
            return hours + "小时" + minutes + "分" + second + "秒";
        } else if (minutes != 0) {
            return minutes + "分" + second + "秒";
        }
        return second + "秒";
    }

    public static String getFormatHMS(long time) {
        time = time / 1000;
        int s = (int) (time % 60);
        int m = (int) (time / 60);
        int h = (int) (time / 3600);
        return String.format("%02d:%02d:%02d", h, m, s);

    }
}

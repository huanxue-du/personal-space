package com.sz.huanxue.androidapp.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * created
 *
 * @author Daikin.Da
 */
public class CalendarUtil {

    public static int getWeekDay(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
       return c.get(Calendar.DAY_OF_WEEK);

    }
    public static int getCurYear(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.get(Calendar.YEAR);
    }
    public static int getCurrMonth(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
       return  c.get(Calendar.MONTH) + 1;

    }

    public static int getCurrDay(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return  c.get(Calendar.DAY_OF_MONTH);
    }


    public static boolean isSameDay(long time,long time1){
        String t1 =yyyyMMddStrFromLong(time);
        return TextUtils.equals(yyyyMMddStrFromLong(time),yyyyMMddStrFromLong(time1));
    }

    public static String yyyyMMddStrFromLong(long ms) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(ms));
    }

}

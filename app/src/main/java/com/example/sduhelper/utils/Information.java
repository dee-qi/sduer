package com.example.sduhelper.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is sduer
 * Created by qidi on 2017/7/26.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class Information {
    private static final String TAG = "@week";

    public static String api_token = null;

    public static boolean isOnTrial = false;

    public static void setToDefault(){
        isOnTrial = false;
    }

    public static int getCurrentWeekCount(){
        //这里设置开学日期。
        int beginningYear,beginningMonth,beginningDay;
        beginningYear = 2017;
        beginningMonth = 9;
        beginningDay = 10;

        Calendar calendar = Calendar.getInstance();
        int currentWeekOfTerm = calendar.get(Calendar.WEEK_OF_YEAR)+4;
        calendar.set(beginningYear,beginningMonth,beginningDay);
        int beginningWeekOfTerm =calendar.get(Calendar.WEEK_OF_YEAR);

        int weekOfTerm = currentWeekOfTerm - beginningWeekOfTerm+1;

        // 如果是在假期就返回-1
        if(weekOfTerm >18||weekOfTerm<1){
            weekOfTerm = -1;
        }

        return weekOfTerm;
    }

    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;//莫名其妙，加一才正常。
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year+"年"+month+"月"+day+"日";
    }

    public static int getCurrentWeekday(){
        Calendar calendar = Calendar.getInstance();
        //calendar返回的是以周日为每周的第一天，这里减一是为了换算成周一为每周的第一天的情况
        int weekday = calendar.get(Calendar.DAY_OF_WEEK)-1;
        return weekday;
    }

    public static String getWeekday(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek){
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            case 1:
                return "周日";
        }
        return "";
    }

    public static int getYear(){Calendar calendar = Calendar.getInstance(); return calendar.get(Calendar.YEAR);}
    public static int getMonth(){Calendar calendar = Calendar.getInstance(); return calendar.get(Calendar.MONTH)+1;}
    public static int getDay(){Calendar calendar = Calendar.getInstance(); return calendar.get(Calendar.DAY_OF_MONTH);}

    public static String stamp2Date(long stamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] s = sdf.format(stamp).split("-");
        String date = "";
        if(s.length ==3) {
            date = s[0] + "年" + s[1] + "月" + s[2] + "日";
        } else {
            date = "格式出错";
        }
//        Date mDate = new Date(stamp);
//        String date = mDate.getYear()+"年"+mDate.getMonth()+"月"+mDate.getDay()+"日";
        return date;
    }

}

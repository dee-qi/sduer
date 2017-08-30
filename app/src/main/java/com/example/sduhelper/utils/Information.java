package com.example.sduhelper.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * This is sduer
 * Created by qidi on 2017/7/26.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class Information {
    private static final String TAG = "Information";

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
        int currentWeekOfTerm = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.set(beginningYear,beginningMonth,beginningDay);
        int beginningWeekOfTerm =calendar.get(Calendar.WEEK_OF_YEAR);

        int weekOfTerm = currentWeekOfTerm - beginningWeekOfTerm;

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

}

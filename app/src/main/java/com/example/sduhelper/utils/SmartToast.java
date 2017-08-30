package com.example.sduhelper.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * This is sduer
 * Created by qidi on 2017/7/21.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

//该工具类用于解决多个Toast被连续触发时显示时间过长的问题
public class SmartToast {
    private static Toast mToast;

    public static void make(Context context,String content){
        if(mToast == null){
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }
        mToast.show();

    }
}

package com.example.sduhelper.utils;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

/**
 * This is sduer
 * Created by qidi on 2017/8/28.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class ApiUtil {
    //用于从配置文件“appConfig”中获取接口信息
    public static String getApi(Context c,String key){
        Properties props = new Properties();
        try {
            InputStream in = c.getAssets().open("appConfig");
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return props.getProperty(key);
    }
}

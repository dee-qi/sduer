package com.example.sduhelper.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * This is sduer
 * Created by qidi on 2017/7/19.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class NetWorkUtil {

    //检查网络连接
    public static boolean isNetWorkAvailable(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null && info.isAvailable()){
            return true;
        } else {
            return false;
        }
    }

    //get method without a header
    public static void get(String url,
                           Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //get method with a header
    public static void get(String url,
                           String token,
                           Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization",token)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //post method without a header
    public static void post(String url,
                            Map<String, String> params,
                            Callback callback){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder mBuilder = new FormBody.Builder();
        RequestBody body;
        for (String key : params.keySet()) {
            mBuilder.add(key, params.get(key));
        }
        body = mBuilder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    //post method with a header
    public static void post(String url,
                            Map<String, String> params,
                            String token,
                            Callback callback){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder mBuilder = new FormBody.Builder();
        RequestBody body;
        for (String key : params.keySet()) {
            mBuilder.add(key, params.get(key));
        }
        body = mBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .header("X-Authorization",token)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
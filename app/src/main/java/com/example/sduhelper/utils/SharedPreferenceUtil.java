package com.example.sduhelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is sduer
 * Created by qidi on 2017/7/25.
 * Tel:18340018130
 * E-mail:sevenddddddd@gmail.com
 */

public class SharedPreferenceUtil {

    private static String Object2String(Object o){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try{
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            String s = new String(Base64.encode(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
            objectOutputStream.close();
            return s;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Object String2Object(String s){
        byte[] bytes = Base64.decode(s.getBytes(),Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try{
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object o = objectInputStream.readObject();
            objectInputStream.close();
            return o;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void saveObj(Context context, String fileKey, String key, Object o){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String s = Object2String(o);
        editor.putString(key,s);
        editor.commit();
    }

    public static void save(Context context, String fileKey, String key, String s){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,s);
        editor.commit();
    }

    public static Object getObj(Context context, String fileKey, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey,Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(key,"");
        if(!s.equals("")){
            return String2Object(s);
        } else {
            return null;
        }
    }

    public static String get(Context context, String fileKey, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey,Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(key,"");
        return s;
    }

    public static void clear(Context context,String fileKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey,Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            sharedPreferences.edit().clear().commit();
        }
    }
}

package com.example.sduhelper.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static List<Activity> list = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.add(this);
    }

    public void finishALl(){
        for(Activity activity : list){
            activity.finish();
        }
    }
}

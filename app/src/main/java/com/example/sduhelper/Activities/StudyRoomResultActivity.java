package com.example.sduhelper.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.sduhelper.Adapters.RoomResultAdapter;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StudyRoomResultActivity extends AppCompatActivity {
    private static final String TAG = "@aaaroom";
    String campus,building;
    Toolbar toolbar;
    RecyclerView resultList;
    ProgressDialog queryDialog;
    private List dataList;
    int mYear = Information.getYear();
    int mMonth = Information.getMonth();
    int mDay = Information.getDay();
    String weekCount;
    FloatingActionButton fab;

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mYear = year;
            mMonth = month+1;
            mDay = dayOfMonth;
            updateToolbar();
            loadData();
        }
    };
    private final int LOAD_SUCCEED = 1;
    private final int LOAD_FAILED = 2;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            queryDialog.dismiss();
            switch (msg.what){
                case LOAD_SUCCEED:
                    SmartToast.make(StudyRoomResultActivity.this,"点击右下角悬浮按钮可选择查询日期");
                    RoomResultAdapter adapter = new RoomResultAdapter(dataList);
                    resultList.setAdapter(adapter);
                    break;
                case LOAD_FAILED:
                    SmartToast.make(StudyRoomResultActivity.this,"加载失败");
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room_result);
        initToolbar(mYear+"年"+mMonth+"月"+mDay+"日 "+Information.getWeekday(mYear,mMonth,mDay));

        Intent intent = getIntent();
        campus = intent.getStringExtra("campus");
        building = intent.getStringExtra("building");

        fab = (FloatingActionButton)findViewById(R.id.studyroom_result_timepicker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StudyRoomResultActivity.this,onDateSetListener,
                        mYear,mMonth-1,mDay).show();
            }
        });
        resultList = (RecyclerView)findViewById(R.id.studyroom_result_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        resultList.setLayoutManager(manager);
        loadData();
    }



    private void loadData(){
        queryDialog = new ProgressDialog(StudyRoomResultActivity.this);
        queryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        queryDialog.setMessage("正在查询");
        queryDialog.setIndeterminate(true);
        queryDialog.setCancelable(false);
        queryDialog.show();
        String oriUrl = ApiUtil.getApi(StudyRoomResultActivity.this,"api_study_room");
        String url = String.format(oriUrl,campus,building,mYear+"-"+mMonth+"-"+mDay);
        Log.d("@room", "loadData: url:"+url);
        NetWorkUtil.get(url, new Callback() {
            Message msg;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                Log.d(TAG, "onFailure: "+e.getMessage());
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                dataList = new LinkedList();
                String s = response.body().string();
                Log.d("@room", "onResponse: "+s);
                try {
                    JSONArray array = new JSONArray(s);
                    JSONObject o = null;
                    for(int i=0; i<array.length(); i++){
                        o = array.getJSONObject(i);
                        StringBuilder builder = new StringBuilder();
                        JSONObject oo = o.getJSONObject("status");
                        Iterator iterator = oo.keys();
                        while(iterator.hasNext()){
                            String current = (String)iterator.next();
                            if(oo.getString(current).equals("空闲")){
                                builder.append("0");
                            } else if(oo.getString(current).equals("上课")){
                                builder.append("1");
                            } else builder.append("2");
                        }
                        builder.append(o.getString("classroom"));
                        Log.d(TAG, "onResponse: "+builder.toString());
                        dataList.add(builder.toString());
                    }
                    msg.what = LOAD_SUCCEED;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = LOAD_FAILED;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        });

    }

    private void initToolbar(String title){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back_white_36dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateToolbar(){
        toolbar.setTitle(mYear+"年"+mMonth+"月"+mDay+"日 "+
        Information.getWeekday(mYear,mMonth,mDay));
    }
}

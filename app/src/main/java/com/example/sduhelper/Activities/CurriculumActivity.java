package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.sduhelper.Adapters.CurriculumAdapter;
import com.example.sduhelper.Items.ItemCurriculum;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CurriculumActivity extends BaseActivity {
    private static final String TAG = "CurriculumActivity";

    ArrayList<ItemCurriculum> list;
    private GridView gridLayout;
    private TextView weekCount;
    private TextView refresh;
    private ProgressDialog queryDialog;

    private final int LOAD_FAILED = 0x999;
    private final int LOAD_SUCCEED = 0x998;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            queryDialog.dismiss();
            switch (msg.what){
                case LOAD_FAILED :
                    SmartToast.make(CurriculumActivity.this,"加载课程失败！");
                    break;
                case LOAD_SUCCEED :

                    CurriculumAdapter adapter = new CurriculumAdapter(CurriculumActivity.this,list,weekCount);
                    gridLayout.setAdapter(adapter);
                    setGridViewHeightBasedOnItems(gridLayout);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculum);

        initToolbar("我的课表");

        weekCount = (TextView)findViewById(R.id.curriculum_week_count);
        refresh = (TextView)findViewById(R.id.curriculum_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceUtil.clear(v.getContext(),"curriculum");
                getCurriculum();
            }
        });
        gridLayout = (GridView)findViewById(R.id.curriculum_grid);

        getCurriculum();
    }

    private void getCurriculum(){
        //正在加载对话框
        queryDialog = new ProgressDialog(CurriculumActivity.this);
        queryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        queryDialog.setMessage("正在加载");
        queryDialog.setIndeterminate(true);
        queryDialog.setCancelable(false);//不可取消
        queryDialog.show();

        //如果本地没有存储，就从服务器获取
        if(SharedPreferenceUtil.getObj(this,"curriculum","list") == null) {
            list = new ArrayList<>();//用于存放ItemCurriculum的List

            NetWorkUtil.get(ApiUtil.getApi(CurriculumActivity.this,"api_academic_table")+SharedPreferenceUtil.get(CurriculumActivity.this, "userInfo", "id"),
                    SharedPreferenceUtil.get(CurriculumActivity.this, "userInfo", "token"),
                    new Callback() {
                        Message msg ;
                        @Override
                        public void onFailure(Call call, IOException e) {
                            msg = new Message();
                            msg.what = LOAD_FAILED;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            msg = new Message();
                            String s = response.body().string();
                            Log.d(TAG, "onResponse: "+s);
                            try {
                                if ((new JSONObject(s)).getInt("code") == 0) {
                                    msg.what = LOAD_SUCCEED;
                                    //初始化一个长度为35的List
                                    for(int i=0; i<35; i++){
                                        list.add(new ItemCurriculum(false,"","","",""));
                                    }

                                    JSONArray array = new JSONObject(s).getJSONArray("obj");
                                    for(int i=0; i<array.length(); i++){
                                        Log.d(TAG, "onResponse: array length is :"+array.length());
                                        JSONObject obj = array.getJSONObject(i);
                                        int id = obj.getInt("weekday")+obj.getInt("courseOrder")*7-8;
                                        ItemCurriculum item = new ItemCurriculum(true,
                                                obj.getString("courseName"),
                                                obj.getString("teacher"),
                                                obj.getString("room"),
                                                obj.getString("week"));
                                        item.setWeekdayAndOrder(obj.getInt("weekday"),
                                                obj.getInt("courseOrder"));
                                        list.set(id,item);
                                    }
                                    //存储课表
                                    SharedPreferenceUtil.saveObj(CurriculumActivity.this,"curriculum","list",list);
                                }
                            } catch (JSONException e){
                                msg.what = LOAD_FAILED;
                                e.printStackTrace();
                            } finally {
                                handler.sendMessage(msg);
                            }
                        }
                    });
        } else {
            Message msg = new Message();
            msg.what = LOAD_SUCCEED;
            list = (ArrayList<ItemCurriculum>)SharedPreferenceUtil.getObj(this,"curriculum","list");
            if(list.size() == 0)
                SmartToast.make(this,"课程为空!");
            handler.sendMessage(msg);
        }
    }

    private void setGridViewHeightBasedOnItems(GridView gridView){
        ListAdapter adapter = gridView.getAdapter();
        if(adapter != null){
            int total = 5;
            View v = adapter.getView(0,null,gridView);
            v.measure(0,View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = total*v.getMeasuredHeight();
            gridView.setLayoutParams(params);
        }
    }

    private void initToolbar(String title){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
}

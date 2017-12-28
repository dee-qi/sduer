package com.example.sduhelper.Activities;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ExamActivity extends BaseActivity {
    private static final String TAG = "ExamActivity";
    private LinearLayout itemContainer;
    private CardView examItem;
    private TextView courseName;
    private TextView courseId;
    private TextView location;
    private TextView time;
    private TextView method;
    private TextView examTips;

    List<Map<String,String>> itemList;

    private final int LOAD_SUCCEED = 0x123;
    private final int LOAD_FAILED = 0x124;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_SUCCEED:
                    examTips.setText("已加载全部");
                    for(int i = 0;i < itemList.size();i++) {
                        Map map = itemList.get(i);
                        examItem = (CardView) getLayoutInflater().inflate(R.layout.item_exam_result, new CardView(ExamActivity.this));
                        courseName = (TextView) examItem.findViewById(R.id.exam_course_name);
                        courseName.setText((String)map.get("name"));
                        courseId = (TextView) examItem.findViewById(R.id.exam_type);
                        courseId.setText((String)map.get("type"));
                        location = (TextView) examItem.findViewById(R.id.exam_location);
                        location.setText((String)map.get("location"));
                        time = (TextView) examItem.findViewById(R.id.exam_time);
                        time.setText((String)map.get("time"));
                        method = (TextView) examItem.findViewById(R.id.exam_method);
                        method.setText((String)map.get("method"));
                        itemContainer.addView(examItem);
                    }
                    if(itemList.size() == 0){
                        examTips.setText("暂无考试信息");
                    }
                    break;
                case LOAD_FAILED :
                    examTips.setText("加载失败");
                    SmartToast.make(ExamActivity.this,"加载考试信息失败！请检查网络！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initToolbar("考试安排");
        itemContainer = (LinearLayout) findViewById(R.id.exam_item_container);
        examTips = (TextView)findViewById(R.id.exam_tips);

        loadData();
        Log.d("@here@", "api is: "+ ApiUtil.getApi(ExamActivity.this,"api_oauth_app"));

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

    private void loadData(){
//        if(Information.isOnTrial){
//            onTial();
//            return;
//        }

        examTips.setText("正在加载中！");
        String url = ApiUtil.getApi(ExamActivity.this,"api_academic_schedule")+ SharedPreferenceUtil.get(ExamActivity.this,"userInfo","id");
        String token = SharedPreferenceUtil.get(ExamActivity.this,"userInfo","token");
        NetWorkUtil.get(url, token, new Callback() {
            Message msg;
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
                try{
                    JSONObject obj = new JSONObject(s);
                    if(obj.getInt("code") == 0){
                        msg.what = LOAD_SUCCEED;
                        itemList = new LinkedList<>();
                        JSONObject examSet = obj.getJSONObject("obj");
                        Iterator<String> examNameSet= examSet.keys();
                        while(examNameSet.hasNext()){
                            String currentExamName = examNameSet.next();
                            JSONArray array = examSet.getJSONArray(currentExamName);
                            for(int i=0; i<array.length(); i++){
                                JSONObject o = array.getJSONObject(i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("name",o.getString("courseName"));
                                map.put("type",currentExamName);
                                map.put("location",o.getString("examRoom"));
                                map.put("time",o.getString("examDate")+"  "+o.getString("examTime"));
                                map.put("method",o.getString("examMethod"));
                                itemList.add(map);
                            }
                        }
                    } else {
                        msg.what = LOAD_FAILED;
                    }
                } catch (JSONException e){
                    Log.d(TAG, "onResponse: "+e.toString());
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(msg);
                }

            }
        });

    }

//    private void onTial(){
//        //一会删了
//        itemList = new LinkedList<>();
//        Message msg = new Message();
//        for(int i = 0; i < 10; i ++){
//            Map<String,String> map = new HashMap<>();
//            map.put("name","测试"+ i);
//            map.put("type","期末考试");
//            map.put("location","明德楼"+i+"区123");
//            map.put("time","2017年8月22日");
//            map.put("method","笔试");
//            itemList.add(map);
//        }
//        msg.what = LOAD_SUCCEED;
//        handler.sendMessage(msg);
//    }
}

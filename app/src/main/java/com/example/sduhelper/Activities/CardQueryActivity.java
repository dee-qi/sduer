package com.example.sduhelper.Activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.Information;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CardQueryActivity extends AppCompatActivity {

    private Spinner spinner;
    private ListView listView;
    private TextView loading;

    ArrayList<Map<String,String>> itemList;

    private String QUERY_DAY = "查询当日消费记录";
    private String QUERY_WEEK = "查询一周内消费记录";
    private String QUERY_MONTH = "查询三个月内消费记录";

    private final int LOAD_SUCCEED = 0x111;
    private final int LOAD_FAILED = 0x112;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_SUCCEED:
                    loading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    SimpleAdapter adapter = new SimpleAdapter(CardQueryActivity.this,itemList,
                            R.layout.item_card_history,
                            new String[]{"time","location","out","balance"},
                            new int[]{R.id.item_card_history_time,R.id.item_card_history_location
                                    ,R.id.item_card_history_out,R.id.item_card_history_balance});
                    listView.setAdapter(adapter);
                    break;
                case LOAD_FAILED:
                    listView.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    loading.setText("加载失败！");
                    SmartToast.make(CardQueryActivity.this,"加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_query);
        initToolbar("流水查询");

        loading = (TextView)findViewById(R.id.schoolcard_query_loading);
        spinner = (Spinner)findViewById(R.id.schoolcard_query_select);
        listView = (ListView)findViewById(R.id.schoolcard_query_result);
        String[] s = new String[]{QUERY_DAY, QUERY_WEEK, QUERY_MONTH};
        ArrayAdapter spadapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,s);
        spinner.setAdapter(spadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String api,url;
                switch (position){
                    case 0:
                        api = ApiUtil.getApi(CardQueryActivity.this,"api_school_card_history_today");
                        url = String.format(api,
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","cardNum"),
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","pwd"));
                        loadData(url);
                        break;
                    case 1:
                        api = ApiUtil.getApi(CardQueryActivity.this,"api_school_card_history_week");
                        url = String.format(api,
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","cardNum"),
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","pwd"));
                        loadData(url);
                        break;
                    case 2:
                        api = ApiUtil.getApi(CardQueryActivity.this,"api_school_card_history_month");
                        url = String.format(api,
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","cardNum"),
                                SharedPreferenceUtil.get(CardQueryActivity.this,"userInfo","pwd"));
                        loadData(url);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //I don't want to code here :D
            }
        });
    }

    private void loadData(String url){
        loading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        NetWorkUtil.get(url, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                itemList = new ArrayList<Map<String, String>>();
                String s = response.body().string();
                try {
                    JSONArray array = new JSONArray(s);
                    for(int i = 0; i< array.length(); i++){
                        JSONArray item = array.getJSONArray(i);
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("time","消费时间："+item.getString(0));
                        map.put("location","消费地点："+item.getString(1));
                        map.put("out","支出金额："+item.getString(3));
                        map.put("balance","卡内余额："+item.getString(4));
                        itemList.add(map);
                    }
                    msg.what = LOAD_SUCCEED;
                    handler.sendMessage(msg);
                } catch (JSONException e){
                    msg.what = LOAD_FAILED;
                    handler.sendMessage(msg);
                }
            }
        });
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

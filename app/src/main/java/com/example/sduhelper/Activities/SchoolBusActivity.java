package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.BaseActivity;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//写这部分的时候不知道Handler是啥。。。于是乎自己写了很奇葩的多线程
//后来看的大佬莫笑

public class SchoolBusActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SchoolBusActivity";
    private ProgressDialog queryDialog;
    private Spinner busStart;
    private Spinner busEnd;
    private Button busQuery;
    private ImageView exchange;
    private RadioButton weekday;
    private RadioButton weekend;

    SimpleAdapter resultAdapter;
    List<Map<String,String>> dataList;
    ListView resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_bus);

        initToolbar("校车查询");
        busStart = (Spinner)findViewById(R.id.bus_start);
        busEnd = (Spinner)findViewById(R.id.bus_end);
        busQuery = (Button)findViewById(R.id.bus_query);
        busQuery.setOnClickListener(this);
        exchange = (ImageView)findViewById(R.id.bus_exchange);
        exchange.setOnClickListener(this);
        resultList = (ListView)findViewById(R.id.result_list);
        resultList.setEnabled(false);
        weekday = (RadioButton)findViewById(R.id.bus_radio_weekday);
        weekend = (RadioButton)findViewById(R.id.bus_radio_weekend);
        weekday.setChecked(true);

        //Spinner Adapter
        String[] campus = {"中心校区","软件园校区","洪家楼校区","千佛山校区","兴隆山校区","趵突泉校区"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,campus);
        busStart.setAdapter(spinnerAdapter);
        busEnd.setAdapter(spinnerAdapter);

        //Spinner监听，用于改变Button颜色
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!busStart.getSelectedItem().toString().equals(busEnd.getSelectedItem().toString())){
                    busQuery.setBackgroundColor(getResources().getColor(R.color.themeColor));
                } else {
                    busQuery.setBackgroundColor(getResources().getColor(R.color.button_default));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty
            }
        };

        busStart.setOnItemSelectedListener(onItemSelectedListener);
        busEnd.setOnItemSelectedListener(onItemSelectedListener);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //查询数据
            case R.id.bus_query :
                if(busStart.getSelectedItem().toString().equals(busEnd.getSelectedItem().toString())){
//                    Toast.makeText(this, "请选择不同的始发地和目的地！", Toast.LENGTH_SHORT).show();
                    SmartToast.make(this,"请选择不同的始发地和目的地");
                } else {
                    queryDialog = new ProgressDialog(SchoolBusActivity.this);
                    queryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    queryDialog.setMessage("正在查询");
                    queryDialog.setIndeterminate(true);
                    queryDialog.setCancelable(false);
                    queryDialog.show();
                    requestBusResult();
                }
                break;
            //交换目的地和始发地
            case R.id.bus_exchange :
                int i = busStart.getSelectedItemPosition();
                busStart.setSelection(busEnd.getSelectedItemPosition());
                busEnd.setSelection(i);
        }
    }


    private void  requestBusResult() {
        dataList = new LinkedList<>();
        resultList.setAdapter(null);//清空ListView数据

        OkHttpClient client = new OkHttpClient();

        String url = buildBusUrl(busStart.getSelectedItem().toString(), busEnd.getSelectedItem().toString(),
                weekend.isChecked());
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                queryDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SchoolBusActivity.this, "网络故障！请检查网络连接后重试！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private boolean isBusDataAvailable = true;

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                queryDialog.dismiss();
                try {
                    String responseData = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseData);

                    if (jsonArray.length() == 0) {
                        isBusDataAvailable = false;
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String,String> map = new HashMap<String, String>();
                        JSONObject object = jsonArray.getJSONObject(i);
                        map.put("s","始 "+object.getString("s"));
                        map.put("e","终 "+object.getString("e"));
                        map.put("t",object.getString("t"));
                        map.put("p","经 "+object.getString("p"));
                        dataList.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultAdapter = new SimpleAdapter(SchoolBusActivity.this,dataList,R.layout.item_bus_result,
                        new String[]{"s","e","t","p"},new int[]{R.id.item_bus_result_start,R.id.item_bus_result_end,
                R.id.item_bus_result_time,R.id.item_bus_result_pass});
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isBusDataAvailable) {
                            resultList.setAdapter(resultAdapter);
                            setListViewHeightBasedOnItems(resultList);//根据ListView的item个数调整ListView的高度
                        } else {
                            Toast.makeText(SchoolBusActivity.this, "所选校区之间没有班车！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

//        Thread requestThread = new Thread(new  Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                String url = buildBusUrl(busStart.getSelectedItem().toString(), busEnd.getSelectedItem().toString(),
//                        weekend.isChecked());
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        queryDialog.dismiss();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(SchoolBusActivity.this, "网络故障！请检查网络连接后重试！", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                    private boolean isBusDataAvailable = true;
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        queryDialog.dismiss();
//                        try {
//                            String responseData = response.body().string();
//                            Log.d(TAG, "onResponse: " + responseData);
//                            JSONArray jsonArray = new JSONArray(responseData);
//                            Log.d(TAG, "onResponse: array length:" + jsonArray.length());
//                            queryResults = new String[jsonArray.length()];
//                            if (queryResults.length == 0) {
//                                isBusDataAvailable = false;
//                            }
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject object = jsonArray.getJSONObject(i);
//                                String s = object.getString("s");
//                                String e = object.getString("e");
//                                String t = object.getString("t");
//                                String item = t + "\n" + "从" + s + " 到 " + e;
//                                queryResults[i] = item;
//                                Log.d(TAG, "onResponse: item" + i + ": " + item);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d(TAG, "onResponse: error");
//                        }
//                        resultAdapter = new ArrayAdapter(getApplicationContext(), R.layout.item_bus_result, queryResults);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (isBusDataAvailable) {
//                                    resultList.setAdapter(resultAdapter);
//                                    setListViewHeightBasedOnItems(resultList);//根据ListView的item个数调整ListView的高度
//                                } else {
//                                    Toast.makeText(SchoolBusActivity.this, "所选校区之间没有班车！", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });
//        requestThread.start();

    //根据ListView的item个数调整ListView的高度
    private void setListViewHeightBasedOnItems(ListView listView){
        ListAdapter adapter = listView.getAdapter();
        if(adapter != null){
            int total = adapter.getCount();
            View v = adapter.getView(0,null,listView);
            v.measure(0,View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = total*v.getMeasuredHeight();
            listView.setLayoutParams(params);
        }
    }

    //生成请求的url
    private String buildBusUrl(String start, String end,boolean isWeekend){
        return String.format(ApiUtil.getApi(SchoolBusActivity.this,"api_school_bus"),start,end,isWeekend?"1":"0");
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

package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sduhelper.Adapters.LibQueryAdapter;
import com.example.sduhelper.Items.LibQueryItem;
import com.example.sduhelper.R;
import com.example.sduhelper.utils.ApiUtil;
import com.example.sduhelper.utils.NetWorkUtil;
import com.example.sduhelper.utils.SharedPreferenceUtil;
import com.example.sduhelper.utils.SmartToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LibraryQueryActivity extends AppCompatActivity {
    private static final String TAG = "LibraryQueryActivity";

    private ListView queryList;
    private List<LibQueryItem> itemList;
    private ProgressDialog dia;
    private TextView nothing;

    private final int LOAD_SUCCEED = 0x123;
    private final int LOAD_FAILED = 0x124;
    private final int LOAD_FAILED_NOTHING = 0x125;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dia.dismiss();
            if(msg.what == LOAD_SUCCEED){
               LibQueryAdapter adapter = new LibQueryAdapter(LibraryQueryActivity.this,R.layout.item_query,itemList);
                queryList.setAdapter(adapter);
            } else if(msg.what == LOAD_FAILED_NOTHING){
                queryList.setVisibility(View.GONE);
                nothing.setVisibility(View.VISIBLE);
                SmartToast.make(LibraryQueryActivity.this,"无结果");
            } else {
                SmartToast.make(LibraryQueryActivity.this,"加载失败");
                LibraryQueryActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_query);

        initToolbar("查询结果");

        queryList = (ListView)findViewById(R.id.library_query_list);
        nothing = (TextView)findViewById(R.id.library_query_none);

        Intent queryIntent = getIntent();
        loadData(queryIntent.getStringExtra("queryText"));
    }

    private void loadData(String query){

        dia = new ProgressDialog(LibraryQueryActivity.this);
        dia.setIndeterminate(true);
        dia.setMessage("正在查询");
        dia.setCancelable(false);
        dia.show();

        String url = ApiUtil.getApi(LibraryQueryActivity.this,"api_lib_search");
        String token = SharedPreferenceUtil.get(LibraryQueryActivity.this,"userInfo","token");
        Map<String,String> map = new HashMap<>();
        map.put("name",query);
        NetWorkUtil.post(url, map, token, new Callback() {
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
                    JSONObject o = new JSONObject(s);
                    if(o.getInt("code") == 0){
                        msg.what = LOAD_SUCCEED;
                        itemList = new LinkedList<LibQueryItem>();
                        JSONArray bookArray = o.getJSONArray("obj");
                        for(int i=0; i<bookArray.length(); i++){
                            JSONObject book = bookArray.getJSONObject(i);
                            String name = book.getString("name");
                            String author = book.getString("author");
                            String press = book.getString("press").replace("&nbsp;","  ");
                            String code = book.getString("code");
                            JSONArray locationsArray = book.getJSONArray("books");
                            List<String> list = new LinkedList<String>();
                            for(int j=0; j<locationsArray.length(); j++){
                                list.add(locationsArray.getString(j));
                            }
                            LibQueryItem item = new LibQueryItem(name,author,press,code,list);
                            itemList.add(item);
                        }
                    } else if(o.getString("msg").contains("没有查找到")){
                        msg.what = LOAD_FAILED_NOTHING;
                    } else msg.what = LOAD_FAILED;
                }catch (JSONException e){
                    e.printStackTrace();
                } finally {
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

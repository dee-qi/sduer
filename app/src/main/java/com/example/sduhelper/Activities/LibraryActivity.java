package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sduhelper.Adapters.BookBorrowedAdapter;
import com.example.sduhelper.Items.BookBorrowedItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LibraryActivity extends BaseActivity {
    private static final String TAG = "LibraryActivity";
    private RecyclerView recyclerView;
    private List<BookBorrowedItem> bookList;
    private SearchView searchView;
    private TextView gone;

    private ProgressDialog dia;

    private final int BIND_SUCCEED = 0x001;
    private final int BIND_FAILED = 0x002;
    private final int LOAD_SUCCEED_AVAILABLE = 0x003;
    private final int LOAD_SUCCEED_UNAVAILABLE = 0x005;
    private final int LOAD_FAILED = 0x004;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dia.dismiss();
            switch (msg.what){
                case BIND_SUCCEED:
                    SharedPreferenceUtil.save(LibraryActivity.this,"userInfo","isLibraryBound","true");
                    SmartToast.make(LibraryActivity.this,"绑定成功！");
                    loadData();
                    break;
                case BIND_FAILED:
                    SmartToast.make(LibraryActivity.this,"绑定失败！请重新进入图书馆功能重试！");
                    LibraryActivity.this.finish();
                    break;
                case LOAD_SUCCEED_AVAILABLE:
                    BookBorrowedAdapter adapter = new BookBorrowedAdapter(bookList);
                    recyclerView.setAdapter(adapter);
                    break;
                case LOAD_SUCCEED_UNAVAILABLE:
                    recyclerView.setVisibility(View.GONE);
                    gone.setVisibility(View.VISIBLE);
                    break;
                case LOAD_FAILED:
                    SmartToast.make(LibraryActivity.this,"加载失败！请检查网络设置");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        initToolbar("图书馆");

        searchView = (SearchView) findViewById(R.id.library_search);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("馆藏查询");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(LibraryActivity.this,LibraryQueryActivity.class);
                intent.putExtra("queryText", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.library_borrowed_list);
        gone = (TextView)findViewById(R.id.library_borrowed_gone);

        if(SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","isLibraryBound")
                .equals("false")||
                SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","cardNum")
                        .equals("false")){
            final EditText cardId = new EditText(this);
            cardId.setHint("校园卡账号（不是学号）");
            cardId.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            final EditText pwd = new EditText(this);
            pwd.setHint("密码");
            pwd.setInputType(EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD);
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.addView(cardId);
            container.addView(pwd);

            new AlertDialog.Builder(this)
                    .setTitle("请先绑定校园卡账号和密码！")
                    .setView(container)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bindLibrary(cardId.getText().toString(),pwd.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LibraryActivity.this.finish();
                        }
                    })
                    .show();
        } else {
            loadData();
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    private void loadData(){
        dia  = new ProgressDialog(this);
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setMessage("数据加载中");
        dia.setIndeterminate(true);
        dia.setCancelable(false);
        dia.show();
        bookList = new ArrayList<>();
        String url = ApiUtil.getApi(LibraryActivity.this,"api_lib_borrowed")
                +SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","id");
        String token = SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","token");
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
                    JSONObject o = new JSONObject(s);
                    if(o.getInt("code") == 0){
                        msg.what = LOAD_SUCCEED_AVAILABLE;
                        JSONArray array = o.getJSONArray("obj");
                        for(int i=0; i<array.length(); i++){
                            JSONObject book = array.getJSONObject(i);
                            BookBorrowedItem item = new BookBorrowedItem(
                                    book.getString("name"),
                                    book.getString("borDate"),
                                    book.getString("retDate"),
                                    book.getString("id"),
                                    book.getString("checkCode"));
                            bookList.add(item);
                        }
                    } else if(o.getString("msg").contains("未借阅")){
                        msg.what = LOAD_SUCCEED_UNAVAILABLE;
                    } else {
                        msg.what = LOAD_FAILED;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(msg);
                }
            }
        });

        //这是测试数据
//        for(int i = 0; i < 3; i++){
//            BookBorrowedItem item = new BookBorrowedItem("测试"+i, "2017/09/20", "2017/10/10","","");
//            bookList.add(item);
//        }

    }

    private void bindLibrary(String id,String pwd){
        dia  = new ProgressDialog(this);
        dia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dia.setMessage("绑定中，请稍候");
        dia.setIndeterminate(true);
        dia.setCancelable(false);
        dia.show();
        String url = ApiUtil.getApi(LibraryActivity.this,"api_lib_bind");
        Map<String,String> map = new HashMap<>();
        map.put("id",SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","id"));
        map.put("cardNo",id);
        map.put("pass",pwd);
        String token = SharedPreferenceUtil.get(LibraryActivity.this,"userInfo","token");
        NetWorkUtil.post(url, map, token, new Callback() {
            Message msg;
            @Override
            public void onFailure(Call call, IOException e) {
                msg = new Message();
                msg.what = BIND_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                msg = new Message();
                String s = response.body().string();
                Log.d(TAG, "onResponse: "+s);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getInt("code") == 0) {
                        msg.what = BIND_SUCCEED;
                    } else msg.what = BIND_FAILED;
                } catch (JSONException e){
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
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

package com.example.sduhelper.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SchoolCardActivity extends BaseActivity {
    private static final String TAG = "SchoolCardActivity";

    private TextView balance;
    private TextView transition;
    private TextView name;
    private TextView bank;
    private TextView lost;
    private TextView freeze;
    private TextView status;
    private SwipeRefreshLayout refreshLayout;
    private Map<String,String> mMap;

    private TextView recharge;

    private final int LOAD_SUCCEED = 0x111;
    private final int LOAD_FAILED = 0x112;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.setRefreshing(false);
            switch (msg.what){
                case LOAD_SUCCEED :
                    balance.setText(mMap.get("校园卡余额 "));
                    transition.setText("过渡余额："+mMap.get("当前过渡余额 ")+"元");
                    name.setText(mMap.get("姓名"));
                    bank.setText(mMap.get("银行卡号 "));
                    lost.setText(mMap.get("挂失状态 "));
                    freeze.setText(mMap.get("冻结状态 "));
                    status.setText(mMap.get("身份类型 "));
                    Log.d(TAG, "handleMessage: mMap is empty?"+mMap.isEmpty());
                    break;
                case LOAD_FAILED :
                    new AlertDialog.Builder(SchoolCardActivity.this)
                            .setTitle("获取信息失败")
                            .setMessage("可能是以下原因：\n" +
                                    "1.操作过于频繁（学校要求两小时操作不得超过5次），请两小时后再试\n" +
                                    "2.网络故障，请检查网络连接\n" +
                                    "3.服务器故障，请在问题反馈中联系我们")
                            .setNegativeButton("我知道了",null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    SchoolCardActivity.this.finish();
                                }
                            })
                            .show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_card);
        initToolbar("校园卡");

        balance = (TextView)findViewById(R.id.schoolcard_balance);
        transition = (TextView)findViewById(R.id.schoolcard_transition);
        name = (TextView)findViewById(R.id.schoolcard_name);
        bank = (TextView)findViewById(R.id.schoolcard_bank);
        lost = (TextView)findViewById(R.id.schoolcard_lost);
        freeze = (TextView)findViewById(R.id.schoolcard_freeze);
        status = (TextView)findViewById(R.id.schoolcard_status);
        recharge = (TextView)findViewById(R.id.schoolcard_recharge);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.schoolcard_refresh);

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshLayout.isRefreshing()){
                    SmartToast.make(SchoolCardActivity.this,"请等待信息获取完毕再进行操作！");
                } else {
                    Intent intent = new Intent(SchoolCardActivity.this, CardRechargeActivity.class);
                    startActivity(intent);
                }
            }
        });

        if(SharedPreferenceUtil.get(SchoolCardActivity.this,"userInfo","cardNum").equals("")){
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
                            loadData(cardId.getText().toString(),pwd.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SchoolCardActivity.this.finish();
                        }
                    })
                    .show();
        } else {
            loadData(SharedPreferenceUtil.get(SchoolCardActivity.this,"userInfo","cardNum"),
                    SharedPreferenceUtil.get(SchoolCardActivity.this,"userInfo","pwd"));
        }
    }

    private void loadData(final String cardId, final String pwd){
        refreshLayout.setRefreshing(true);
        String url = String.format(ApiUtil.getApi(SchoolCardActivity.this, "api_school_card_getInfo"),
                cardId,pwd);
//            String url = String.format(ApiUtil.getApi(SchoolCardActivity.this,"api_school_card_getInfo"),
//                    "268210","@@@@");
        NetWorkUtil.get(url, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
                Log.d(TAG, "onFailure: IN schoolCard :"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                try{
                    Log.d(TAG, "onResponse: schoolcard "+s);
                    JSONArray array = new JSONArray(s);
                    mMap = new HashMap<String, String>();
                    for(int i=0; i<array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        mMap.put(obj.getString("name"),obj.getString("value"));
                        Log.d(TAG, "put in map :"+obj.getString("name"));
                    }
                    msg.what = LOAD_SUCCEED;
                    SharedPreferenceUtil.save(SchoolCardActivity.this,"userInfo","cardNum",cardId);
                    SharedPreferenceUtil.save(SchoolCardActivity.this,"userInfo","pwd",pwd);
                } catch (JSONException e){
                    msg.what = LOAD_FAILED;
                    Log.d(TAG, "onResponse: "+e.getMessage());
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

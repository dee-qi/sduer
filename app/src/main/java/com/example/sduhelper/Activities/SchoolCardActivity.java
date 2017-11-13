package com.example.sduhelper.Activities;

import android.app.ProgressDialog;
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
    private ProgressDialog pd;

    private TextView recharge;
    private TextView history;

    private final int BIND_SUCCEED = 0x100;
    private final int BIND_FAILED = 0x155;
    private final int BIND_INCORRECT = 0x222;
    private final int LOAD_SUCCEED = 0x111;
    private final int LOAD_FAILED = 0x112;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            refreshLayout.setRefreshing(false);
            switch (msg.what){
                case BIND_SUCCEED:
                    pd.dismiss();
                    SmartToast.make(SchoolCardActivity.this, "绑定成功");
                    SharedPreferenceUtil.save(SchoolCardActivity.this, "userInfo", "isCardBound", "true");
                    loadData();
                    break;
                case BIND_INCORRECT:
                    pd.dismiss();
                    new AlertDialog.Builder(SchoolCardActivity.this)
                            .setTitle("绑定失败")
                            .setMessage("请输入正确的校园卡号和密码。")
                            .setNegativeButton("我知道了",null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    SchoolCardActivity.this.finish();
                                }
                            })
                            .show();
                    break;
                case BIND_FAILED:
                    pd.dismiss();
                    new AlertDialog.Builder(SchoolCardActivity.this)
                            .setTitle("绑定失败")
                            .setMessage("请检查网络连接或者在意见反馈中联系我们。")
                            .setNegativeButton("我知道了",null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    SchoolCardActivity.this.finish();
                                }
                            })
                            .show();
                    break;
                case LOAD_SUCCEED :
                    balance.setText(mMap.get("校园卡余额"));
                    transition.setText("过渡余额："+mMap.get("当前过渡余额")+"元");
                    name.setText(mMap.get("姓名"));
                    bank.setText(mMap.get("银行卡号"));
                    lost.setText(mMap.get("挂失状态"));
                    freeze.setText(mMap.get("冻结状态"));
                    status.setText(mMap.get("身份类型"));
                    break;
                case LOAD_FAILED :
                    new AlertDialog.Builder(SchoolCardActivity.this)
                            .setTitle("获取信息失败")
                            .setMessage("请检查网络设置或者在问题反馈中联系我们。")
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
        history = (TextView)findViewById(R.id.schoolcard_history);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.schoolcard_refresh);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

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

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartToast.make(SchoolCardActivity.this, "功能维护中");
//                if(refreshLayout.isRefreshing()){
//                    SmartToast.make(SchoolCardActivity.this,"请等待信息获取完毕再进行操作！");
//                } else {
//                    Intent intent = new Intent(SchoolCardActivity.this, CardQueryActivity.class);
//                    startActivity(intent);
//                }
            }
        });

        if(SharedPreferenceUtil.get(SchoolCardActivity.this,"userInfo","isCardBound").equals("false")){
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
                            bindCard(cardId.getText().toString(), pwd.getText().toString());
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
            loadData();
        }
    }

    private void bindCard(String cardNo, String pwd){
        pd = new ProgressDialog(SchoolCardActivity.this);
        pd.setMessage("绑定中");
        pd.show();
        String url = ApiUtil.getApi(SchoolCardActivity.this, "api_school_card_bind");
        String token = SharedPreferenceUtil.get(SchoolCardActivity.this, "userInfo", "token");
        Map<String, String> map = new HashMap<String, String>();
        map.put("cardNo",cardNo);
        map.put("password", pwd);
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
                if(s.contains("成功")) msg.what = BIND_SUCCEED;
                else if(s.contains("失败")) msg.what = BIND_INCORRECT;
                else msg.what = BIND_FAILED;

                handler.sendMessage(msg);
            }
        });
    }
    private void loadData(){
        refreshLayout.setRefreshing(true);
        String url = ApiUtil.getApi(SchoolCardActivity.this, "api_school_card_getInfo");
        String token = SharedPreferenceUtil.get(SchoolCardActivity.this, "userInfo", "token");
        NetWorkUtil.get(url, token, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                msg.what = LOAD_FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d(TAG, "onResponse: "+s);
                try{
                    JSONArray array = new JSONObject(s).getJSONArray("obj");
                    mMap = new HashMap<String, String>();
                    for(int i=0; i<array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        mMap.put(obj.getString("key"),obj.getString("value"));
                    }
                    msg.what = LOAD_SUCCEED;
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
